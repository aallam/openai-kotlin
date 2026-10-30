package com.aallam.openai.api.misc

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.ResponseMetadata
import com.aallam.openai.api.http.Timeout
import kotlin.test.*

class TestResponseMetadata {
    @Test
    fun headersAreCaseInsensitiveAndPreserveRepeatedValues() {
        val values = mutableListOf("req-1")
        val metadata = ResponseMetadata(200, mapOf(
            "X-Request-ID" to values,
            "X-Ratelimit-Limit-Requests" to listOf("100"),
            "x-ratelimit-remaining-tokens" to listOf("invalid"),
            "X-RateLimit-Reset-Tokens" to listOf("1m2s"),
            "X-Repeated" to listOf("one", "two"),
        ))
        values.add("later")
        assertEquals(listOf("req-1"), metadata.headerValues("x-request-id"))
        assertEquals("req-1", metadata.requestId)
        assertEquals(100L, metadata.rateLimitRequests)
        assertNull(metadata.rateLimitRemainingTokens)
        assertEquals("1m2s", metadata.rateLimitResetTokens)
        assertEquals(listOf("one", "two"), metadata.headerValues("X-REPEATED"))
        assertTrue(metadata.headerValues("missing").isEmpty())
    }

    @Test
    fun originalPositionalOptionsAndCopyStillCompile() {
        val timeout = Timeout()
        val old = RequestOptions(mapOf("header" to "value"), mapOf("query" to "value"), timeout)
        val headers: Map<String, String> = old.component1()
        val parameters: Map<String, String> = old.component2()
        val duration: Timeout? = old.component3()
        assertEquals(headers, old.copy().headers)
        assertEquals(parameters, old.urlParameters)
        assertEquals(timeout, duration)
        assertNull(old.onResponse)
    }
}
