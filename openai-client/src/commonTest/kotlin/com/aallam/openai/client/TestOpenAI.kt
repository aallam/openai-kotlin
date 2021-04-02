package com.aallam.openai.client

import com.aallam.openai.api.answer.AnswerRequest
import com.aallam.openai.api.answer.QuestionAnswer
import com.aallam.openai.api.classification.ClassificationRequest
import com.aallam.openai.api.classification.LabeledExample
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.api.search.SearchRequest
import com.aallam.openai.client.internal.runBlockingTest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class TestOpenAI {

    private val openAI = OpenAI(config)

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
}

