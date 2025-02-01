package com.aallam.openai.client

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import kotlinx.io.buffered
import kotlinx.io.bytestring.encode
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteString
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

import kotlin.test.*

class TestChatVisionJVM : TestOpenAI() {

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun encoded() = test {
        val byteString = SystemFileSystem.source(path = Path("src/jvmTest/resources/nature.jpeg")).buffered().use {
            it.readByteString()
        }
        val encoded = Base64.encode(source = byteString)
        val request = chatCompletionRequest {
            model = ModelId("gpt-4o")
            messages {
                user {
                    content {
                        text("What’s in this image?")
                        image("data:image/jpeg;base64,$encoded")
                    }
                }
            }
            maxTokens = 300
        }
        val response = openAI.chatCompletion(request)
        val content = response.choices.first().message.content.orEmpty()
        assertNotNull(content)
    }

}
