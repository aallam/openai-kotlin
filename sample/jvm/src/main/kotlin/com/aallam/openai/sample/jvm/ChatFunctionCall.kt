package com.aallam.openai.sample.jvm

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.extension.mergeToChatMessage
import kotlinx.coroutines.flow.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

/**
 * This code snippet demonstrates the use of OpenAI's chat completion capabilities
 * with a focus on integrating function calls into the chat conversation.
 */
suspend fun chatFunctionCall(openAI: OpenAI) {
    // *** Chat Completion with Function Call  *** //

    println("\n> Create Chat Completion function call...")
    val modelId = ModelId("gpt-3.5-turbo-1106")
    val chatMessages = mutableListOf(
        ChatMessage(
            role = ChatRole.User,
            content = "What's the weather like in San Francisco, Tokyo, and Paris?"
        )
    )
    val request = chatCompletionRequest {
        model = modelId
        messages = chatMessages
        tools {
            function(
                name = "currentWeather",
                description = "Get the current weather in a given location",
                parameters = {
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
            )
        }
        toolChoice = ToolChoice.Auto // or ToolChoice.function("currentWeather")
    }

    val response = openAI.chatCompletion(request)
    val message = response.choices.first().message
    chatMessages.append(message)
    for (toolCall in message.toolCalls.orEmpty()) {
        val functionResponse = toolCall.execute()
        chatMessages.append(toolCall, functionResponse)
    }
    val secondResponse = openAI.chatCompletion(
        request = ChatCompletionRequest(model = modelId, messages = chatMessages)
    )
    print(secondResponse.choices.first().message.content.orEmpty())

    // reset messages
    chatMessages.clear()
    chatMessages.append(
        ChatMessage(
            role = ChatRole.User,
            content = "What's the weather like in San Francisco, Tokyo, and Paris?"
        )
    )

    // *** Chat Completion Stream with Function Call  *** //
    println("\n> Create Chat Completion function call (stream)...")
    @OptIn(ExperimentalOpenAI::class)
    val chatMessage = openAI.chatCompletions(request)
        .map { completion -> completion.choices.first() }
        .toList()
        .mergeToChatMessage()

    chatMessages.append(chatMessage)
    for (toolCall in chatMessage.toolCalls.orEmpty()) {
        val functionResponse = toolCall.execute()
        chatMessages.append(toolCall, functionResponse)
    }
    openAI.chatCompletions(request = ChatCompletionRequest(model = modelId, messages = chatMessages))
        .onEach { print(it.choices.first().delta.content.orEmpty()) }
        .onCompletion { println() }
        .collect()
}

/**
 * A map that associates function names with their corresponding functions.
 */
private val availableFunctions = mapOf("currentWeather" to ::callCurrentWeather)

/**
 * Example of a fake function for retrieving weather information based on location and temperature unit.
 * In a production scenario, this function could be replaced with an actual backend or external API call.
 */
private fun callCurrentWeather(args: JsonObject): String {
    val location = args.getValue("location").jsonPrimitive.content
    val unit = args["unit"]?.jsonPrimitive?.content ?: "fahrenheit"
    return currentWeather(location, unit)
}

/**
 * Example of a fake function for retrieving weather information based on location and temperature unit.
 */
private fun currentWeather(location: String, unit: String): String {
    val weatherData = mapOf(
        "Tokyo" to "10",
        "San Francisco" to "72"
    )
    val temperature = weatherData[location] ?: "22"
    val weatherInfo = WeatherInfo(location, temperature, unit)
    return Json.encodeToString(weatherInfo)
}

/**
 * Serializable data class to represent weather information.
 */
@Serializable
data class WeatherInfo(val location: String, val temperature: String, val unit: String)

/**
 * Executes a function call and returns its result.
 */
private fun ToolCall.execute(): String {
    val functionToCall = availableFunctions[function.name] ?: error("Function ${function.name} not found")
    val functionArgs = function.argumentsAsJson()
    return functionToCall(functionArgs)
}

/**
 * Appends a chat message to a list of chat messages.
 */
private fun MutableList<ChatMessage>.append(message: ChatMessage) {
    add(
        ChatMessage(
            role = message.role,
            content = message.content.orEmpty(),
            toolCalls = message.toolCalls,
            toolCallId = message.toolCallId,
        )
    )
}

/**
 * Appends a function call and response to a list of chat messages.
 */
private fun MutableList<ChatMessage>.append(toolCall: ToolCall, functionResponse: String) {
    add(
        ChatMessage(
            role = ChatRole.Tool,
            toolCallId = toolCall.id,
            name = toolCall.function.name,
            content = functionResponse
        )
    )
}
