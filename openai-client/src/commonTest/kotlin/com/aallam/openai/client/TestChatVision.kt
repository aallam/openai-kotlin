package com.aallam.openai.client

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.FinishReason
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.reportUsage
import com.aallam.openai.client.internal.TestFileSystem
import com.aallam.openai.client.internal.testFilePath
import kotlinx.io.buffered
import kotlinx.io.bytestring.encode
import kotlinx.io.readByteString
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.*

@OptIn(ExperimentalEncodingApi::class)
private val InlineTestImage: String by lazy {
    val imageBytes = TestFileSystem.source(path = testFilePath("image/pets.png")).buffered().use {
        it.readByteString()
    }
    "data:image/png;base64,${Base64.encode(source = imageBytes)}"
}

class TestChatVision : TestOpenAI() {

    @Test
    fun textimage() = test {
        val request = chatCompletionRequest {
            model = ModelId("gpt-4o")
            messages {
                user {
                    content {
                        text("Describe this image in one short sentence.")
                        // Keep this test deterministic in CI by avoiding external image hosts.
                        image(InlineTestImage)
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

    @Test
    fun multiImage() = test {
        val request = chatCompletionRequest {
            model = ModelId("gpt-4o")
            messages {
                user {
                    content {
                        text("Briefly describe both images and say whether they differ.")
                        image(InlineTestImage, detail = "low")
                        image(InlineTestImage, detail = "low")
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
