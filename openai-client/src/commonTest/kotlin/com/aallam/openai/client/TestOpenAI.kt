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
import com.aallam.openai.api.file.Answers
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileRequest
import com.aallam.openai.api.file.Processed
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
    private val openAI = OpenAIApi(httpClient, fileSystem)
    private lateinit var filePath: Path

    @BeforeTest
    fun init() {
        val jsonl = """
            { "text": "AJ" }
            { "text": "Abby" }
            { "text": "Abe" }
            { "text": "Ace" }
        """.trimIndent()
        filePath = "pupps.jsonl".toPath()
        fileSystem.write(filePath) {
            writeUtf8(jsonl)
        }
    }

    @Test
    fun search() {
        runTest {
            val documents = listOf("White House", "hospital", "school")
            val query = "the president"
            val request = SearchRequest(documents, query)
            val response = openAI.search(Davinci, request)
            assertEquals(documents.size, response.size)
        }
    }

    @Test
    fun engines() {
        runTest {
            val response = openAI.engines()
            assertNotEquals(0, response.size)
        }
    }

    @Test
    fun engine() {
        runTest {
            val engineId = Davinci
            val response = openAI.engine(engineId)
            assertEquals(engineId, response.id)
        }
    }

    @Test
    fun completion() {
        runTest {
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
    }

    @Test
    fun completions() {
        runTest {
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
    }

    @ExperimentalOpenAI
    @Test
    fun classifications() {
        runTest {
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
    }

    @ExperimentalOpenAI
    @Test
    fun answers() {
        runTest {
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
    }

    @Test
    fun file() {
        runTest {
            val request = FileRequest(
                file = filePath.toString(),
                purpose = Answers
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
        }
    }

    private suspend fun waitFileProcess(fileId: FileId) {
        while (true) {
            val file = openAI.file(fileId)
            if (file?.status == Processed) break
            delay(1000L)
        }
    }
}
