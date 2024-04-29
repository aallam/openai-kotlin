package com.aallam.openai.client

import com.aallam.openai.api.batch.Batch
import com.aallam.openai.api.batch.BatchRequest
import com.aallam.openai.api.batch.CompletionWindow
import com.aallam.openai.api.batch.RequestInput
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.TextContent
import com.aallam.openai.api.core.Endpoint
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.file.fileSource
import com.aallam.openai.api.file.fileUpload
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.JsonLenient
import com.aallam.openai.client.internal.asSource
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class TestBatches : TestOpenAI() {


    @Test
    fun batchSerialization() {
        val json = """
            {
              "id": "batch_0mhGzcpyyQnS1T38bFI4vgMN",
              "object": "batch",
              "endpoint": "/v1/chat/completions",
              "errors": null,
              "input_file_id": "file-CmkZMEEBGbVB0YMzuKMjCT0C",
              "completion_window": "24h",
              "status": "validating",
              "output_file_id": null,
              "error_file_id": null,
              "created_at": 1714347843,
              "in_progress_at": null,
              "expires_at": 1714434243,
              "finalizing_at": null,
              "completed_at": null,
              "failed_at": null,
              "expired_at": null,
              "cancelling_at": null,
              "cancelled_at": null,
              "request_counts": {
                "total": 0,
                "completed": 0,
                "failed": 0
              },
              "metadata": null
            }
        """.trimIndent()

        val batch = JsonLenient.decodeFromString<Batch>(json)
        assertEquals("batch_0mhGzcpyyQnS1T38bFI4vgMN", batch.id.id)
        assertEquals("/v1/chat/completions", batch.endpoint.path)
        assertEquals("24h", batch.completionWindow?.value)
    }

    @Test
    fun batches() = test {
        val requestInput = RequestInput(
            customId = "request-1",
            method = "POST",
            url = "/v1/chat/completions",
            body = ChatCompletionRequest(
                model = ModelId("gpt-3.5-turbo"),
                messages = listOf(
                    ChatMessage(
                        role = Role("system"),
                        messageContent = TextContent("You are a helpful assistant.")
                    ),
                    ChatMessage(
                        role = Role("user"),
                        messageContent = TextContent("What is 2+2?")
                    )
                )
            )
        )

        val jsonl = JsonLenient.encodeToString(requestInput)

        val fileRequest = fileUpload {
            file = fileSource {
                name = "requestInput.jsonl"
                source = jsonl.asSource()
            }
            purpose = Purpose("batch")
        }
        val batchFile = openAI.file(fileRequest)

        val request = BatchRequest(
            inputFileId = batchFile.id,
            endpoint = Endpoint.Completions,
            completionWindow = CompletionWindow.TwentyFourHours

        )
        val batch = openAI.batch(request = request)
        val fetchedBatch = openAI.batch(id = batch.id)
        assertEquals(batch.id, fetchedBatch?.id)

        val batches = openAI.batches()
        assertContains(batches.map { it.id }, batch.id)

        openAI.cancel(id = batch.id)
        openAI.delete(fileId = batchFile.id)
    }
}