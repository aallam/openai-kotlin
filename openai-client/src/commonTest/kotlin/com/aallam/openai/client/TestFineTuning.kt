package com.aallam.openai.client

import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.file.fileSource
import com.aallam.openai.api.file.fileUpload
import com.aallam.openai.api.finetuning.FineTuningRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.asSource
import com.aallam.openai.client.internal.waitFileProcess
import ulid.ULID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestFineTuning : TestOpenAI() {

    @Test
    fun fineTuningJob() = test {
        val id = ULID.randomULID()
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
        val fileId = openAI.file(request).id
        openAI.waitFileProcess(fileId)

        val jobRequest = FineTuningRequest(trainingFile = fileId, model = ModelId("gpt-3.5-turbo"))
        val fineTuningJob = openAI.fineTuningJob(jobRequest)

        assertEquals(fineTuningJob.trainingFile.id, fileId.id)

        // At least one fine-tune exists
        val fineTunes = openAI.fineTuningJobs()
        assertTrue(fineTunes.isNotEmpty())
        println(fineTunes.contains(fineTuningJob))

        // At least last created fine-tune exists
        val createdFineTune = openAI.fineTuningJob(fineTuningJob.id)
        assertNotNull(createdFineTune)

        // Get events
        val fineTuneEvents = openAI.fineTuningEvents(fineTuningJob.id)
        assertTrue(fineTuneEvents.isNotEmpty())

        // Cancel fine-tune
        val canceled = openAI.cancel(fineTuningJob.id)
        assertNotNull(canceled)

        // cleanup
        openAI.delete(fileId)
    }
}
