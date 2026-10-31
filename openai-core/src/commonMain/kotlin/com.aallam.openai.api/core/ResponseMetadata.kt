package com.aallam.openai.api.core

/** HTTP status and response headers, available before the response body is decoded or streamed. */
public class ResponseMetadata(
    /** HTTP response status code. */
    public val statusCode: Int,
    headers: Map<String, List<String>>,
) {
    /** Snapshot of all response headers with their original names and values. */
    public val headers: Map<String, List<String>> = headers.mapValues { it.value.toList() }

    /** Values for [name], matched without regard to case. */
    public fun headerValues(name: String): List<String> =
        headers.entries.filter { it.key.equals(name, ignoreCase = true) }.flatMap { it.value }

    /** First value for [name], or null when absent. */
    public fun header(name: String): String? = headerValues(name).firstOrNull()

    /** Server request identifier, when supplied. */
    public val requestId: String? get() = header("x-request-id")

    /** Request limit for the current rate-limit window, when supplied as an integer. */
    public val rateLimitRequests: Long? get() = header("x-ratelimit-limit-requests")?.toLongOrNull()

    /** Token limit for the current rate-limit window, when supplied as an integer. */
    public val rateLimitTokens: Long? get() = header("x-ratelimit-limit-tokens")?.toLongOrNull()

    /** Requests remaining in the current rate-limit window. */
    public val rateLimitRemainingRequests: Long? get() = header("x-ratelimit-remaining-requests")?.toLongOrNull()

    /** Tokens remaining in the current rate-limit window. */
    public val rateLimitRemainingTokens: Long? get() = header("x-ratelimit-remaining-tokens")?.toLongOrNull()

    /** Server-supplied duration until the request limit resets. */
    public val rateLimitResetRequests: String? get() = header("x-ratelimit-reset-requests")

    /** Server-supplied duration until the token limit resets. */
    public val rateLimitResetTokens: String? get() = header("x-ratelimit-reset-tokens")
}
