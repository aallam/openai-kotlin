# Webhooks

Create project webhook endpoints and verify incoming deliveries using the
[OpenAI webhook contract](https://developers.openai.com/api/docs/guides/webhooks)
and [Standard Webhooks specification](https://github.com/standard-webhooks/standard-webhooks/blob/main/spec/standard-webhooks.md).

## Endpoint management

```kotlin
val endpoint = openAI.webhookEndpoint(
    WebhookEndpointRequest(
        eventTypes = listOf("response.completed"),
        name = "Background responses",
        url = "https://example.com/openai-webhook",
    )
)
// Store endpoint.signingSecret securely. Creation and rotation return it only once.
val page = openAI.webhookEndpoints(limit = 20)
val nextPage = openAI.webhookEndpoints(after = page.lastId, limit = 20)
openAI.updateWebhookEndpoint(endpoint.id, WebhookEndpointUpdate(name = "Responses"))
val test = openAI.testWebhookEndpoint(endpoint.id, WebhookTestRequest("response.completed"))
// test.success means the test request completed; check test.statusCode for receiver success.
val rotated = openAI.rotateWebhookSecret(endpoint.id, WebhookSecretRotation(
    keepOldSecretActiveFor24Hours = true,
))
// Store rotated.signingSecret securely before removing the previous verifier key.
openAI.deleteWebhookEndpoint(endpoint.id)
```

The endpoint API manages the authenticated project's `/webhook_endpoints` resource.
It does not register an HTTP receiver in your application. List results retain
cursor metadata; retrieval returns null and deletion returns false for missing
endpoints. Event type strings are open-ended to accommodate new API events.

## Verify a delivery

```kotlin
val verifier = WebhookVerifier(secret = signingSecret)
val event = verifier.unwrap(
    payload = rawRequestBytes,
    headers = requestHeaders,
    nowSeconds = trustedCurrentUnixSeconds,
)
when (event.type) {
    "response.completed" -> {
        // Deduplicate event.id, then retrieve the response identified in event.data.
    }
}
```

Keep the original body bytes. JSON parsing, whitespace changes or re-encoding
before verification invalidate the signed message. The signed delivery timestamp
is checked against the caller-supplied current Unix time, with an inclusive
five-minute tolerance in both directions by default. Use a trusted clock and keep
it synchronized; the event's `createdAt` is not the delivery timestamp.

`verify()` checks the signature without parsing the body. `unwrap()` verifies
first, then validates UTF-8 and parses a `WebhookEvent`. Invalid signatures and
out-of-window timestamps throw `InvalidWebhookSignatureException`; correctly
signed malformed JSON or event shapes fail during parsing instead. Header names
are case-insensitive, ambiguous duplicate header names are rejected, and any
valid `v1` signature in a rotation header is accepted. Signing keys accept the
`whsec_` prefix or bare base64. HMAC-SHA256 uses KotlinCrypto; asymmetric `v1a`
signatures are not supported by this verifier.

The event preserves its complete JSON `payload`, including unknown top-level
and event-specific `data` fields. Unknown event types remain readable. Implement
application-level event-ID deduplication: timestamp validation alone does not
prevent processing the same valid delivery more than once during its tolerance
window. Acknowledge deliveries promptly and handle longer processing separately.
