package com.aallam.openai.sample.jvm

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

suspend fun chatFunctionCall(openAI: OpenAI) {
    // *** Chat Completion with Function Call  *** //

    println("\n> Create Chat Completion function call...")
    val modelId = ModelId("gpt-3.5-turbo-0613")
    val chatMessages = mutableListOf(
        ChatMessage(
            role = ChatRole.User,
            content = "What's the weather like in Boston?"
        )
    )

    val params = Parameters.buildJsonObject {
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
    val message = response.choices.first().message
    message.functionCall?.let { functionCall ->
        val functionResponse = callFunction(functionCall)
        updateChatMessages(chatMessages, message, functionCall, functionResponse)
        val secondResponse = openAI.chatCompletion(
            request = ChatCompletionRequest(
                model = modelId,
                messages = chatMessages,
            )
        )
        print(secondResponse)
    }

    // *** Chat Completion Stream with Function Call  *** //

    println("\n> Create Chat Completion function call (stream)...")
    val chunks = mutableListOf<ChatChunk>()
    openAI.chatCompletions(request)
        .onEach { chunks += it.choices.first() }
        .onCompletion {
            val chatMessage = chatMessageOf(chunks)
            chatMessage.functionCall?.let {
                val functionResponse = callFunction(it)
                updateChatMessages(chatMessages, message, it, functionResponse)
            }
        }
        .collect()

    openAI.chatCompletions(
        ChatCompletionRequest(
            model = modelId,
            messages = chatMessages,
        )
    )
        .onEach { print(it.choices.first().delta.content.orEmpty()) }
        .onCompletion { println() }
        .collect()
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

private fun callFunction(functionCall: FunctionCall): String {
    val availableFunctions = mapOf("currentWeather" to ::currentWeather)
    val functionToCall = availableFunctions[functionCall.name] ?: error("Function ${functionCall.name} not found")
    val functionArgs = functionCall.argumentsAsJson()

    return functionToCall(
        functionArgs.getValue("location").jsonPrimitive.content,
        functionArgs["unit"]?.jsonPrimitive?.content ?: "fahrenheit"
    )
}

private fun updateChatMessages(
    chatMessages: MutableList<ChatMessage>,
    message: ChatMessage,
    functionCall: FunctionCall,
    functionResponse: String
) {
    chatMessages.add(
        ChatMessage(
            role = message.role,
            content = message.content.orEmpty(), // required to not be empty in this case
            functionCall = message.functionCall
        )
    )
    chatMessages.add(
        ChatMessage(role = ChatRole.Function, name = functionCall.name, content = functionResponse)
    )
}

fun chatMessageOf(chunks: List<ChatChunk>): ChatMessage {
    val funcName = StringBuilder()
    val funcArgs = StringBuilder()
    var role: ChatRole? = null
    val content = StringBuilder()

    chunks.forEach { chunk ->
        role = chunk.delta.role ?: role
        chunk.delta.content?.let { content.append(it) }
        chunk.delta.functionCall?.let { call ->
            funcName.append(call.name)
            funcArgs.append(call.arguments)
        }
    }

    return chatMessage {
        this.role = role
        this.content = content.toString()
        if (funcName.isNotEmpty() || funcArgs.isNotEmpty()) {
            functionCall = FunctionCall(funcName.toString(), funcArgs.toString())
            name = funcName.toString()
        }
    }
}
