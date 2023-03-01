package com.aallam.openai.client

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.chat.chatMessage
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
                model = ModelId("gpt-3.5-turbo"),
                messages = listOf(
                    chatMessage {
                        role = ChatRole.User,
                        content = "Hello!"
                    }
                )
            }

            val completion = openAI.chatCompletion(request)
            assertTrue { completion.choices.isNotEmpty() }

            val results = mutableListOf<ChatCompletion>()
            openAI.chatCompletions(request)
                .onEach { results += it }
                .launchIn(this)
                .join()

            assertNotEquals(0, results.size)
        }
    }
}