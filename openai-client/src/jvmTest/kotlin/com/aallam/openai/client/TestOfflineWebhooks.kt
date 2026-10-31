package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.webhook.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineWebhooks {
    @Test
    fun lifecycleUsesDocumentedPathsMethodsBodiesAndPagination() = runTest {
        var call = 0
        val engine = MockEngine { request ->
            assertEquals("marker", request.headers["X-Test"])
            val response = when (call++) {
                0 -> {
                    assertEquals(HttpMethod.Post, request.method)
                    assertEquals("/v1/webhook_endpoints", request.url.encodedPath)
                    assertEquals(Json.parseToJsonElement("""{"event_types":["response.completed"],"name":"receiver","url":"https://example.com/hook"}"""), body(request))
                    endpointWithSecret
                }
                1 -> {
                    assertEquals(HttpMethod.Get, request.method)
                    assertEquals("/v1/webhook_endpoints/hook_1", request.url.encodedPath)
                    endpoint
                }
                2 -> {
                    assertEquals(HttpMethod.Post, request.method)
                    assertEquals("/v1/webhook_endpoints/hook_1", request.url.encodedPath)
                    assertEquals(Json.parseToJsonElement("""{"name":"updated"}"""), body(request))
                    endpoint
                }
                3 -> {
                    assertEquals(HttpMethod.Get, request.method)
                    assertEquals("/v1/webhook_endpoints", request.url.encodedPath)
                    assertEquals("hook_0", request.url.parameters["after"])
                    assertEquals("2", request.url.parameters["limit"])
                    """{"data":[$endpoint],"has_more":true,"first_id":"hook_1","last_id":"hook_1","object":"list"}"""
                }
                4 -> {
                    assertEquals(HttpMethod.Post, request.method)
                    assertEquals("/v1/webhook_endpoints/hook_1/rotate_secret", request.url.encodedPath)
                    assertEquals(Json.parseToJsonElement("""{"keep_old_secret_active_for_24_hours":true}"""), body(request))
                    endpointWithSecret
                }
                5 -> {
                    assertEquals(HttpMethod.Post, request.method)
                    assertEquals("/v1/webhook_endpoints/hook_1/test", request.url.encodedPath)
                    assertEquals(Json.parseToJsonElement("""{"event_type":"response.completed"}"""), body(request))
                    """{"event_type":"response.completed","status_code":503,"success":true,"webhook_endpoint_id":"hook_1","object":"webhook_endpoint.test"}"""
                }
                6 -> {
                    assertEquals(HttpMethod.Delete, request.method)
                    assertEquals("/v1/webhook_endpoints/hook_1", request.url.encodedPath)
                    """{"id":"hook_1","deleted":true,"object":"webhook_endpoint.deleted"}"""
                }
                else -> error("Unexpected request")
            }
            respond(response, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val client = client(engine)
        val options = RequestOptions(headers = mapOf("X-Test" to "marker"))
        try {
            val created = client.webhookEndpoint(WebhookEndpointRequest(listOf("response.completed"), "receiver", "https://example.com/hook"), options)
            assertEquals("whsec_test", created.signingSecret)
            assertFalse(created.toString().contains("whsec_test"))
            assertNull(client.webhookEndpoint("hook_1", options)?.signingSecret)
            client.updateWebhookEndpoint("hook_1", WebhookEndpointUpdate(name = "updated"), options)
            val page = client.webhookEndpoints("hook_0", 2, options)
            assertEquals(true, page.hasMore)
            assertEquals("hook_1", page.lastId)
            assertEquals("whsec_test", client.rotateWebhookSecret("hook_1", WebhookSecretRotation(true), options).signingSecret)
            val test = client.testWebhookEndpoint("hook_1", WebhookTestRequest("response.completed"), options)
            assertTrue(test.success)
            assertEquals(503, test.statusCode)
            assertTrue(client.deleteWebhookEndpoint("hook_1", options))
            assertEquals(7, call)
        } finally { client.close() }
    }

    @Test
    fun missingEndpointReturnsNullAndDeleteReturnsFalse() = runTest {
        val client = client(MockEngine { respond("""{"error":{"message":"not found"}}""", status = HttpStatusCode.NotFound, headers = headersOf(HttpHeaders.ContentType, "application/json")) })
        try {
            assertNull(client.webhookEndpoint("missing"))
            assertFalse(client.deleteWebhookEndpoint("missing"))
        } finally { client.close() }
    }

    private suspend fun body(request: io.ktor.client.request.HttpRequestData) = Json.parseToJsonElement(request.body.toByteArray().decodeToString())
    private fun client(engine: MockEngine): OpenAI = OpenAI(OpenAIConfig(token = "test", engine = engine, logging = LoggingConfig(logLevel = LogLevel.None), retry = RetryStrategy(maxRetries = 0)))
    private val endpoint = """{"id":"hook_1","created_at":1770000000,"event_types":["response.completed"],"name":"receiver","url":"https://example.com/hook","signing_secret_hint":null,"object":"webhook_endpoint"}"""
    private val endpointWithSecret = endpoint.dropLast(1) + """, "signing_secret":"whsec_test"}"""
}
