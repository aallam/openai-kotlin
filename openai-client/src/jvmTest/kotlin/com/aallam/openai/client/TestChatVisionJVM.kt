package com.aallam.openai.client

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.chat.internal.ToolType
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.TestOpenAI
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okio.FileSystem
import okio.Okio
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
