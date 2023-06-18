# Chat: Function Call

```kotlin
import com.aallam.openai.api.chat.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
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
    val message = response.choices.first().message ?: error("no response found!")
    message.functionCall?.let { functionCall ->
        val availableFunctions = mapOf("currentWeather" to ::currentWeather)
        val functionToCall = availableFunctions[functionCall.name] ?: error("Function ${functionCall.name} not found")
        val functionArgs = functionCall.argumentsAsJson() ?: error("arguments field is missing")
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
```