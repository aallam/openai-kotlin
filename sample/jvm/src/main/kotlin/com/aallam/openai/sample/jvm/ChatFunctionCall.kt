package com.aallam.openai.sample.jvm

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
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
    val modelId = ModelId("gpt-3.5-turbo")
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
        functionCall = FunctionMode.Named("currentWeather") // or FunctionMode.Auto
    }

    val response = openAI.chatCompletion(request)
    val message = response.choices.first().message
    chatMessages.append(message)
    message.functionCall?.let { functionCall ->
        val functionResponse = functionCall.execute()
        chatMessages.append(functionCall, functionResponse)
        val secondResponse = openAI.chatCompletion(
            request = ChatCompletionRequest(model = modelId, messages = chatMessages)
        )
        print(secondResponse.choices.first().message.content.orEmpty())
    } ?: print(message.content.orEmpty())

    // *** Chat Completion Stream with Function Call  *** //

    println("\n> Create Chat Completion function call (stream)...")
    val chatMessage = openAI.chatCompletions(request)
        .map { completion -> completion.choices.first() }
        .fold(initial = ChatMessageAssembler()) { assembler, chunk -> assembler.merge(chunk) }
        .build()

    chatMessages.append(chatMessage)
    chatMessage.functionCall?.let { functionCall ->
        val functionResponse = functionCall.execute()
        chatMessages.append(functionCall, functionResponse)
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
 * Example dummy function for retrieving weather information based on location and temperature unit.
 * In a production scenario, this function could be replaced with an actual backend or external API call.
 */
private fun callCurrentWeather(args: JsonObject): String {
    val location = args.getValue("location").jsonPrimitive.content
    val unit = args["unit"]?.jsonPrimitive?.content ?: "fahrenheit"
    return currentWeather(location, unit)
}

/**
 * Example dummy function for retrieving weather information based on location and temperature unit.
 */
private fun currentWeather(location: String, unit: String): String {
    val weatherInfo = WeatherInfo(location, "72", unit, listOf("sunny", "windy"))
    return Json.encodeToString(weatherInfo)
}

/**
 * Serializable data class to represent weather information.
 */
@Serializable
data class WeatherInfo(val location: String, val temperature: String, val unit: String, val forecast: List<String>)


/**
 * Executes a function call and returns its result.
 */
private fun FunctionCall.execute(): String {
    val functionToCall = availableFunctions[name] ?: error("Function $name not found")
    val functionArgs = argumentsAsJson()
    return functionToCall(functionArgs)
}

/**
 * Appends a chat message to a list of chat messages.
 */
private fun MutableList<ChatMessage>.append(message: ChatMessage) {
    add(ChatMessage(role = message.role, content = message.content.orEmpty(), functionCall = message.functionCall))
}

/**
 * Appends a function call and response to a list of chat messages.
 */
private fun MutableList<ChatMessage>.append(functionCall: FunctionCall, functionResponse: String) {
    add(ChatMessage(role = ChatRole.Function, name = functionCall.name, content = functionResponse))
}

/**
 * A class to help assemble chat messages from chat chunks.
 */
class ChatMessageAssembler {
    private val chatFuncName = StringBuilder()
    private val chatFuncArgs = StringBuilder()
    private val chatContent = StringBuilder()
    private var chatRole: ChatRole? = null

    /**
     * Merges a chat chunk into the chat message being assembled.
     */
    fun merge(chunk: ChatChunk): ChatMessageAssembler {
        chatRole = chunk.delta.role ?: chatRole
        chunk.delta.content?.let { chatContent.append(it) }
        chunk.delta.functionCall?.let { call ->
            call.nameOrNull?.let { chatFuncName.append(it) }
            call.argumentsOrNull?.let { chatFuncArgs.append(it) }
        }
        return this
    }

    /**
     * Builds and returns the assembled chat message.
     */
    fun build(): ChatMessage = chatMessage {
        this.role = chatRole
        this.content = chatContent.toString()
        if (chatFuncName.isNotEmpty() || chatFuncArgs.isNotEmpty()) {
            this.functionCall = FunctionCall(chatFuncName.toString(), chatFuncArgs.toString())
            this.name = chatFuncName.toString()
        }
    }
}
