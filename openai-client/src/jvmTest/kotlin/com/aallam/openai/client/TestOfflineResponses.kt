package com.aallam.openai.client

import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.http.content.TextContent
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineResponses {
    @Test
    fun createStreamUsesSseAndRetainsMultilineEvents() = runTest {
        val client = OpenAI(OpenAIConfig(token = "test", engine = MockEngine { request ->
            assertEquals("/v1/responses", request.url.encodedPath)
            assertEquals("text/event-stream", request.headers[HttpHeaders.Accept])
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertEquals(true, body["stream"]?.jsonPrimitive?.boolean)
            respond(
                "event: response.output_text.delta\ndata: {\"type\":\"response.output_text.delta\",\ndata: \"delta\":\"hello\",\"sequence_number\":1}\n\nevent: response.completed\ndata: {\"type\":\"response.completed\",\"response\":{\"id\":\"resp_1\"}}\n\n",
                headers = headersOf(HttpHeaders.ContentType, "text/event-stream"),
            )
        }))
        try {
            val events = client.responses(ResponseRequest(ModelId("model"), ResponseInput("hello"))).toList()
            assertEquals("hello", events.first().delta)
            assertEquals("resp_1", events.last().response?.id?.id)
        } finally { client.close() }
    }

    @Test
    fun resumeStreamSendsCursorAndIncludeOptions() = runTest {
        val client = OpenAI(OpenAIConfig(token = "test", engine = MockEngine { request ->
            assertEquals(HttpMethod.Get, request.method)
            assertEquals("5", request.url.parameters["starting_after"])
            assertEquals(listOf("reasoning.encrypted_content", "file_search_call.results"), request.url.parameters.getAll("include[]"))
            assertEquals("false", request.url.parameters["include_obfuscation"])
            respond("data: {\"type\":\"response.output_text.delta\",\"delta\":\"first\"}\n\ndata: {\"type\":\"response.completed\"}\n\n", headers = headersOf(HttpHeaders.ContentType, "text/event-stream"))
        }))
        try {
            val events = client.responses(ResponseId("resp_1"), 5, listOf("reasoning.encrypted_content", "file_search_call.results"), false).take(1).toList()
            assertEquals("first", events.single().delta)
        } finally { client.close() }
    }

    @Test
    fun compactionAndTokenCountUseDedicatedPayloads() = runTest {
        val client = OpenAI(OpenAIConfig(token = "test", engine = MockEngine { request ->
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertFalse("store" in body)
            val result = when (request.url.encodedPath) {
                "/v1/responses/compact" -> """{"id":"compact_1","output":[{"type":"compaction","encrypted_content":"opaque"}]}"""
                "/v1/responses/input_tokens" -> """{"object":"response.input_tokens","input_tokens":42}"""
                else -> error("Unexpected path: ${request.url}")
            }
            respond(result, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }))
        try {
            assertEquals("opaque", client.compactResponse(ResponseCompactRequest(ModelId("model"), ResponseInput("history"))).output.single()["encrypted_content"]?.jsonPrimitive?.content)
            assertEquals(42, client.responseInputTokens(ResponseTokenCountRequest(ModelId("model"), ResponseInput("history"))).inputTokens)
        } finally { client.close() }
    }

    @Test
    fun streamApiErrorsRemainObservable() = runTest {
        val client = OpenAI(OpenAIConfig(token = "test", engine = MockEngine {
            respond("data: {\"type\":\"error\",\"code\":\"server_error\",\"message\":\"retry later\"}\n\n", headers = headersOf(HttpHeaders.ContentType, "text/event-stream"))
        }))
        try {
            val error = client.responses(ResponseRequest(ModelId("model"))).toList().single()
            assertEquals("error", error.type)
            assertEquals("retry later", error.data["message"]?.jsonPrimitive?.content)
        } finally { client.close() }
    }
    @Test
    fun missingResponsesReturnNullOrFalseWhilePermissionErrorsPropagate() = runTest {
        for (status in listOf(HttpStatusCode.NotFound, HttpStatusCode.Forbidden)) {
            val client = OpenAI(OpenAIConfig(token = "test", engine = MockEngine {
                respond(
                    """{"error":{"message":"unavailable","type":"invalid_request_error","param":null,"code":"unavailable"}}""",
                    status = status,
                    headers = headersOf(HttpHeaders.ContentType, "application/json"),
                )
            }))
            try {
                val id = ResponseId("missing")
                if (status == HttpStatusCode.NotFound) {
                    assertNull(client.response(id))
                    assertNull(client.response(id, listOf("reasoning.encrypted_content")))
                    assertNull(client.cancel(id))
                    assertFalse(client.delete(id))
                } else {
                    assertFailsWith<com.aallam.openai.api.exception.PermissionException> { client.response(id) }
                    assertFailsWith<com.aallam.openai.api.exception.PermissionException> { client.response(id, emptyList()) }
                    assertFailsWith<com.aallam.openai.api.exception.PermissionException> { client.cancel(id) }
                    assertFailsWith<com.aallam.openai.api.exception.PermissionException> { client.delete(id) }
                }
            } finally { client.close() }
        }
    }

    @Test
    fun inputItemsIncludesAreRepeatedAlongsidePagination() = runTest {
        val client = OpenAI(OpenAIConfig(token = "test", engine = MockEngine { request ->
            assertEquals("/v1/responses/resp_1/input_items", request.url.encodedPath)
            assertEquals(listOf("reasoning.encrypted_content", "file_search_call.results"), request.url.parameters.getAll("include[]"))
            assertEquals("3", request.url.parameters["limit"])
            assertEquals("item_1", request.url.parameters["after"])
            respond("""{"object":"list","data":[{"type":"function_call_output","call_id":"call_1","output":"result"}],"has_more":false,"first_id":"item_2","last_id":"item_2"}""", headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }))
        try {
            val items = client.responseInputItems(ResponseId("resp_1"), listOf("reasoning.encrypted_content", "file_search_call.results"), limit = 3, after = "item_1")
            assertEquals("call_1", items.data.single().callId)
            assertEquals("result", items.data.single().output?.jsonPrimitive?.content)
        } finally { client.close() }
    }

}

/** Compiles an implementation of the pre-extension Responses interface. */
@Suppress("unused")
private class OriginalResponsesImplementation : Responses {
    override suspend fun response(request: ResponseRequest, requestOptions: com.aallam.openai.api.core.RequestOptions?): Response = error("unused")
    override suspend fun response(id: ResponseId, requestOptions: com.aallam.openai.api.core.RequestOptions?): Response? = null
    override suspend fun delete(id: ResponseId, requestOptions: com.aallam.openai.api.core.RequestOptions?): Boolean = false
    override suspend fun cancel(id: ResponseId, requestOptions: com.aallam.openai.api.core.RequestOptions?): Response? = null
    override suspend fun responseInputItems(
        id: ResponseId,
        limit: Int?,
        order: com.aallam.openai.api.core.SortOrder?,
        after: String?,
        before: String?,
        requestOptions: com.aallam.openai.api.core.RequestOptions?,
    ): com.aallam.openai.api.core.PaginatedList<ResponseInputItem> = error("unused")
}
