package com.aallam.openai.client

import com.aallam.openai.api.chat.ChatResponseFormat
import com.aallam.openai.api.chat.Effort
import com.aallam.openai.api.chat.JsonSchema
import com.aallam.openai.api.conversation.ConversationRequest
import com.aallam.openai.api.core.Usage
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.ResponseCreateRequest
import com.aallam.openai.api.response.ResponseId
import com.aallam.openai.api.response.ResponseInput
import com.aallam.openai.api.response.ResponseReasoningOptions
import com.aallam.openai.api.response.ResponseResult
import com.aallam.openai.api.response.ResponseTextOptions
import com.aallam.openai.client.internal.reportUsage
import com.aallam.openai.client.internal.withTestCleanup
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TestResponses : TestOpenAI() {
    @Test
    fun storedResponseWithStructuredOutput() = test {
        withTestCleanup {
            val conversation = openAI.conversation(ConversationRequest())
            afterTest {
                withTestCleanup {
                    afterTest { assertTrue(openAI.delete(conversation.id)) }
                    for (item in openAI.conversationItems(conversation.id)) {
                        val itemId = assertNotNull(item.id)
                        afterTest { openAI.deleteConversationItem(conversation.id, itemId) }
                    }
                }
            }
            val schema = Json.parseToJsonElement("""{"type":"object","properties":{"name":{"type":"string"}},"required":["name"],"additionalProperties":false}""").jsonObject
            val request = responseRequest("Return JSON with name set to Ada.").copy(
                conversation = conversation.id.id,
                text = ResponseTextOptions(ChatResponseFormat.jsonSchema(JsonSchema("Name", schema = schema, strict = true))),
            )
            val response = openAI.createResponse(request)
            afterTest { assertTrue(openAI.delete(response.id)) }
            assertEquals("completed", response.status)
            assertNull(response.error)
            recordUsage(response)
            val text = response.output.flatMap { it.content.orEmpty() }.mapNotNull { it.text }.joinToString("")
            assertEquals("Ada", Json.parseToJsonElement(text).jsonObject.getValue("name").jsonPrimitive.content)
            assertEquals(response.id, openAI.retrieveResponse(response.id)?.id)
            assertTrue(openAI.responseInputItemsPage(response.id).isNotEmpty())
            assertTrue(openAI.conversationItems(conversation.id).any { it.role?.role == "assistant" })
        }
    }

    @Test
    fun streamingResponse() = test {
        withTestCleanup {
            var responseId: ResponseId? = null
            val events = openAI.streamResponses(responseRequest("Reply with hello."))
                .onEach { event ->
                    if (responseId == null) event.response?.id?.let { id ->
                        responseId = id
                        afterTest { assertTrue(openAI.delete(id)) }
                    }
                }.toList()
            assertTrue(events.any { it.type == "response.created" })
            assertTrue(events.any { it.type == "response.output_text.delta" && !it.delta.isNullOrBlank() })
            val response = assertNotNull(events.last().response)
            assertEquals("response.completed", events.last().type)
            assertEquals("completed", response.status)
            recordUsage(response)
            assertEquals(response.id, openAI.retrieveResponse(response.id)?.id)
        }
    }

    @Test
    fun backgroundResponseCancellation() = test {
        withTestCleanup {
            val response = openAI.createResponse(responseRequest("Reply with hello.").copy(background = true))
            afterTest { assertTrue(openAI.delete(response.id)) }
            var cancelled = false
            afterTest { if (!cancelled) cancelBackgroundResponse(response.id) }
            assertEquals(true, response.background)
            val final = cancelBackgroundResponse(response.id)
            cancelled = true
            assertEquals(response.id, final.id)
            assertTrue(final.status in listOf("cancelled", "completed"))
            recordUsage(final)
        }
    }

    @Test
    fun cancellingBackgroundStream() = test {
        withTestCleanup {
            val events = openAI.streamResponses(responseRequest("Reply with hello.").copy(background = true))
                .onEach { event -> event.response?.id?.let { id ->
                    afterTest { assertTrue(openAI.delete(id)) }
                    afterTest { cancelBackgroundResponse(id) }
                } }.take(1).toList()
            val event = events.single()
            assertEquals("response.created", event.type)
            assertNotNull(event.response?.id)
        }
    }

    private fun responseRequest(input: String) = ResponseCreateRequest(
        model = ModelId("gpt-6-luna"), input = ResponseInput(input), maxOutputTokens = 64,
        reasoning = ResponseReasoningOptions(effort = Effort("none")), store = true,
    )

    private suspend fun cancelBackgroundResponse(id: ResponseId): ResponseResult = try {
        assertNotNull(openAI.cancelResponse(id))
    } catch (cause: InvalidRequestException) {
        val response = assertNotNull(openAI.retrieveResponse(id))
        if (cause.statusCode != 400 || response.status != "completed") throw cause
        response
    }

    private fun recordUsage(response: ResponseResult) {
        reportUsage("responses", assertNotNull(response.model), response.usage?.let {
            Usage(it.inputTokens, it.outputTokens, it.totalTokens)
        })
    }
}
