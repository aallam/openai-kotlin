package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.logging.LogLevel
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TestOfflineRequestOptions {
    @Test
    fun test() = runTest {
        val engine = MockEngine { request ->
            assertEquals("/v1/models", request.url.encodedPath)
            assertEquals("valueB", request.headers["key"])
            respond("""{"object":"list","data":[]}""", headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        OpenAI(OpenAIConfig("offline-test", engine = engine, headers = mapOf("key" to "valueA"),
            logging = LoggingConfig(LogLevel.None))).use { client ->
            client.models(RequestOptions(headers = mapOf("key" to "valueB")))
        }
    }

    @Test
    fun testOverride() = runTest {
        val engine = MockEngine { request ->
            assertEquals("/v1/assistants", request.url.encodedPath)
            assertEquals("assistants=v0", request.headers["OpenAI-Beta"])
            respond("""{"object":"list","data":[]}""", headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        OpenAI(OpenAIConfig("offline-test", engine = engine, headers = mapOf("OpenAI-Beta" to "assistants=v3"),
            logging = LoggingConfig(LogLevel.None))).use { client ->
            client.assistants(requestOptions = RequestOptions(headers = mapOf("OpenAI-Beta" to "assistants=v0")))
        }
    }
}
