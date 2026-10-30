package com.aallam.openai.client

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.ResponseMetadata
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.exception.OpenAIHttpException
import com.aallam.openai.api.model.ModelId
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class TestOfflineResponseMetadata {
    @Test
    fun successHeadersArriveBeforeBodyDecodingAndDoNotConsumeBody() = runTest {
        var metadata: ResponseMetadata? = null
        val engine = MockEngine {
            respond("""{"id":"test","created":0,"owned_by":"openai"}""", headers = headersOf(
                HttpHeaders.ContentType to listOf("application/json"),
                "X-Request-Id" to listOf("req-success"),
                "x-ratelimit-remaining-requests" to listOf("9"),
            ))
        }
        OpenAI(OpenAIConfig(token = "test", engine = engine)).use { client ->
            val model = client.model(ModelId("test"), RequestOptions(onResponse = { metadata = it }))
            assertEquals("test", model.id.id)
            assertEquals("req-success", metadata?.requestId)
            assertEquals(9L, metadata?.rateLimitRemainingRequests)
            assertEquals(200, metadata?.statusCode)
        }
    }

    @Test
    fun errorHeadersAreAvailableBeforeTheOperationThrows() = runTest {
        val seen = mutableListOf<Int>()
        val engine = MockEngine {
            respond("""{"error":{"message":"bad input","type":"invalid_request_error","code":null,"param":null}}""", HttpStatusCode.BadRequest,
                headersOf(HttpHeaders.ContentType to listOf("application/json"), "x-request-id" to listOf("req-error")))
        }
        OpenAI(OpenAIConfig(token = "test", engine = engine)).use { client ->
            assertFailsWith<InvalidRequestException> {
                client.model(ModelId("test"), RequestOptions(onResponse = {
                    assertEquals("req-error", it.requestId)
                    seen.add(it.statusCode)
                }))
            }
            assertEquals(listOf(400), seen)
        }
    }

    @Test
    fun streamingHeadersArriveBeforeTheFirstChunkAndCancellationStillWorks() = runTest {
        val order = mutableListOf<String>()
        val engine = MockEngine {
            respond("data: {\"id\":\"chunk-1\",\"created\":0,\"model\":\"test\",\"choices\":[]}\n\ndata: [DONE]\n\n",
                headers = headersOf(HttpHeaders.ContentType to listOf("text/event-stream"), "X-Request-Id" to listOf("req-stream")))
        }
        OpenAI(OpenAIConfig(token = "test", engine = engine)).use { client ->
            val chunks = client.chatCompletions(
                ChatCompletionRequest(ModelId("test"), listOf(ChatMessage(ChatRole.User, "hello"))),
                RequestOptions(onResponse = {
                    assertEquals("req-stream", it.requestId)
                    order.add("headers")
                }),
            ).take(1).toList()
            order.add("chunk")
            assertEquals("chunk-1", chunks.single().id)
            assertEquals(listOf("headers", "chunk"), order)
        }
    }

    @Test
    fun observerCancellationIsNotConvertedToAnHttpFailure() = runTest {
        val engine = MockEngine {
            respond("""{"id":"test","created":0,"owned_by":"openai"}""", headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        OpenAI(OpenAIConfig(token = "test", engine = engine)).use { client ->
            val error = assertFailsWith<CancellationException> {
                client.model(ModelId("test"), RequestOptions(onResponse = { throw CancellationException("stop") }))
            }
            assertEquals("stop", error.message)
        }
    }
    @Test
    fun headersRemainAvailableWhenBodyDecodingFails() = runTest {
        var metadata: ResponseMetadata? = null
        val engine = MockEngine {
            respond("not json", headers = headersOf(HttpHeaders.ContentType to listOf("application/json"), "x-request-id" to listOf("req-invalid-body")))
        }
        OpenAI(OpenAIConfig(token = "test", engine = engine)).use { client ->
            assertFailsWith<OpenAIHttpException> {
                client.model(ModelId("test"), RequestOptions(onResponse = { metadata = it }))
            }
            assertEquals("req-invalid-body", metadata?.requestId)
        }
    }

    @Test
    fun cancellationBeforeAResponseDoesNotInvokeObserver() = runTest {
        var observed = false
        val engine = MockEngine { throw CancellationException("before headers") }
        OpenAI(OpenAIConfig(token = "test", engine = engine)).use { client ->
            assertFailsWith<CancellationException> {
                client.model(ModelId("test"), RequestOptions(onResponse = { observed = true }))
            }
            assertFalse(observed)
        }
    }

    @Test
    fun observerFailureDoesNotRetryASuccessfulPost() = runTest {
        var requests = 0
        val failure = IllegalStateException("observer failed")
        val engine = MockEngine { request ->
            requests++
            assertEquals(HttpMethod.Post, request.method)
            respond("""{"id":"chat-1","created":0,"model":"test","choices":[]}""",
                headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val config = OpenAIConfig(token = "test", engine = engine,
            retry = RetryStrategy(maxRetries = 2, maxDelay = 1.milliseconds))
        OpenAI(config).use { client ->
            val error = assertFailsWith<OpenAIHttpException> {
                client.chatCompletion(
                    ChatCompletionRequest(ModelId("test"), listOf(ChatMessage(ChatRole.User, "hello"))),
                    RequestOptions(onResponse = { throw failure }),
                )
            }
            assertSame(failure, error.cause)
            assertEquals(1, requests)
        }
    }

    @Test
    fun transientTransportFailuresStillUseTheExistingRetryPolicy() = runTest {
        var requests = 0
        var observations = 0
        val engine = MockEngine {
            if (++requests == 1) throw java.io.IOException("temporary transport failure")
            respond("""{"id":"test","created":0,"owned_by":"openai"}""",
                headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val config = OpenAIConfig(token = "test", engine = engine,
            retry = RetryStrategy(maxRetries = 1, maxDelay = 1.milliseconds))
        OpenAI(config).use { client ->
            val model = client.model(ModelId("test"), RequestOptions(onResponse = { observations++ }))
            assertEquals("test", model.id.id)
            assertEquals(2, requests)
            assertEquals(1, observations)
        }
    }

}
