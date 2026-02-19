package com.aallam.openai.client.misc

import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.internal.createHttpClient
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Tests for HttpClient authentication behavior, specifically token handling
 * when receiving 401 responses.
 */
class TestHttpClientAuth {

    /**
     * Verifies that the authentication token is NOT cleared when a 401 response
     * is received from the API. This test ensures that the refreshTokens callback
     * returns the old tokens instead of null (which would clear the token).
     *
     * To verify the test is working correctly:
     * 1. Run the test - it should pass
     * 2. Comment out the refreshTokens block in HttpClient.kt
     * 3. Run the test again - it should fail
     */
    @Test
    fun testTokenNotClearedOn401() = runTest {
        val testToken = "test-token-12345"
        var requestCount = 0
        val capturedAuthHeaders = mutableListOf<String?>()

        // Create a mock engine that simulates:
        // 1. First request: Returns 401 (triggers token refresh)
        // 2. Second request: Should still have the token (not cleared)
        val mockEngine = MockEngine { request ->
            requestCount++
            val authHeader = request.headers[HttpHeaders.Authorization]
            capturedAuthHeaders.add(authHeader)

            when (requestCount) {
                1 -> {
                    // First request: return 401 to trigger refresh
                    respond(
                        content = """{"error": {"message": "Invalid token", "type": "invalid_request_error"}}""",
                        status = HttpStatusCode.Unauthorized,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                2 -> {
                    // Second request: should succeed with same token
                    respond(
                        content = """{"data": []}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                else -> error("Unexpected request count: $requestCount")
            }
        }

        val config = OpenAIConfig(token = testToken, engine = mockEngine)
        val httpClient = createHttpClient(config)

        try {
            // Make a request that will trigger 401 and then retry
            httpClient.get("/test")

            // Verify we made 2 requests (initial + retry after 401)
            assertEquals(2, requestCount, "Should have made 2 requests (initial + retry)")

            // Verify first request had the token
            assertNotNull(capturedAuthHeaders[0], "First request should have Authorization header")
            assertEquals("Bearer $testToken", capturedAuthHeaders[0])

            // Verify second request STILL has the token (not cleared)
            assertNotNull(capturedAuthHeaders[1], "Second request should still have Authorization header")
            assertEquals("Bearer $testToken", capturedAuthHeaders[1],
                "Token should NOT be cleared after 401 response")
        } finally {
            httpClient.close()
        }
    }
}