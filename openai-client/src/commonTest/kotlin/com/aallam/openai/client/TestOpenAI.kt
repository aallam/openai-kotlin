package com.aallam.openai.client

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.answer.AnswerRequest
import com.aallam.openai.api.answer.QuestionAnswer
import com.aallam.openai.api.classification.ClassificationRequest
import com.aallam.openai.api.classification.LabeledExample
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.engine.Ada
import com.aallam.openai.api.engine.Curie
import com.aallam.openai.api.engine.Davinci
import com.aallam.openai.api.file.File
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileRequest
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.finetunes.FineTune
import com.aallam.openai.api.finetunes.FineTuneId
import com.aallam.openai.api.finetunes.FineTuneRequest
import com.aallam.openai.api.search.SearchRequest
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
import kotlin.test.*

class TestOpenAI {

    private val fileSystem = FakeFileSystem()
    private val openAI = OpenAIApi(transport, fileSystem)

    @Test
    fun search() = runTest {
        val documents = listOf("White House", "hospital", "school")
        val query = "the president"
        val request = SearchRequest(documents, query)
        val response = openAI.search(Davinci, request)
        assertEquals(documents.size, response.size)
    }

    @Test
    fun engines() = runTest {
        val response = openAI.engines()
        assertNotEquals(0, response.size)
    }

    @Test
    fun engine() = runTest {
        val engineId = Davinci
        val response = openAI.engine(engineId)
        assertEquals(engineId, response.id)
    }

    @Test
    fun completion() = runTest {
        val request = CompletionRequest(
            prompt = "Once upon a time",
            maxTokens = 5,
            temperature = 1.0,
            topP = 1.0,
            n = 1,
            logprobs = null,
            stop = listOf("\n")
        )

        val response = openAI.completion(Davinci, request)
        assertNotNull(response.choices[0].text)
    }

    @Test
    fun completions() = runTest {
        val request = CompletionRequest(
            prompt = "Once upon a time",
            maxTokens = 5,
            temperature = 1.0,
            topP = 1.0,
            n = 1,
            logprobs = null,
            stop = listOf("\n"),
        )

        val results = mutableListOf<TextCompletion>()
        openAI.completions(Davinci, request)
            .onEach { results += it }
            .launchIn(this)
            .join()

        assertNotEquals(0, results.size)
    }

    @ExperimentalOpenAI
    @Test
    fun classifications() = runTest {
        val request = ClassificationRequest(
            model = Curie,
            query = "It is a raining day :(",
            searchModel = Ada,
            labels = listOf("Positive", "Negative", "Neutral"),
            examples = listOf(
                LabeledExample("A happy moment", "Positive"),
                LabeledExample("I am sad.", "Negative"),
                LabeledExample("I am feeling awesome", "Positive"),
            )
        )
        val response = openAI.classifications(request)
        assertEquals("Negative", response.label)
    }

    @ExperimentalOpenAI
    @Test
    fun answers() = runTest {
        val request = AnswerRequest(
            model = Curie,
            question = "which puppy is happy?",
            searchModel = Ada,
            examples = listOf(
                QuestionAnswer(
                    question = "What is human life expectancy in the United States?",
                    answer = "78 years."
                )
            ),
            examplesContext = "In 2017, U.S. life expectancy was 78.6 years.",
            maxTokens = 5,
            stop = listOf("\n", "<|endoftext|>"),
            documents = listOf("Puppy A is happy.", "Puppy B is sad.")
        )
        val response = openAI.answers(request)
        assertEquals("puppy A.", response.answers[0])
    }

    @Test
    fun file() = runTest {
        // File setup
        val filePath: Path = "pupps.jsonl".toPath()
        val jsonl = """
                    { "text": "AJ" }
                    { "text": "Abby" }
                    { "text": "Abe" }
                    { "text": "Ace" }
                """.trimIndent()
        fileSystem.write(filePath) { writeUtf8(jsonl) }

        val request = FileRequest(
            file = filePath.toString(),
            purpose = Purpose.Answers
        )
        val filename = filePath.name

        // Create file
        val fileCreate = openAI.file(request)
        assertEquals(filename, fileCreate.filename)

        // Get created file
        waitFileProcess(fileCreate.id)

        // Delete file
        openAI.delete(fileCreate.id)

        // Check deleted file
        val fileGetAfterDelete = openAI.file(fileCreate.id)
        assertNull(fileGetAfterDelete)
    }

    @Test
    fun files() = runTest {
        val response = openAI.files()
        assertNotNull(response)
        assertTrue(response.isNotEmpty())
    }

    @ExperimentalOpenAI
    @Test
    fun fineTune() = runTest {
        val jsonl = """
            {"prompt":"Company: BHFF insurance\nProduct: allround insurance\nAd:One stop shop for all your insurance needs!\nSupported:", "completion":" yes"}
            {"prompt":"Company: Loft conversion specialists\nProduct: -\nAd:Straight teeth in weeks!\nSupported:", "completion":" no"}
        """.trimIndent()
        withFile("training.jsonl", jsonl, Purpose.FineTune) {
            val request = FineTuneRequest(it.id)
            var fineTune = openAI.fineTune(request)
            println(fineTune)
            fineTune = waitFineTune(fineTune.id)
            val completionRequest = CompletionRequest(
                prompt = "Company: Reliable accountants Ltd\\nProduct: Personal Tax help\\nAd:Best advice in town!\\nSupported:",
                maxTokens = 1,
                model = fineTune.fineTunedModel
            )
            println(fineTune)
            val completion = openAI.completion(completionRequest)
            println(completion)
            assertTrue(completion.choices.isNotEmpty())

            openAI.deleteFineTune(fineTune.id)

            // Check deleted file
            val fineTuneGetAfterDelete = openAI.fineTune(fineTune.id)
            assertNull(fineTuneGetAfterDelete)
        }
    }

    private suspend fun withFile(filePath: String, jsonl: String, purpose: Purpose, block: suspend (File) -> Unit) {
        fileSystem.write(filePath.toPath()) { writeUtf8(jsonl) }
        val request = FileRequest(
            file = filePath,
            purpose = purpose
        )
        val file = openAI.file(request)
        block(file)
        openAI.delete(file.id)
    }

    private suspend fun waitFileProcess(fileId: FileId) {
        withContext(Dispatchers.Default) {
            while (true) {
                val file = openAI.file(fileId)
                if (file?.status?.raw == "processed") break
                delay(3000L)
            }
        }
    }

    @ExperimentalOpenAI
    private suspend fun waitFineTune(fineTuneId: FineTuneId): FineTune {
        return withContext(Dispatchers.Default) {
            var currentDelay = 1000L
            while (true) {
                val fineTune = openAI.fineTune(fineTuneId)
                println(fineTune)
                if (fineTune?.status?.raw == "processed") return@withContext fineTune
                delay(currentDelay)
                currentDelay = (currentDelay * 2).coerceAtMost(30_000L)
                println("current delay: $currentDelay")
            }
            error("unreachable..")
        }
    }
}
