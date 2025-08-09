package com.aallam.openai.client

import com.aallam.openai.api.core.Parameters.Companion.buildJsonObject
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.responses.*
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlin.test.Test
import kotlin.test.assertNotNull

class TestResponses : TestOpenAI() {

    @Test
    fun basicResponse() = test {
        val response = openAI.createResponse(
            request = responseRequest {
                model = ModelId("gpt-4o")
                input = ResponseInput.from("What is the capital of France?")
            }
        )

        assertNotNull(response)
        assertNotNull(response.output)
    }

    @Test
    fun responseWithTools() = test {
        val response = openAI.createResponse(
            request = responseRequest {
                model = ModelId("gpt-4o")
                input = ResponseInput.from("What's the weather like in Paris?")
                tools {
                    add(
                        ResponseTool.Function(
                            name = "get_weather",
                            description = "Get the current weather",
                            parameters = buildJsonObject {
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
                            })
                    )
                }
            })


        assertNotNull(response)
        assertNotNull(response.output)
    }

    @Test
    fun responseWithInstructions() = test {
        val response = openAI.createResponse(
            request = responseRequest {
                model = ModelId("gpt-4o")
                input = ResponseInput.from("Tell me about artificial intelligence")
                instructions = "Provide a concise answer focusing on recent developments"
                maxOutputTokens = 200
            }
        )

        assertNotNull(response)
        assertNotNull(response.output)
    }
}
