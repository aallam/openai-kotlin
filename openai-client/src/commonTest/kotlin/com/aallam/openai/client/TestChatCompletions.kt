package com.aallam.openai.client

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.model.ModelId
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class TestChatCompletions : TestOpenAI() {

    @Test
    fun chatCompletions() {
        runTest {
            val request = chatCompletionRequest {
                model = ModelId("gpt-3.5-turbo")
                messages {
                    message {
                        role = ChatRole.System
                        content = "You are a helpful assistant.!"
                    }
                    message {
                        role = ChatRole.User
                        content = "Who won the world series in 2020?"
                    }
                    message {
                        role = ChatRole.Assistant
                        content = "The Los Angeles Dodgers won the World Series in 2020."
                    }
                    message {
                        role = ChatRole.User
                        content = "Where was it played?"
                    }
                }
            }

            val completion = openAI.chatCompletion(request)
            assertTrue { completion.choices.isNotEmpty() }

            val results = mutableListOf<ChatCompletionChunk>()
            openAI.chatCompletions(request).onEach { results += it }.launchIn(this).join()

            assertNotEquals(0, results.size)
        }
    }
}
