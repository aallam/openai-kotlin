package com.aallam.openai.client

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.file.fileSource
import com.aallam.openai.api.file.fileUpload
import com.aallam.openai.api.finetune.FineTuneEvent
import com.aallam.openai.api.finetune.fineTuneRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.asSource
import com.aallam.openai.client.internal.waitFileProcess
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import ulid.ULID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalOpenAI::class)
class TestFineTunes : TestOpenAI() {

    @Test
    fun fineTunes() = runTest {

        val id = ULID.randomULID()
        val jsonl = """
           { "prompt":"Did the U.S. join the League of Nations?", "completion":"No"}
           { "prompt":"Where was the League of Nations created?", "completion":"Paris"}
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

        // Fine-tune created using training file
        val fineTune = openAI.fineTune(
            request = fineTuneRequest {
                trainingFile = fileId
                model = ModelId("ada")
            }
        )
        val fineTuneModel = fineTune.fineTunedModel
        assertEquals(fineTune.trainingFiles.first().filename, source.name)

        // At least one fine-tune exists
        val fineTunes = openAI.fineTunes()
        assertTrue(fineTunes.isNotEmpty())

        // At least last created fine-tune exists
        val createdFineTune = openAI.fineTune(fineTune.id)
        assertNotNull(createdFineTune)

        // Get events
        val fineTuneEvents = openAI.fineTuneEvents(fineTune.id)
        assertTrue(fineTuneEvents.isNotEmpty())

        // Cancel fine-tune
        val canceled = openAI.cancel(fineTune.id)
        assertNotNull(canceled)

        // Get events as stream
        val events = mutableListOf<FineTuneEvent>()
        openAI.fineTuneEventsFlow(fineTune.id)
            .onEach { events += it }
            .launchIn(this)
            .join()

        // cleanup
        openAI.delete(fileId)
        fineTuneModel?.let { openAI.delete(it) }
    }
}
