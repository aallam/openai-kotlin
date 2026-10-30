package com.aallam.openai.api.webhook

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Result of sending a sample webhook delivery. */
@Serializable
public data class WebhookTestResult(
    /** Delivered sample event type. */
    @SerialName("event_type") public val eventType: String,
    /** HTTP status returned by the receiving endpoint. */
    @SerialName("status_code") public val statusCode: Int,
    /** Endpoint that received the sample. */
    @SerialName("webhook_endpoint_id") public val webhookEndpointId: String,
    /** Whether the test request completed; inspect statusCode for receiver success. */
    @SerialName("success") public val success: Boolean = true,
    /** API object discriminator. */
    @SerialName("object") public val objectType: String = "webhook_endpoint.test",
)
