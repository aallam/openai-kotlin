package com.aallam.openai.api.webhook

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Creates a project webhook endpoint. */
@Serializable
public data class WebhookEndpointRequest(
    /** Subscribed event types. */
    @SerialName("event_types") public val eventTypes: List<String>,
    /** Human-readable endpoint name. */
    @SerialName("name") public val name: String,
    /** HTTPS receiver URL. */
    @SerialName("url") public val url: String,
)
