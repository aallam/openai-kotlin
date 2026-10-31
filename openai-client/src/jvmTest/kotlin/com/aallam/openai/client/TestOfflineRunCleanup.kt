package com.aallam.openai.client

import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.run.AssistantStreamEvent
import com.aallam.openai.api.run.AssistantStreamEventType
import com.aallam.openai.client.internal.withTestCleanup
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TestOfflineRunCleanup {
    @Test
    fun earlyStreamFailureCancelsRunBeforeDeletingThread() = runTest {
        val requests = mutableListOf<String>()
        val engine = MockEngine { request ->
            val path = request.url.encodedPath
            requests += path
            respond(
                if (path.endsWith("/cancel")) runJson("cancelled") else """{"id":"thread-1","object":"thread.deleted","deleted":true}""",
                headers = jsonHeaders,
            )
        }
        OpenAI(OpenAIConfig("test", engine = engine, retry = RetryStrategy(maxRetries = 0))).use { client ->
            val failure = AssertionError("stream stopped before completion")
            assertSame(failure, assertFailsWith<AssertionError> {
                withTestCleanup {
                    val cleanup = RunCleanup(this, client)
                    cleanup.event(AssistantStreamEvent("thread.created", AssistantStreamEventType.THREAD_CREATED,
                        """{"id":"thread-1","created_at":0,"metadata":{}}"""))
                    cleanup.event(AssistantStreamEvent("thread.run.created", AssistantStreamEventType.THREAD_RUN_CREATED,
                        runJson("in_progress")))
                    throw failure
                }
            })
            assertTrue(failure.suppressedExceptions.isEmpty(), "Cleanup should succeed")
        }
        assertEquals(listOf("/v1/threads/thread-1/runs/run-1/cancel", "/v1/threads/thread-1"), requests)
    }

    @Test
    fun cancellationRaceAcceptsOnlyAConfirmedTerminalRun() = runTest {
        for (status in listOf("completed", "in_progress")) {
            val requests = mutableListOf<String>()
            val engine = MockEngine { request ->
                val path = request.url.encodedPath
                requests += "${request.method.value} $path"
                when {
                    path.endsWith("/cancel") -> respond(
                        """{"error":{"message":"Run cannot be cancelled","type":"invalid_request_error","code":null}}""",
                        HttpStatusCode.BadRequest, jsonHeaders)
                    path.endsWith("/run-1") -> respond(runJson(status), headers = jsonHeaders)
                    else -> respond("""{"id":"thread-1","object":"thread.deleted","deleted":true}""", headers = jsonHeaders)
                }
            }
            OpenAI(OpenAIConfig("test", engine = engine, retry = RetryStrategy(maxRetries = 0))).use { client ->
                suspend fun exerciseCleanup() = withTestCleanup {
                    RunCleanup(this, client).event(AssistantStreamEvent("thread.run.created",
                        AssistantStreamEventType.THREAD_RUN_CREATED, runJson("in_progress")))
                }
                if (status == "completed") exerciseCleanup()
                else assertFailsWith<InvalidRequestException> { exerciseCleanup() }
            }
            assertEquals(listOf("POST /v1/threads/thread-1/runs/run-1/cancel",
                "GET /v1/threads/thread-1/runs/run-1", "DELETE /v1/threads/thread-1"), requests)
        }
    }

    private fun runJson(status: String): String =
        """{"id":"run-1","created_at":0,"thread_id":"thread-1","assistant_id":"assistant-1","status":"$status","model":"test"}"""

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
}
