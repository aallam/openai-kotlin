package com.aallam.openai.sample.jvm

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

@OptIn(BetaOpenAI::class)
suspend fun chatFunctionCall(openAI: OpenAI) {
    println("> Create Chat Completion function call...")
    val modelId = ModelId("gpt-3.5-turbo-0613")
    val chatMessages = mutableListOf(
        ChatMessage(
            role = ChatRole.User,
            content = "What's the weather like in Boston?"
        )
    )

    val params = FunctionParameters.buildJson {
        put("type", "object")
        putJsonObject("properties") {
            putJsonObject("location") {
                put("type", "string")
                put("description", "The city and state, e.g. San Francisco, CA")
            }
            putJsonObject("unit") {
                put("type", "string")
                putJsonArray("enum") {
                    add("celsius")
                    add("fahrenheit")
                }
            }
        }
        putJsonArray("required") {
            add("location")
        }
    }
    val request = chatCompletionRequest {
        model = modelId
        messages = chatMessages
        functions {
            function {
                name = "currentWeather"
                description = "Get the current weather in a given location"
                parameters = params
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

/**
 * Example dummy function hard coded to return the same weather
 * In production, this could be your backend API or an external API
 */
fun currentWeather(location: String, unit: String): String {
    val weatherInfo = WeatherInfo(location, "72", unit, listOf("sunny", "windy"))
    return Json.encodeToString(weatherInfo)
}