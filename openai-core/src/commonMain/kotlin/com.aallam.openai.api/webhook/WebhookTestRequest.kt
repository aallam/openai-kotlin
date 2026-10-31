package com.aallam.openai.api.webhook

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Requests a sample delivery to a webhook endpoint. */
@Serializable
public data class WebhookTestRequest(
    /** Type of sample event to deliver. */
    @SerialName("event_type") public val eventType: String,
)
