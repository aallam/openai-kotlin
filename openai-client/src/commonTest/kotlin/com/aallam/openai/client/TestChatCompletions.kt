package com.aallam.openai.client

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

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
        val modelId = ModelId("gpt-3.5-turbo-0613")
        val chatMessages = mutableListOf(
            ChatMessage(
                role = ChatRole.User,
                content = "What's the weather like in Boston?"
            )
        )

        val request = chatCompletionRequest {
            model = modelId
            messages = chatMessages
            functions {
                function {
                    name = "currentWeather"
                    description = "Get the current weather in a given location"
                    parameters = Parameters.fromJsonString(
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
            }
            functionCall = FunctionMode.Named("currentWeather")
        }

        val response = openAI.chatCompletion(request)
        val message = response.choices.first().message ?: error("No chat response found!")
        assertEquals("currentWeather",  message.functionCall?.name)
    }
}
