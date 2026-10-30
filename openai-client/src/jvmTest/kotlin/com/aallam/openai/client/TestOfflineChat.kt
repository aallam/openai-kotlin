package com.aallam.openai.client

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.extension.mergeToChatMessage
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineChat {
    private val completion = """{"id":"chat_1","created":1,"model":"model","choices":[],"metadata":{"tag":"value"}}"""

    @Test
    fun storedCompletionLifecyclePreservesQueriesBodiesAndPagination() = runTest {
        val requests = mutableListOf<String>()
        val engine = MockEngine { request ->
            assertEquals("request-id", request.headers["X-Client-Request-Id"])
            val route = "${request.method.value} ${request.url.encodedPath}"
            requests += route
            val response = when (route) {
                "GET /v1/chat/completions/chat_1" -> completion
                "GET /v1/chat/completions" -> {
                    assertEquals("chat_0", request.url.parameters["after"])
                    assertEquals("2", request.url.parameters["limit"])
                    assertEquals("asc", request.url.parameters["order"])
                    assertEquals("model", request.url.parameters["model"])
                    assertEquals("value", request.url.parameters["metadata[tag]"])
                    """{"data":[$completion],"has_more":true,"first_id":"chat_1","last_id":"chat_1"}"""
                }
                "POST /v1/chat/completions/chat_1" -> {
                    val body = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
                    assertEquals("updated", body["metadata"]?.jsonObject?.get("tag")?.jsonPrimitive?.content)
                    completion
                }
                "GET /v1/chat/completions/chat_1/messages" -> {
                    assertEquals("msg_0", request.url.parameters["after"])
                    assertEquals("desc", request.url.parameters["order"])
                    """{"data":[{"id":"msg_1","role":"assistant","content":"hello"}],"has_more":false,"last_id":"msg_1"}"""
                }
                "DELETE /v1/chat/completions/chat_1" -> """{"id":"chat_1","object":"chat.completion.deleted","deleted":true}"""
                else -> error("Unexpected request $route")
            }
            respond(response, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()))
        }
        OpenAI(OpenAIConfig(token = "offline-token", engine = engine, retry = RetryStrategy(maxRetries = 0))).use { api ->
            val options = RequestOptions(headers = mapOf("X-Client-Request-Id" to "request-id"))
            assertEquals("chat_1", api.chatCompletion("chat_1", options)?.id)
            val page = api.chatCompletions(ChatCompletionListQuery("chat_0", 2, mapOf("tag" to "value"), ModelId("model"), SortOrder.Ascending), options)
            assertEquals(true, page.hasMore)
            assertEquals("chat_1", page.lastId)
            assertEquals("chat_1", api.updateChatCompletion("chat_1", ChatCompletionUpdate(mapOf("tag" to "updated")), options).id)
            val messages = api.chatCompletionMessages("chat_1", "msg_0", 2, SortOrder.Descending, options)
            assertEquals("msg_1", messages.single().id)
            assertEquals("hello", messages.single().content)
            assertTrue(api.deleteChatCompletion("chat_1", options))
        }
        assertEquals(5, requests.size)
    }

    @Test
    fun advancedOptionsMergeIntoRequestsAndDecodeCustomCallsAndDeltas() = runTest {
        val result = """{"id":"chat_1","created":1,"model":"model","choices":[{"index":0,"message":{"role":"assistant","tool_calls":[{"type":"custom","id":"call_1","custom":{"name":"calculate","input":"42"}}]}}]}"""
        val engine = MockEngine { request ->
            val wire = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
            assertEquals("tenant", wire["metadata"]?.jsonObject?.get("owner")?.jsonPrimitive?.content)
            assertEquals("custom", wire["tools"]!!.jsonArray[0].jsonObject["type"]?.jsonPrimitive?.content)
            assertEquals("file", wire["messages"]!!.jsonArray[0].jsonObject["content"]!!.jsonArray[0].jsonObject["type"]?.jsonPrimitive?.content)
            if (wire["stream"]?.jsonPrimitive?.boolean == true) {
                respond("data: ${result.replace("\"message\"", "\"delta\"")}\n\ndata: [DONE]\n\n", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "text/event-stream"))
            } else {
                respond(result, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
            }
        }
        OpenAI(OpenAIConfig(token = "offline-token", engine = engine)).use { api ->
            val request = ChatCompletionRequest(ModelId("model"), listOf(ChatMessage.User("legacy")), metadata = mapOf("owner" to "tenant"))
            val options = ChatCompletionOptions(
                messages = listOf(ChatInputMessage(ChatRole.User, ChatInputParts(listOf(ChatInputPart.File(ChatInputFile(fileId = "file_1")))))),
                tools = listOf(CustomTool(CustomToolDefinition("calculate"))),
            )
            assertEquals("42", api.chatCompletion(request, options).choices.single().message?.toolCalls?.single()?.custom?.input)
            assertEquals("42", api.chatCompletions(request, options).toList().single().choices.single().delta?.toolCalls?.single()?.custom?.input)
        }
    }

    @Test
    fun storedMessageListingPreservesNullContentAndMultimodalParts() = runTest {
        val engine = MockEngine {
            respond("""{"data":[{"id":"msg_1","role":"user","name":"reader","content":null,"content_parts":[
              {"type":"text","text":"read these"},
              {"type":"image_url","image_url":{"url":"https://example.com/image.png","detail":"high"}},
              {"type":"input_audio","input_audio":{"data":"YXVkaW8=","format":"wav"}},
              {"type":"file","file":{"file_id":"file_123","filename":"example.pdf"}}
            ]}],"has_more":false}""", HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        OpenAI(OpenAIConfig(token = "offline-token", engine = engine)).use { api ->
            val message = api.chatCompletionMessages("chat_1").single()
            assertNull(message.content)
            assertEquals("reader", message.name)
            val parts = assertNotNull(message.contentParts)
            assertEquals("read these", assertIs<ChatInputPart.Text>(parts[0]).text)
            assertEquals("high", assertIs<ChatInputPart.Image>(parts[1]).imageUrl.detail)
            assertEquals("wav", assertIs<ChatInputPart.Audio>(parts[2]).inputAudio.format)
            assertEquals("file_123", assertIs<ChatInputPart.File>(parts[3]).file.fileId)
        }
    }

    @Test
    fun missingStoredCompletionReturnsNullOrFalse() = runTest {
        val engine = MockEngine {
            respond("""{"error":{"message":"missing","type":"invalid_request_error","param":null,"code":null}}""", HttpStatusCode.NotFound, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        OpenAI(OpenAIConfig(token = "offline-token", engine = engine, retry = RetryStrategy(maxRetries = 0))).use { api ->
            assertNull(api.chatCompletion("missing"))
            assertFalse(api.deleteChatCompletion("missing"))
        }
    }
}

/** Existing implementations only need to provide the original two operations. */
private class LegacyChatImplementation : Chat {
    override suspend fun chatCompletion(request: ChatCompletionRequest, requestOptions: RequestOptions?): ChatCompletion =
        error("Unused source compatibility fixture")

    override fun chatCompletions(request: ChatCompletionRequest, requestOptions: RequestOptions?): kotlinx.coroutines.flow.Flow<ChatCompletionChunk> =
        kotlinx.coroutines.flow.emptyFlow()
}
