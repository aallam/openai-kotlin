package com.aallam.openai.client

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.file.FileRequest
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.finetune.FineTuneEvent
import com.aallam.openai.api.finetune.FineTuneRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.waitFileProcess
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import okio.Path
import okio.Path.Companion.toPath
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
        val filePath: Path = "$id.jsonl".toPath()
        val filename = filePath.name
        val jsonl = """
           { "prompt":"Did the U.S. join the League of Nations?", "completion":"No"}
           { "prompt":"Where was the League of Nations created?", "completion":"Paris"}
        """.trimIndent()
        fileSystem.write(filePath) { writeUtf8(jsonl) }
        val request = FileRequest(
            file = filePath.toString(),
            purpose = Purpose("fine-tune")
        )
        val trainingFile = openAI.file(request).id
        openAI.waitFileProcess(trainingFile)

        // Fine-tune created using training file
        val fineTune = openAI.fineTune(
            request = FineTuneRequest(
                trainingFile = trainingFile,
                model = ModelId("ada")
            )
        )
        val fineTuneModel = fineTune.fineTunedModel
        assertEquals(fineTune.trainingFiles.first().filename, filename)

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
        openAI.delete(trainingFile)
        fineTuneModel?.let { openAI.delete(it) }
    }
}
