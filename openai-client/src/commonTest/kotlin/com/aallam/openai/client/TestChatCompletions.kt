package com.aallam.openai.client

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.*

class TestChatCompletions : TestOpenAI() {

    @Test
    fun chatCompletions() = test {
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

    @Test
    fun chatCompletionsFunction() = test {
        val modelId = ModelId("gpt-3.5-turbo")
        val chatMessages = mutableListOf(
            ChatMessage(
                role = ChatRole.User,
                content = "What's the weather like in Boston?"
            )
        )

        val request = chatCompletionRequest {
            model = modelId
            messages = chatMessages
            tools {
                function(
                    name = "currentWeather",
                    parameters =
                    """
                    {
                      "type": "object",
                      "properties": {
                        "location": {
                          "type": "string",
                          "description": "The city and state, e.g. San Francisco, CA"
                        },
                        "unit": {
                          "type": "string",
                          "enum": [
                            "celsius",
                            "fahrenheit"
                          ]
                        }
                      },
                      "required": [
                        "location"
                      ]
                    }
                    """
                )
            }

            toolChoice = ToolChoice.function("currentWeather")
        }

        val response = openAI.chatCompletion(request)
        val message = response.choices.first().message
        val toolCall = message.toolCalls?.first() as? ToolCall.Function
        assertNotNull(toolCall)
        assertEquals("currentWeather", toolCall.function?.name)
    }

    @Test
    fun json() = test {
        val request = chatCompletionRequest {
            model = ModelId("gpt-3.5-turbo-1106")
            responseFormat = ChatResponseFormat.JsonObject
            messages {
                message {
                    role = ChatRole.System
                    content = "You are a helpful assistant.!"
                }
                message {
                    role = ChatRole.System
                    content =
                        """All your answers should be a valid JSON, and the format: {"question": <question>, "response": <response>}"""
                }
                message {
                    role = ChatRole.User
                    content = "Who won the world cup in 1998?"
                }
            }
        }
        val response = openAI.chatCompletion(request)
        val content = response.choices.first().message.content.orEmpty()

        @Serializable
        data class Answer(val question: String? = null, val response: String? = null)

        val answer = Json.decodeFromString<Answer>(content)
        assertNotNull(answer.question)
        assertNotNull(answer.response)
    }
}
