package com.aallam.openai.client

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.model.ModelId
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotEquals

class TestCompletions : TestOpenAI() {

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
}
