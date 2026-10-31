package com.aallam.openai.api.webhook

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlin.test.*

class TestWebhookVerifier {
    // Independent fixture: Python stdlib hmac.new(key, id + '.' + timestamp + '.' + raw_body, hashlib.sha256).
    private val secret = "MDEyMzQ1Njc4OWFiY2RlZmdoaWprbG1ub3BxcnN0dXY="
    private val payload = """{"id":"evt_test","object":"event","type":"response.completed","created_at":1769999999,"data":{"id":"resp_1","extra":"é"},"future":{"v":1}}""" + "\n"
    private val signature = "a0AO/llx9xxyhrBpOUre7nSPQWLHW5LPsyukXj2+0xI="
    private val now = 1770000000L

    @Test
    fun knownSignatureVerifiesWithPrefixOrBareKeyAndPreservesUnknownFields() {
        for (key in listOf(secret, "whsec_$secret")) {
            val event = WebhookVerifier(key).unwrap(payload.encodeToByteArray(), headers(), now)
            assertEquals("evt_test", event.id)
            assertEquals("response.completed", event.type)
            assertEquals(1769999999L, event.createdAt)
            assertEquals("é", event.data["extra"]?.jsonPrimitive?.content)
            assertEquals(Json.parseToJsonElement(payload), Json.parseToJsonElement(Json.encodeToString(event)))
        }
    }

    @Test
    fun signatureBindsIdTimestampAndEveryRawByte() {
        val verifier = WebhookVerifier(secret)
        assertFailsWith<InvalidWebhookSignatureException> { verifier.verify(payload.trimEnd().encodeToByteArray(), headers(), now) }
        assertFailsWith<InvalidWebhookSignatureException> { verifier.verify(payload.encodeToByteArray(), headers() + ("webhook-id" to "wh_other"), now) }
        assertFailsWith<InvalidWebhookSignatureException> { verifier.verify(payload.encodeToByteArray(), headers() + ("webhook-timestamp" to (now + 1).toString()), now) }
    }

    @Test
    fun toleranceIncludesBoundaryButRejectsValidSignaturesOutsidePastAndFutureWindows() {
        val signatures = mapOf(
            -300L to "pHE599WElu2kz+gayINSRW+9uytdDmYQB8NnnTEOKkc=",
            300L to "8axlQxKLAtx1Da3Eo15AFUutMyoDMn2FpfTgWyAauKk=",
            -301L to "ZVtzWoRlJiYCg6b/9qbXMgkH6jjQ8jeQRiD6yo/25B8=",
            301L to "ClTwp4z6oIEjvczjolypeXMhLocLhJhi1d5JozuQJfc=",
        )
        val verifier = WebhookVerifier(secret)
        for ((offset, value) in signatures) {
            val headers = headers(value) + ("webhook-timestamp" to (now + offset).toString())
            if (offset == -300L || offset == 300L) verifier.verify(payload.encodeToByteArray(), headers, now)
            else assertFailsWith<InvalidWebhookSignatureException> { verifier.verify(payload.encodeToByteArray(), headers, now) }
        }
    }

    @Test
    fun rotationAcceptsAnyValidV1SignatureAndRejectsOtherVersions() {
        val verifier = WebhookVerifier(secret)
        verifier.verify(payload.encodeToByteArray(), headers() + ("webhook-signature" to "v1,garbage v2,$signature v1,$signature"), now)
        assertFailsWith<InvalidWebhookSignatureException> {
            verifier.verify(payload.encodeToByteArray(), headers() + ("webhook-signature" to "v2,$signature v1,AAAA"), now)
        }
    }

    @Test
    fun headerNamesAreCaseInsensitiveAndDuplicatesAreRejected() {
        val verifier = WebhookVerifier(secret)
        verifier.verify(payload.encodeToByteArray(), headers().mapKeys { it.key.uppercase() }, now)
        assertFailsWith<InvalidWebhookSignatureException> { verifier.verify(payload.encodeToByteArray(), headers() + ("Webhook-Id" to "wh_test"), now) }
        assertFailsWith<InvalidWebhookSignatureException> { verifier.verify(payload.encodeToByteArray(), headers() - "webhook-id", now) }
    }

    @Test
    fun malformedTimestampAndSignatureAreRejectedWithoutOverflow() {
        val verifier = WebhookVerifier(secret)
        for (value in listOf("-1", "NaN", "99999999999999999999999", Long.MAX_VALUE.toString())) {
            assertFailsWith<InvalidWebhookSignatureException> { verifier.verify(payload.encodeToByteArray(), headers() + ("webhook-timestamp" to value), now) }
        }
        assertFailsWith<InvalidWebhookSignatureException> { verifier.verify(payload.encodeToByteArray(), headers() + ("webhook-signature" to "v1,!@#"), now) }
        assertFailsWith<IllegalArgumentException> { WebhookVerifier("") }
        assertFailsWith<IllegalArgumentException> { WebhookVerifier(secret, -1) }
    }

    @Test
    fun correctlySignedInvalidJsonFailsAfterSignatureVerification() {
        val verifier = WebhookVerifier(secret)
        val headers = headers("54TYPFuDCkoBdfvbBkqSjeDRa9C7lPBxtl7IY1mkiL0=")
        verifier.verify("not JSON".encodeToByteArray(), headers, now)
        assertFailsWith<SerializationException> { verifier.unwrap("not JSON".encodeToByteArray(), headers, now) }
    }

    @Test
    fun unknownEventTypesRemainReadableButInvalidEventShapesFail() {
        val raw = Json.parseToJsonElement(payload.replace("response.completed", "future.event")).jsonObject
        assertEquals("future.event", WebhookEvent(raw).type)
        assertFailsWith<IllegalArgumentException> { WebhookEvent(JsonObject(raw - "data")) }
        assertFailsWith<IllegalArgumentException> { WebhookEvent(JsonObject(raw + ("created_at" to JsonPrimitive("123")))) }
    }

    private fun headers(value: String = signature) = mapOf(
        "webhook-id" to "wh_test", "webhook-timestamp" to now.toString(), "webhook-signature" to "v1,$value",
    )
}
