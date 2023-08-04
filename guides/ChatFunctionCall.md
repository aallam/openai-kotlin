# Chat with Function Call

This guide is designed to demonstrate the interaction with OpenAI API using the Kotlin language to execute a chat completion request with function calls.

## Prerequisites

- The OpenAI Kotlin SDK installed.
- An API key from OpenAI.

## Overview

We'll use the `gpt-3.5-turbo-0613` model to create a chatbot that responds to a user's query about the weather in Boston. We'll send a chat completion request which includes a function `currentWeather` that the model can call.

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
val modelId = ModelId("gpt-3.5-turbo-0613")
```

### Defining the Function

Define a dummy function `currentWeather` which the model might call. This function will return hardcoded weather information.

```kotlin
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
        content = "What's the weather like in Boston?"
    )
)
```

### Creating the Chat Completion Request

Create a chat completion request that includes the model, messages, functions, and function call mode.

```kotlin
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
```

### Sending the Request and Handling the Response

Send the chat request and handle the response. If there's a function call in the response, execute it and send a second chat completion request.

```kotlin
val response = openAI.chatCompletion(request)
val message = response.choices.first().message
message.functionCall?.let { functionCall ->
    val availableFunctions = mapOf("currentWeather" to ::currentWeather)
    val functionToCall = availableFunctions[functionCall.name] ?: error("Function ${functionCall.name} not found")
    val functionArgs = functionCall.argumentsAsJson()
    val functionResponse = functionToCall(
        functionArgs.getValue("location").jsonPrimitive.content,
        functionArgs["unit"]?.jsonPrimitive?.content ?: "fahrenheit"
    )
    chatMessages.add(
        ChatMessage(
            role = message.role,
            content = message.content ?: "", // required to not be empty in this case
            functionCall = message.functionCall
        )
    )
    chatMessages.add(
        ChatMessage(
            role = ChatRole.Function, 
            name = functionCall.name, 
            content = functionResponse
        )
    )

    val secondRequest = chatCompletionRequest {
        model = modelId
        messages = chatMessages
    }

    val secondResponse = openAI.chatCompletion(secondRequest)
    println(secondResponse.choices.first().message.content)
} ?: println(message.content)
```

#### Notes

- You should validate and handle the function calls made by the model carefully as the model could generate invalid JSON or hallucinate parameters.
- It's recommended to implement user confirmation flows before executing actions that could have an impact on the real world.

### Complete Example

Below is a complete Kotlin example following the guide:

```kotlin
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.serialization.*
import kotlinx.serialization.json.*

suspend fun main() {
    val token = System.getenv("OPENAI_API_KEY")
    val openAI = OpenAI(token)

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
        val availableFunctions = mapOf("currentWeather" to ::currentWeather)
        val functionToCall = availableFunctions[functionCall.name] ?: error("Function ${functionCall.name} not found")
        val functionArgs = functionCall.argumentsAsJson()
        val functionResponse = functionToCall(
            functionArgs.getValue("location").jsonPrimitive.content,
            functionArgs["unit"]?.jsonPrimitive?.content ?: "fahrenheit"
        )

        chatMessages.add(
            ChatMessage(
                role = message.role,
                content = message.content ?: "",
                functionCall = message.functionCall
            )
        )

        chatMessages.add(
            ChatMessage(
                role = ChatRole.Function,
                name = functionCall.name,
                content = functionResponse
            )
        )

        val secondRequest = chatCompletionRequest {
            model = modelId
            messages = chatMessages
        }

        val secondResponse = openAI.chatCompletion(secondRequest)
        println(secondResponse.choices.first().message.content)
    } ?: println(message.content)
}

@Serializable
data class WeatherInfo(val location: String, val temperature: String, val unit: String, val forecast: List<String>)

fun currentWeather(location: String, unit: String): String {
    val weatherInfo = WeatherInfo(location, "72", unit, listOf("sunny", "windy"))
    return Json.encodeToString(weatherInfo)
}
```

This completes the guide for executing a chat completion request with a function call. Happy coding!
