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
                    parameters = FunctionParameters.fromJsonString(
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
            functionCall = FunctionMode.Auto
        }

        val response = openAI.chatCompletion(request)
        val message = response.choices.first().message ?: error("No chat response found!")

        message.functionCall?.let { functionCall ->
            val availableFunctions = mapOf("currentWeather" to ::currentWeather)

            val functionToCall = availableFunctions[functionCall.name] ?: return@let
            val functionArgs = functionCall.argumentsAsJson() ?: error("arguments field is missing")

            val functionResponse = functionToCall(
                functionArgs.getValue("location").jsonPrimitive.content,
                functionArgs["unit"]?.jsonPrimitive?.content ?: "fahrenheit"
            )

            chatMessages.add(message.copy(content = "")) // OpenAI throws an error in this case if the content is null, although it's optional!
            chatMessages.add(
                ChatMessage(role = ChatRole.Function, name = functionCall.name, content = functionResponse)
            )

            val secondResponse = openAI.chatCompletion(
                request = ChatCompletionRequest(
                    model = modelId,
                    messages = chatMessages,
                )
            )

            print(secondResponse)
        }
    }

    @Serializable
    data class WeatherInfo(val location: String, val temperature: String, val unit: String, val forecast: List<String>)

    fun currentWeather(location: String, unit: String): String {
        val weatherInfo = WeatherInfo(location, "72", unit, listOf("sunny", "windy"))
        return Json.encodeToString(weatherInfo)
    }
}

