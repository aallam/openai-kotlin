# Chat & Tool Calls

This guide demonstrates chat completions with tool calls using the Kotlin client.

## Prerequisites

- OpenAI API Kotlin client installed.
- An API key from OpenAI.

## Complete Example

```kotlin
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.serialization.json.*

suspend fun main() {
    val token = System.getenv("OPENAI_API_KEY")
    val openAI = OpenAI(token)

    val modelId = ModelId("gpt-4o-mini")
    val chatMessages = mutableListOf(
        ChatMessage(
            role = ChatRole.User,
            content = "What's the weather like in San Francisco, Tokyo, and Paris?"
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
        tools {
            function(
                name = "currentWeather",
                description = "Get the current weather in a given location",
                parameters = params
            )
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

    println(secondResponse.choices.first().message.content.orEmpty())
}

private val availableFunctions = mapOf("currentWeather" to ::callCurrentWeather)

private fun callCurrentWeather(args: JsonObject): String {
    return when (val location = args.getValue("location").jsonPrimitive.content) {
        "San Francisco" -> """{"location": "San Francisco", "temperature": "72", "unit": "fahrenheit"}"""
        "Tokyo" -> """{"location": "Tokyo", "temperature": "10", "unit": "celsius"}"""
        "Paris" -> """{"location": "Paris", "temperature": "22", "unit": "celsius"}"""
        else -> """{"location": "$location", "temperature": "unknown", "unit": "unknown"}"""
    }
}

private fun ToolCall.Function.execute(): String {
    val functionToCall = availableFunctions[function.name] ?: error("Function ${function.name} not found")
    val functionArgs = function.argumentsAsJson()
    return functionToCall(functionArgs)
}

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

private fun MutableList<ChatMessage>.append(toolCall: ToolCall.Function, functionResponse: String) {
    add(
        ChatMessage(
            role = ChatRole.Tool,
            toolCallId = toolCall.id,
            name = toolCall.function.name,
            content = functionResponse
        )
    )
}
```

## Notes

- Validate tool call arguments before executing external side effects.
- Add user confirmation for actions with real-world impact.
- Keep the tool list small and specific for better model behavior.
