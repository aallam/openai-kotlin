package com.aallam.openai.client

import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.testFilePath
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.io.buffered
import kotlinx.io.files.SystemFileSystem
import kotlin.test.Test
import kotlinx.io.readString
import kotlin.test.assertFailsWith

class TestDeepinfraChatCompletions : TestDeepinfraOpenAICompatibleEndpoint() {

    @Test
    fun testChatCompletionExceedingTokenLimit() = test {
        val request = chatCompletionRequest {
            model = ModelId("GuilhermeFreire/llama-3.3-70b-32k-instruct-slg-1-11-1-16k")
            messages {
                message {
                    role = ChatRole.User
                    content = SystemFileSystem.source(
                        testFilePath("text/two_character_screenplay.txt")
                    ).buffered().readString()
                }
            }
        }

        assertFailsWith<InvalidRequestException> {
            deepInfra.chatCompletion(request)
        }
    }

    @Test
    fun testStreamingChatCompletionsExceedingTokenLimit() = test {
        val request = chatCompletionRequest {
            model = ModelId("GuilhermeFreire/llama-3.3-70b-32k-instruct-slg-1-11-1-16k")
            messages {
                message {
                    role = ChatRole.User
                    content = SystemFileSystem.source(
                        testFilePath("text/two_character_screenplay.txt")
                    ).buffered().readString()
                }
            }
        }

        val result = runCatching {
            deepInfra.chatCompletions(request).collect { }
        }

        assert(result.exceptionOrNull() is InvalidRequestException)
    }
}
