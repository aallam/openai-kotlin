package com.aallam.openai.client

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.exception.*
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class TestOfflineErrorResponses {
    @Test
    fun nonJsonErrorResponsesKeepTheirHttpStatusAndExceptionType() = runTest {
        val responses = listOf(
            Triple(HttpStatusCode.NotFound, "", null),
            Triple(HttpStatusCode.NotFound, "This endpoint no longer exists", "text/plain"),
            Triple(HttpStatusCode.Forbidden, "<html>Forbidden</html>", "text/html"),
            Triple(HttpStatusCode.BadRequest, "{invalid-json", "application/json"),
            Triple(HttpStatusCode.BadRequest, "{}", "application/json"),
            Triple(HttpStatusCode.TooManyRequests, "Slow down", "text/plain"),
            Triple(HttpStatusCode(418, "I'm a teapot"), "", null),
        )
        for ((status, body, contentType) in responses) {
            val error = requestError(status, body, contentType)
            assertEquals(status.value, error.statusCode)
            assertIs<ClientRequestException>(error.cause)
            assertTrue(error.message.orEmpty().contains(status.value.toString()))
            when (status) {
                HttpStatusCode.NotFound, HttpStatusCode.BadRequest -> assertIs<InvalidRequestException>(error)
                HttpStatusCode.Forbidden -> assertIs<PermissionException>(error)
                HttpStatusCode.TooManyRequests -> assertIs<RateLimitException>(error)
                else -> assertIs<UnknownAPIException>(error)
            }
        }
    }

    @Test
    fun structuredErrorsDecodeEvenWithoutAContentType() = runTest {
        for (contentType in listOf(null, "application/json")) {
            val error = requestError(
                HttpStatusCode.Unauthorized,
                """{"error":{"message":"Invalid credential","type":"invalid_request_error","code":"invalid_api_key","param":"token"}}""",
                contentType,
            )
            assertIs<AuthenticationException>(error)
            assertEquals("Invalid credential", error.message)
            assertEquals("invalid_request_error", error.error.detail?.type)
            assertEquals("invalid_api_key", error.error.detail?.code)
            assertEquals("token", error.error.detail?.param)
        }
    }

    @Test
    fun cancellationAndRequestTimeoutKeepTheirOriginalExceptionTypes() = runTest {
        for (cancelled in listOf(true, false)) {
            val engine = MockEngine { request ->
                if (cancelled) throw CancellationException("cancelled request")
                else throw HttpRequestTimeoutException(request)
            }
            OpenAI(OpenAIConfig("test", engine = engine, retry = RetryStrategy(maxRetries = 0), logging = LoggingConfig(LogLevel.None))).use { client ->
                if (cancelled) {
                    val error = assertFailsWith<CancellationException> {
                        client.chatCompletion(ChatCompletionRequest(ModelId("test"), emptyList()))
                    }
                    assertEquals("cancelled request", error.message)
                } else {
                    val error = assertFailsWith<OpenAITimeoutException> {
                        client.chatCompletion(ChatCompletionRequest(ModelId("test"), emptyList()))
                    }
                    assertIs<HttpRequestTimeoutException>(error.cause)
                }
            }
        }
    }

    private suspend fun requestError(status: HttpStatusCode, body: String, contentType: String?): OpenAIAPIException {
        val engine = MockEngine {
            respond(body, status, contentType?.let { headersOf(HttpHeaders.ContentType, it) } ?: Headers.Empty)
        }
        OpenAI(OpenAIConfig("test", engine = engine, retry = RetryStrategy(maxRetries = 0), logging = LoggingConfig(LogLevel.None))).use { client ->
            return assertFailsWith<OpenAIAPIException> {
                client.chatCompletion(ChatCompletionRequest(ModelId("test"), emptyList()))
            }
        }
    }
}
