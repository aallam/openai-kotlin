@file:OptIn(kotlin.io.encoding.ExperimentalEncodingApi::class)

package com.aallam.openai.api.webhook

import kotlinx.serialization.json.Json
import org.kotlincrypto.macs.hmac.sha2.HmacSHA256
import kotlin.io.encoding.Base64

/** An incoming webhook lacks a valid signature or has an unacceptable delivery timestamp. */
public class InvalidWebhookSignatureException(message: String) : IllegalArgumentException(message)

/**
 * Verifies OpenAI's Standard Webhooks HMAC-SHA256 signatures over the unmodified body bytes.
 * Supply a trusted current Unix time on every call. Timestamp tolerance limits replay age;
 * applications must additionally deduplicate event IDs when processing deliveries.
 *
 * @param secret base64 signing key, with or without the whsec_ prefix.
 * @param toleranceSeconds maximum delivery timestamp age or future clock skew, inclusive.
 */
public class WebhookVerifier(secret: String, private val toleranceSeconds: Long = 300) {
    private val key: ByteArray = Base64.decode(secret.removePrefix("whsec_"))

    init {
        require(key.isNotEmpty()) { "Webhook signing key must not be empty" }
        require(toleranceSeconds >= 0) { "Webhook tolerance must not be negative" }
    }

    /**
     * Verifies headers and the original bytes before parsing JSON.
     * Header names are case-insensitive. Supported signature versions are v1 (HMAC-SHA256).
     * @throws InvalidWebhookSignatureException for missing/ambiguous headers, stale/future timestamps or invalid signatures.
     */
    public fun verify(payload: ByteArray, headers: Map<String, String>, nowSeconds: Long) {
        require(nowSeconds >= 0) { "Current Unix time must not be negative" }
        val id = header(headers, "webhook-id")
        val timestamp = header(headers, "webhook-timestamp")
        val signatures = header(headers, "webhook-signature")
        val time = timestamp.toLongOrNull()
            ?.takeIf { it >= 0 && timestamp.all(Char::isDigit) }
            ?: invalid("Invalid webhook timestamp")
        val distance = if (time > nowSeconds) time - nowSeconds else nowSeconds - time
        if (distance > toleranceSeconds) invalid("Webhook timestamp is outside the allowed tolerance")
        val prefix = "$id.$timestamp.".encodeToByteArray()
        val expected = HmacSHA256(key).doFinal(prefix + payload)
        val matches = signatures.split(Regex("\\s+")).any { token ->
            val parts = token.split(',', limit = 2)
            if (parts.size != 2 || parts[0] != "v1") return@any false
            val candidate = try { Base64.decode(parts[1]) } catch (_: IllegalArgumentException) { return@any false }
            constantTimeEquals(expected, candidate)
        }
        if (!matches) invalid("No valid webhook signature found")
    }

    /**
     * Verifies the unmodified body, then parses its UTF-8 JSON event while retaining unknown fields.
     * Signature errors are distinct from invalid UTF-8, JSON or event-shape errors.
     */
    public fun unwrap(payload: ByteArray, headers: Map<String, String>, nowSeconds: Long): WebhookEvent {
        verify(payload, headers, nowSeconds)
        return Json.decodeFromString(WebhookEvent.serializer(), payload.decodeToString(throwOnInvalidSequence = true))
    }

    private fun header(headers: Map<String, String>, name: String): String {
        val values = headers.entries.filter { it.key.equals(name, ignoreCase = true) }
        if (values.size != 1 || values.single().value.isBlank()) invalid("Missing or ambiguous $name header")
        return values.single().value
    }

    private fun constantTimeEquals(expected: ByteArray, actual: ByteArray): Boolean {
        if (expected.size != actual.size) return false
        var difference = 0
        for (index in expected.indices) difference = difference or (expected[index].toInt() xor actual[index].toInt())
        return difference == 0
    }

    private fun invalid(message: String): Nothing = throw InvalidWebhookSignatureException(message)
}
