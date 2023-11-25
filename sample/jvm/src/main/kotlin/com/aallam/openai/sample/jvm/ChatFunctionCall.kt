package com.aallam.openai.sample.jvm

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.serialization.json.*

/**
 * This code snippet demonstrates the use of OpenAI's chat completion capabilities
 * with a focus on integrating function calls into the chat conversation.
 */
suspend fun chatFunctionCall(openAI: OpenAI) {
    // *** Chat Completion with Function Call  *** //

    println("\n> Create Chat Completion function call...")
    val modelId = ModelId("gpt-3.5-turbo-1106")
    val chatMessages = mutableListOf(chatMessage {
        role = ChatRole.User
        content = "What's the weather like in San Francisco, Tokyo, and Paris?"
    })
    val request = chatCompletionRequest {
        model = modelId
        messages = chatMessages
        tools {
            function(
                name = "currentWeather",
                description = "Get the current weather in a given location",
            ) {
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
        }
        toolChoice = ToolChoice.Auto // or ToolChoice.function("currentWeather")
    }

    val response = openAI.chatCompletion(request)
    val message = response.choices.first().message
    chatMessages.append(message)
    for (toolCall in message.toolCalls.orEmpty()) {
        require(toolCall is ToolCall.Function) { "Tool call is not a function" }
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
            role = ChatRole.User, content = "What's the weather like in San Francisco, Tokyo, and Paris?"
        )
    )
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
private fun MutableList<ChatMessage>.append(toolCall: ToolCall.Function, functionResponse: String) {
    val message = ChatMessage(
        role = ChatRole.Tool,
        toolCallId = toolCall.id,
        name = toolCall.function.name,
        content = functionResponse
    )
    add(message)
}
