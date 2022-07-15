@file:Suppress("DEPRECATION")

package com.aallam.openai.client

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.edits.EditsRequest
import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.file.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.moderation.ModerationRequest
import com.aallam.openai.client.internal.OpenAIApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import okio.Path
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import ulid.ULID
import kotlin.test.*

class TestOpenAI {

    private val fileSystem = FakeFileSystem()
    private val openAI = OpenAIApi(transport, fileSystem)

    @Test
    fun completions() {
        runTest {
            val request = CompletionRequest(
                model = ModelId("text-davinci-002"),
                prompt = "Once upon a time",
                maxTokens = 5,
                temperature = 1.0,
                topP = 1.0,
                n = 1,
                stop = listOf("\n"),
            )

            val results = mutableListOf<TextCompletion>()
            openAI.completions(request)
                .onEach { results += it }
                .launchIn(this)
                .join()

            assertNotEquals(0, results.size)
        }
    }

    @Test
    fun file() {
        runTest {
            val id = ULID.randomULID()
            val filePath: Path = "$id.jsonl".toPath()
            val jsonl = """
                    { "text": "AJ" }
                    { "text": "Abby" }
                    { "text": "Abe" }
                    { "text": "Ace" }
                """.trimIndent()
            fileSystem.write(filePath) { writeUtf8(jsonl) }

            val request = FileRequest(
                file = filePath.toString(),
                purpose = Purpose("answers")
            )
            val filename = filePath.name

            // Create file
            val fileCreate = openAI.file(request)
            assertEquals(filename, fileCreate.filename)

            // Get created file
            withContext(Dispatchers.Default) {
                waitFileProcess(fileCreate.id)
            }

            // Delete file
            openAI.delete(fileCreate.id)

            // Check deleted file
            val fileGetAfterDelete = openAI.file(fileCreate.id)
            assertNull(fileGetAfterDelete)
        }
    }

    @Test
    fun files() {
        runTest {
            val response = openAI.files()
            assertNotNull(response)
            assertTrue(response.isNotEmpty())
        }
    }

    @Test
    fun edits() = runTest {
        val response = openAI.edit(
            request = EditsRequest(
                model = ModelId("text-davinci-edit-001"),
                input = "What day of the wek is it?",
                instruction = "Fix the spelling mistakes"
            )
        )
        assertTrue { response.created != 0L }
        assertTrue { response.choices.isNotEmpty() }
    }

    @Test
    fun embeddings() = runTest {
        val response = openAI.embeddings(
            request = EmbeddingRequest(
                model = ModelId("text-similarity-babbage-001"),
                input = listOf("The food was delicious and the waiter...")
            )
        )
        assertTrue { response.isNotEmpty() }
        val embedding = response.first()
        assertTrue { embedding.embedding.isNotEmpty() }
        assertEquals(embedding.index, 0)
    }

    @Test
    fun models() = runTest {
        val resModels = openAI.models()
        assertTrue { resModels.isNotEmpty() }
        val resModel = resModels.first()
        val model = openAI.model(resModel.id)
        assertEquals(resModel, model)
    }

    @Test
    fun moderations() = runTest {
        val response = openAI.moderations(
            request = ModerationRequest(
                input = "I want to kill them."
            )
        )

        val result = response.results.first()
        assertTrue(result.flagged)
    }

    private suspend fun waitFileProcess(fileId: FileId) {
        while (true) {
            val file = openAI.file(fileId)
            if (file?.status == Status("processed")) break
            delay(1000L)
        }
    }
}
