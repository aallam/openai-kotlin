package com.aallam.openai.client

import com.aallam.openai.api.conversation.*
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.api.logging.LogLevel
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.http.content.TextContent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineConversations {
    private val conversationJson = """{"id":"conv_1","created_at":123,"metadata":null,"object":"conversation"}"""
    private val itemJson = """{"id":"msg_1","type":"message","role":"user","content":[{"type":"input_text","text":"Hello"}]}"""
    private val pageJson = """{"data":[$itemJson],"first_id":"msg_1","last_id":"msg_1","has_more":true,"object":"list"}"""

    @Test
    fun conversationLifecycleUsesResourcePathsAndExplicitMetadataNull() = runTest {
        val methods = mutableListOf<HttpMethod>()
        val engine = MockEngine { request ->
            methods += request.method
            assertEquals("/v1/conversations", request.url.encodedPath.substringBefore("/conv_1"))
            when {
                request.method == HttpMethod.Post && request.url.encodedPath.endsWith("/conv_1") -> {
                    assertEquals(JsonNull, Json.parseToJsonElement((request.body as TextContent).text).jsonObject["metadata"])
                    assertEquals("value", request.headers["X-Test"])
                    respond(conversationJson, headers = headersOf(HttpHeaders.ContentType, "application/json"))
                }
                request.method == HttpMethod.Post -> {
                    val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
                    assertEquals("Hello", body["items"]!!.jsonArray.first().jsonObject["content"]!!.jsonPrimitive.content)
                    respond(conversationJson, headers = headersOf(HttpHeaders.ContentType, "application/json"))
                }
                request.method == HttpMethod.Delete -> respond("""{"id":"conv_1","object":"conversation.deleted","deleted":true}""", headers = headersOf(HttpHeaders.ContentType, "application/json"))
                else -> respond(conversationJson, headers = headersOf(HttpHeaders.ContentType, "application/json"))
            }
        }
        OpenAI(OpenAIConfig(token = "test", engine = engine, logging = LoggingConfig(LogLevel.None))).use { client ->
            val created = client.conversation(ConversationRequest(listOf(ConversationItem.message(Role.User, "Hello"))))
            assertEquals(ConversationId("conv_1"), created.id)
            assertEquals(created.id, client.conversation(created.id)!!.id)
            client.updateConversation(created.id, ConversationUpdateRequest(null), RequestOptions(headers = mapOf("X-Test" to "value")))
            assertTrue(client.delete(created.id))
        }
        assertEquals(listOf(HttpMethod.Post, HttpMethod.Get, HttpMethod.Post, HttpMethod.Delete), methods)
    }

    @Test
    fun itemLifecyclePreservesPagesIncludesAndDeleteResponse() = runTest {
        val methods = mutableListOf<HttpMethod>()
        val engine = MockEngine { request ->
            methods += request.method
            if (request.url.encodedPath.endsWith("/msg_1")) {
                if (request.method == HttpMethod.Get) {
                    assertEquals(listOf("message.output_text.logprobs", "reasoning.encrypted_content"), request.url.parameters.getAll("include[]"))
                }
                respond(if (request.method == HttpMethod.Delete) conversationJson else itemJson, headers = headersOf(HttpHeaders.ContentType, "application/json"))
            } else {
                assertEquals("/v1/conversations/conv_1/items", request.url.encodedPath)
                assertEquals(listOf("message.output_text.logprobs", "reasoning.encrypted_content"), request.url.parameters.getAll("include[]"))
                if (request.method == HttpMethod.Get) {
                    assertEquals("msg_0", request.url.parameters["after"])
                    assertEquals("2", request.url.parameters["limit"])
                    assertEquals("asc", request.url.parameters["order"])
                }
                respond(pageJson, headers = headersOf(HttpHeaders.ContentType, "application/json"))
            }
        }
        OpenAI(OpenAIConfig(token = "test", engine = engine, logging = LoggingConfig(LogLevel.None))).use { client ->
            val id = ConversationId("conv_1")
            val include = listOf("message.output_text.logprobs", "reasoning.encrypted_content")
            val added = client.createConversationItems(id, ConversationItemsRequest(listOf(ConversationItem.message(Role.User, "Hello"))), include)
            assertTrue(added.hasMore!!)
            assertEquals("msg_1", added.lastId)
            assertEquals("msg_1", client.conversationItem(id, "msg_1", include)!!.id)
            val page = client.conversationItems(id, limit = 2, order = SortOrder.Ascending, after = "msg_0", include = include)
            assertEquals("message", page.first().type)
            assertEquals(id, client.deleteConversationItem(id, "msg_1").id)
        }
        assertEquals(listOf(HttpMethod.Post, HttpMethod.Get, HttpMethod.Get, HttpMethod.Delete), methods)
    }

    @Test
    fun customClientsCanOmitConversationOperations() = runTest {
        // Compiling this implementation verifies that no Conversations methods are required.
        val customClient = object : Conversations {}
        val id = ConversationId("conv_1")
        assertFailsWith<UnsupportedOperationException> { customClient.conversation() }
        assertFailsWith<UnsupportedOperationException> { customClient.conversation(id) }
        assertFailsWith<UnsupportedOperationException> {
            customClient.updateConversation(id, ConversationUpdateRequest(null))
        }
        assertFailsWith<UnsupportedOperationException> { customClient.delete(id) }
        assertFailsWith<UnsupportedOperationException> {
            customClient.createConversationItems(id, ConversationItemsRequest(emptyList()))
        }
        assertFailsWith<UnsupportedOperationException> { customClient.conversationItem(id, "msg_1") }
        assertFailsWith<UnsupportedOperationException> { customClient.deleteConversationItem(id, "msg_1") }
        assertFailsWith<UnsupportedOperationException> { customClient.conversationItems(id) }
    }

    @Test
    fun missingResourcesAreNullableButOtherErrorsPropagate() = runTest {
        var status = HttpStatusCode.NotFound
        val engine = MockEngine {
            respond("""{"error":{"message":"missing","type":"invalid_request_error","code":null,"param":null}}""", status, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        OpenAI(OpenAIConfig(token = "test", engine = engine, retry = RetryStrategy(maxRetries = 0), logging = LoggingConfig(LogLevel.None))).use { client ->
            assertNull(client.conversation(ConversationId("missing")))
            assertNull(client.conversationItem(ConversationId("missing"), "missing"))
            assertFalse(client.delete(ConversationId("missing")))
            status = HttpStatusCode.Unauthorized
            assertFailsWith<AuthenticationException> { client.conversation(ConversationId("missing")) }
        }
    }
}
