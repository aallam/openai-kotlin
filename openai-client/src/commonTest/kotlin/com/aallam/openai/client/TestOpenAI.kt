package com.aallam.openai.client

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.answer.AnswerRequest
import com.aallam.openai.api.answer.QuestionAnswer
import com.aallam.openai.api.classification.ClassificationRequest
import com.aallam.openai.api.classification.LabeledExample
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.api.file.FileRequest
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.search.SearchRequest
import com.aallam.openai.client.internal.runBlockingTest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okio.ExperimentalFileSystem
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import kotlin.test.*

@ExperimentalFileSystem
class TestOpenAI {

    private val openAI = OpenAI(config)
    private lateinit var filePath: Path

    @BeforeTest
    fun init() {
        val filename = "pupps.jsonl"
        val jsonl = """
            { "text": "AJ" }
            { "text": "Abby" }
            { "text": "Abe" }
            { "text": "Ace" }
        """.trimIndent()
        filePath = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / filename.toPath()
        FileSystem.SYSTEM.write(filePath) {
            writeUtf8(jsonl)
        }
    }

    @AfterTest
    fun finish() {
        runBlockingTest {
            FileSystem.SYSTEM.delete(filePath)
            openAI.files().forEach {
                openAI.delete(it.id)
            }
        }
    }

    @Test
    fun search() {
        runBlockingTest {
            val documents = listOf("White House", "hospital", "school")
            val query = "the president"
            val request = SearchRequest(documents, query)
            val response = openAI.search(EngineId.Davinci, request)
            assertEquals(documents.size, response.size)
        }
    }

    @Test
    fun engines() {
        runBlockingTest {
            val response = openAI.engines()
            assertNotEquals(0, response.size)
        }
    }

    @Test
    fun engine() {
        runBlockingTest {
            val engineId = EngineId.Davinci
            val response = openAI.engine(engineId)
            assertEquals(engineId, response.id)
        }
    }

    @Test
    fun completion() {
        runBlockingTest {
            val request = CompletionRequest(
                    prompt = "Once upon a time",
                    maxTokens = 5,
                    temperature = 1.0,
                    topP = 1.0,
                    n = 1,
                    logprobs = null,
                    stop = listOf("\n")
            )

            val response = openAI.completion(EngineId.Davinci, request)
            assertNotNull(response.choices[0].text)
        }
    }

    @Test
    fun completions() {
        runBlockingTest {
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
            openAI.completions(EngineId.Davinci, request)
                    .onEach { results += it }
                    .launchIn(this)
                    .join()

            assertNotEquals(0, results.size)
        }
    }

    @ExperimentalOpenAI
    @Test
    fun classifications() {
        runBlockingTest {
            val request = ClassificationRequest(
                    model = EngineId.Curie,
                    query = "It is a raining day :(",
                    searchModel = EngineId.Ada,
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
        runBlockingTest {
            val request = AnswerRequest(
                    model = EngineId.Curie,
                    question = "which puppy is happy?",
                    searchModel = EngineId.Ada,
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

    @ExperimentalFileSystem
    @Test
    fun file() {
        runBlockingTest {
            val request = FileRequest(
                    file = filePath.toString(),
                    purpose = Purpose.Answers
            )
            val filename = filePath.name

            // Create file
            val fileCreate = openAI.file(request)
            assertEquals(filename, fileCreate.filename)

            // Get created file
            val fileGet = openAI.file(fileCreate.id)
            assertEquals(filename, fileGet?.filename)

            // Delete file
            openAI.delete(fileCreate.id)

            // Check deleted file
            val fileGetAfterDelete = openAI.file(fileCreate.id)
            assertNull(fileGetAfterDelete)
        }
    }

    @Test
    fun files() {
        runBlockingTest {
            val response = openAI.files()
            assertNotNull(response)
        }
    }
}
