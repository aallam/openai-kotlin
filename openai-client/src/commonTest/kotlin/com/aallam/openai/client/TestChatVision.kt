package com.aallam.openai.client

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import kotlin.test.*

class TestChatVision : TestOpenAI() {

    @Test
    fun textimage() = test {
        val request = chatCompletionRequest {
            model = ModelId("gpt-4-vision-preview")
            messages {
                user {
                    content {
                        text("What’s in this image?")
                        image("https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg")
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
            model = ModelId("gpt-4-vision-preview")
            messages {
                user {
                    content {
                        text("What are in these images? Is there any difference between them?")
                        image("https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg")
                        image("https://upload.wikimedia.org/wikipedia/commons/thumb/d/dd/Gfp-wisconsin-madison-the-nature-boardwalk.jpg/2560px-Gfp-wisconsin-madison-the-nature-boardwalk.jpg")
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
