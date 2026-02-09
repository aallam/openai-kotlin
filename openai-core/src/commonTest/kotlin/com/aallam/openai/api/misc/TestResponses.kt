package com.aallam.openai.api.misc

import com.aallam.openai.api.chat.ChatResponseFormat
import com.aallam.openai.api.chat.Effort
import com.aallam.openai.api.chat.SearchContextSize
import com.aallam.openai.api.chat.UserLocation
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TestResponses {

    @Test
    fun responseRequestSerializesExpectedFields() {
        val request = ResponseRequest(
            model = ModelId("gpt-4.1"),
            input = ResponseInput("hello"),
            reasoning = ResponseReasoning(effort = Effort("medium")),
            text = ResponseText(format = ChatResponseFormat.Text),
            tools = listOf(
                ResponseTool(
                    type = "web_search_preview",
                    searchContextSize = SearchContextSize.High,
                    userLocation = UserLocation()
                )
            ),
            store = true,
            maxOutputTokens = 128
        )

        val encoded = Json.encodeToJsonElement(ResponseRequest.serializer(), request).jsonObject
        assertEquals("gpt-4.1", encoded["model"]?.jsonPrimitive?.content)
        assertEquals("hello", encoded["input"]?.jsonPrimitive?.content)
        assertEquals("high", encoded["tools"]?.jsonArray?.first()?.jsonObject?.get("search_context_size")?.jsonPrimitive?.content)
    }

    @Test
    fun responsePayloadSupportsReasoningContent() {
        val payload = """
            {
              "id":"resp_123",
              "object":"response",
              "model":"gpt-4.1",
              "status":"completed",
              "output":[
                {
                  "id":"msg_1",
                  "type":"message",
                  "role":"assistant",
                  "status":"completed",
                  "content":[
                    {
                      "type":"output_text",
                      "text":"answer",
                      "reasoning_content":"internal reasoning"
                    }
                  ]
                }
              ],
              "output_text":"answer"
            }
        """.trimIndent()

        val response = Json { ignoreUnknownKeys = true }.decodeFromString(Response.serializer(), payload)
        assertEquals("resp_123", response.id.id)
        assertEquals("completed", response.status)
        assertEquals("internal reasoning", response.output.first().content?.first()?.reasoningContent)
        assertEquals("answer", response.outputText)
        assertNotNull(response.output.first().content)
    }
}
