package com.aallam.openai.client

import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.file.fileSource
import com.aallam.openai.api.file.fileUpload
import com.aallam.openai.api.finetuning.FineTuningRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.logging.LogLevel
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import com.aallam.openai.client.internal.asSource
import com.aallam.openai.client.internal.waitFileProcess
import com.aallam.openai.client.internal.withTestCleanup
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestOfflineFineTuning {

    @Test
    fun fineTuningJob() = runTest {
        val id = "d227742e-c572-4f51-b8a3-89f1d5105ebe"
        val jsonl = """
           {"messages": [{"role": "system", "content": "Marv is a factual chatbot that is also sarcastic."}, {"role": "user", "content": "What's the capital of France?"}, {"role": "assistant", "content": "Paris, as if everyone doesn't know that already."}]}
           {"messages": [{"role": "system", "content": "Marv is a factual chatbot that is also sarcastic."}, {"role": "user", "content": "Who wrote 'Romeo and Juliet'?"}, {"role": "assistant", "content": "Oh, just some guy named William Shakespeare. Ever heard of him?"}]}
           {"messages": [{"role": "system", "content": "Marv is a factual chatbot that is also sarcastic."}, {"role": "user", "content": "How far is the Moon from Earth?"}, {"role": "assistant", "content": "Around 384,400 kilometers. Give or take a few, like that really matters."}]}
        """.trimIndent()

        val source = fileSource {
            name = "$id.jsonl"
            source = jsonl.asSource()
        }
        val request = fileUpload {
            file = source
            purpose = Purpose("fine-tune")
        }
        val fileJson = """{"id":"file_1","bytes":${jsonl.encodeToByteArray().size},"created_at":1,"filename":"$id.jsonl","purpose":"fine-tune","status":"processed"}"""
        fun jobJson(status: String = "validating_files") =
            """{"id":"ftjob_1","created_at":1,"model":"gpt-3.5-turbo","organization_id":"org_1","status":"$status","hyperparameters":{"n_epochs":"auto"},"training_file":"file_1","result_files":[]}"""
        val steps = listOf(
            HttpMethod.Post to "/v1/files",
            HttpMethod.Get to "/v1/files/file_1",
            HttpMethod.Post to "/v1/fine_tuning/jobs",
            HttpMethod.Get to "/v1/fine_tuning/jobs",
            HttpMethod.Get to "/v1/fine_tuning/jobs/ftjob_1",
            HttpMethod.Get to "/v1/fine_tuning/jobs/ftjob_1/events",
            HttpMethod.Post to "/v1/fine_tuning/jobs/ftjob_1/cancel",
            HttpMethod.Delete to "/v1/files/file_1",
        )
        var index = 0
        val engine = MockEngine { outgoing ->
            assertTrue(index < steps.size, "Unexpected request ${outgoing.method} ${outgoing.url.encodedPath}")
            assertEquals(steps[index], outgoing.method to outgoing.url.encodedPath)
            val response = when (index++) {
                0 -> {
                    val multipart = outgoing.body.toByteArray().decodeToString()
                    assertTrue(multipart.contains(jsonl))
                    assertTrue(multipart.contains("$id.jsonl"))
                    assertTrue(multipart.contains("fine-tune"))
                    fileJson
                }
                1 -> fileJson
                2 -> {
                    val body = Json.parseToJsonElement(outgoing.body.toByteArray().decodeToString()).jsonObject
                    assertEquals("file_1", body.getValue("training_file").jsonPrimitive.content)
                    assertEquals("gpt-3.5-turbo", body.getValue("model").jsonPrimitive.content)
                    jobJson()
                }
                3 -> """{"data":[${jobJson()}]}"""
                4 -> jobJson()
                5 -> """{"data":[{"id":"event_1","created_at":1,"level":"info","message":"Validating files"}]}"""
                6 -> jobJson("cancelled")
                else -> """{"id":"file_1","object":"file.deleted","deleted":true}"""
            }
            respond(response, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        OpenAI(OpenAIConfig("offline-test", engine = engine, retry = RetryStrategy(maxRetries = 0),
            logging = LoggingConfig(LogLevel.None))).use { openAI ->
            withTestCleanup {
                val fileId = openAI.file(request).id
                afterTest { openAI.delete(fileId) }
                openAI.waitFileProcess(fileId)

                val jobRequest = FineTuningRequest(trainingFile = fileId, model = ModelId("gpt-3.5-turbo"))
                val fineTuningJob = openAI.fineTuningJob(jobRequest)
                afterTest { assertNotNull(openAI.cancel(fineTuningJob.id)) }

                assertEquals(fineTuningJob.trainingFile.id, fileId.id)

                // At least one fine-tune exists
                val fineTunes = openAI.fineTuningJobs()
                assertTrue(fineTunes.isNotEmpty())
                assertTrue(fineTunes.contains(fineTuningJob))

                // At least last created fine-tune exists
                val createdFineTune = openAI.fineTuningJob(fineTuningJob.id)
                assertNotNull(createdFineTune)

                // Get events
                val fineTuneEvents = openAI.fineTuningEvents(fineTuningJob.id)
                assertTrue(fineTuneEvents.isNotEmpty())
            }
        }
        assertEquals(steps.size, index, "Every lifecycle and cleanup operation should execute")
    }
}
