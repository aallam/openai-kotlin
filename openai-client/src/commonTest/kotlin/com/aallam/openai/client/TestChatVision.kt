package com.aallam.openai.client

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
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
                        text("What’s in this image?")
                        // Keep this test deterministic in CI by avoiding external image hosts.
                        image(InlineTestImage)
                    }
                }
            }
            maxTokens = 300
        }
        val response = openAI.chatCompletion(request)
        val content = response.choices.first().message.content.orEmpty()
        assertNotNull(content)
    }

    @Test
    fun multiImage() = test {
        val request = chatCompletionRequest {
            model = ModelId("gpt-4o")
            messages {
                user {
                    content {
                        text("What are in these images? Is there any difference between them?")
                        image(InlineTestImage)
                        image(InlineTestImage)
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
