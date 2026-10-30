package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.model.ModelId
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TestOfflineClient {
    @Test
    fun configuredClientSendsOptionsAndDecodesResponse() = runTest {
        val engine = MockEngine { request ->
            assertEquals("/v1/models/test-model", request.url.encodedPath)
            assertEquals("example", request.headers["X-Client-Test"])
            assertEquals("true", request.url.parameters["details"])
            respond(
                """{"id":"test-model","created":0,"owned_by":"openai"}""",
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        OpenAI(OpenAIConfig(token = "offline-test", engine = engine)).use { client ->
            val result = client.model(
                ModelId("test-model"),
                RequestOptions(
                    headers = mapOf("X-Client-Test" to "example"),
                    urlParameters = mapOf("details" to "true"),
                ),
            )
            assertEquals("test-model", result?.id?.id)
        }
    }
}
