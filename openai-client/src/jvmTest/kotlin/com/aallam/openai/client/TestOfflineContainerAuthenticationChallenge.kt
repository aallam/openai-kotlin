package com.aallam.openai.client

import com.aallam.openai.api.container.ContainerId
import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.logging.LogLevel
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlin.test.*

class TestOfflineContainerAuthenticationChallenge {
    @Test
    fun unauthorizedUploadIsNotReplayedWithAConsumedSource() = runTest {
        var requests = 0
        var closes = 0
        val buffer = Buffer().apply { write(byteArrayOf(1, 2, 3)) }
        val source = object : RawSource by buffer {
            override fun close() { closes++; buffer.close() }
        }
        val client = OpenAI(OpenAIConfig("test", engine = MockEngine { request ->
            requests++
            assertEquals("Bearer test", request.headers[HttpHeaders.Authorization])
            request.body.toByteArray()
            respond("""{"error":{"message":"Unauthorized","type":"invalid_api_key"}}""", HttpStatusCode.Unauthorized,
                headersOf(HttpHeaders.ContentType to listOf("application/json"), HttpHeaders.WWWAuthenticate to listOf("Bearer realm=\"OpenAI\"")))
        }, retry = RetryStrategy(maxRetries = 0), logging = LoggingConfig(LogLevel.None)))
        try {
            assertFailsWith<AuthenticationException> {
                client.uploadContainerFile(ContainerId("cntr_1"), FileSource("fixture.txt", source))
            }
            assertEquals(1, requests)
            assertEquals(1, closes)
        } finally { client.close() }
    }
}
