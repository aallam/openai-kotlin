package com.aallam.openai.client

import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TestOfflineAuthentication {
    @Test
    fun unauthorizedResponseDoesNotDiscardConfiguredApiKey() = runTest {
        val authorization = mutableListOf<String?>()
        var healthy = false
        val engine = MockEngine { request ->
            authorization += request.headers[HttpHeaders.Authorization]
            if (healthy) {
                respond("""{"id":"test","created":0,"owned_by":"openai"}""", headers = headersOf(HttpHeaders.ContentType, "application/json"))
            } else {
                respond("""{"error":{"message":"temporary failure","type":"invalid_request_error"}}""", HttpStatusCode.Unauthorized, headersOf(HttpHeaders.ContentType, "application/json"))
            }
        }
        OpenAI(OpenAIConfig("configured-key", engine = engine, retry = RetryStrategy(maxRetries = 0), logging = LoggingConfig(LogLevel.None))).use { client ->
            assertFailsWith<AuthenticationException> { client.model(ModelId("test")) }
            healthy = true
            assertEquals("test", client.model(ModelId("test"))!!.id.id)
            authorization.forEach { assertEquals("Bearer configured-key", it) }
        }
    }
}
