package com.aallam.openai.client

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.FinishReason
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.reportUsage
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
                        text("Describe this image in one short sentence.")
                        image("data:image/jpeg;base64,$encoded", detail = "low")
                    }
                }
            }
            maxTokens = 64
        }
        val response = openAI.chatCompletion(request)
        reportUsage("vision", response.model, response.usage)
        assertEquals(FinishReason.Stop, response.choices.first().finishReason)
        val content = response.choices.first().message.content
        assertFalse(content.isNullOrBlank())
    }

}
