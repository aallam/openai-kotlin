# Chat & Tool Calls: Function Call

This guide is designed to demonstrate the interaction with OpenAI API using the Kotlin language to execute a chat completion request with function calls.

## Prerequisites

- OpenAI API Kotlin client installed.
- An API key from OpenAI.

## Overview

We'll use the `gpt-3.5-turbo-1106` model to create a chatbot that responds to a user's query about the weather in Boston.
We'll send a chat completion request which includes a function `currentWeather` that the model can call.

## Step-by-step Instructions

### Setting Up the OpenAI Instance

Firstly, we initialize an instance of the OpenAI class, providing it with the OpenAI API key.

```kotlin
val token = System.getenv("OPENAI_API_KEY")
val openAI = OpenAI(token)
```

### Defining the Model

Specify the model to use for the chat request.

```kotlin
val modelId = ModelId("gpt-3.5-turbo-1106")
```

### Defining the Function

Define a fake function `currentWeather` which the model might call.
This function will return hardcoded weather information.

```kotlin
/**
 * A map that associates function names with their corresponding functions.
 */
private val availableFunctions = mapOf("currentWeather" to ::callCurrentWeather)

/**
 * Example of a fake function for retrieving weather information based on location and temperature unit.
 * In a production scenario, this function could be replaced with an actual backend or external API call.
 */
private fun callCurrentWeather(args: JsonObject): String {
    return when (val location = args.getValue("location").jsonPrimitive.content) {
        "San Francisco" -> """"{"location": "San Francisco", "temperature": "72", "unit": "fahrenheit"}"""
        "Tokyo" -> """{"location": "Tokyo", "temperature": "10", "unit": "celsius"}"""
        "Paris" -> """{"location": "Paris", "temperature": "22", "unit": "celsius"}"""
        else -> """{"location": "$location", "temperature": "unknown", "unit": "unknown"}"""
    }
}
```

### Defining Function Parameters

Define the parameters for the function the model might call.

```kotlin
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
```

### Setting Up the Chat Messages

Start with a user message inquiring about the weather in Boston.

```kotlin
val chatMessages = mutableListOf(
    ChatMessage(
        role = ChatRole.User,
        content = "What's the weather like in San Francisco, Tokyo, and Paris?"
    )
)
```

### Creating the Chat Completion Request

Create a chat completion request that includes the model, messages, tools and function calls.

```kotlin
val request = chatCompletionRequest {
    model = modelId
    messages = chatMessages
    tools {
        function(
            name = "currentWeather",
            description = "Get the current weather in a given location",
            parameters = parameters
        )
    }
    toolChoice = ToolChoice.Auto // or ToolChoice.function("currentWeather")
}
```

### Sending the Request and Handling the Response

1. Send the chat request and handle the response.
```kotlin
val response = openAI.chatCompletion(request)
val message = response.choices.first().message
```
2. Add the response message to the chat messages list.
```kotlin
chatMessages.append(message)
```
3. If there's a function call in the response, execute it and send a second chat completion request.
```kotlin
for (toolCall in message.toolCalls.orEmpty()) {
    require(toolCall is ToolCall.Function) { "Tool call is not a function" }
    val functionResponse = toolCall.execute()
    chatMessages.append(toolCall, functionResponse)
}

/**
 * Executes a function call and returns its result.
 */
private fun ToolCall.Function.execute(): String {
    val functionToCall = availableFunctions[function.name] ?: error("Function ${function.name} not found")
    val functionArgs = function.argumentsAsJson()
    return functionToCall(functionArgs)
}
```

#### Notes

- You should validate and handle the function calls made by the model carefully as the model could generate invalid JSON or hallucinate parameters.
- It's recommended to implement user confirmation flows before executing actions that could have an impact on the real world.

### Complete Example

Below is a complete Kotlin example following the guide:

```kotlin

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.serialization.json.*

/**
 * This code snippet demonstrates the use of OpenAI's chat completion capabilities
 * with a focus on integrating function calls into the chat conversation.
 */
suspend fun main() {
    val token = System.getenv("OPENAI_API_KEY")
    val openAI = OpenAI(token)

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
    return when (val location = args.getValue("location").jsonPrimitive.content) {
        "San Francisco" -> """"{"location": "San Francisco", "temperature": "72", "unit": "fahrenheit"}"""
        "Tokyo" -> """{"location": "Tokyo", "temperature": "10", "unit": "celsius"}"""
        "Paris" -> """{"location": "Paris", "temperature": "22", "unit": "celsius"}"""
        else -> """{"location": "$location", "temperature": "unknown", "unit": "unknown"}"""
    }
}

/**
 * Executes a function call and returns its result.
 */
private fun ToolCall.Function.execute(): String {
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
private fun MutableList<ChatMessage>.append(toolCall: ToolCall.Function, functionResponse: String) {
    val message = ChatMessage(
        role = ChatRole.Tool,
        toolCallId = toolCall.id,
        name = toolCall.function.name,
        content = functionResponse
    )
    add(message)
}
```

This completes the guide for executing a chat completion request with a function call. Happy coding!
