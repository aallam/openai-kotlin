package com.aallam.openai.client

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.test.*

class TestChatVisionJVM : TestOpenAI() {

    @Test
    fun encoded() = test {
        val byteString = FileSystem.RESOURCES.read("nature.jpeg".toPath()) {
            readByteString()
        }
        val encoded = byteString.base64()
        val request = chatCompletionRequest {
            model = ModelId("gpt-4-vision-preview")
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
