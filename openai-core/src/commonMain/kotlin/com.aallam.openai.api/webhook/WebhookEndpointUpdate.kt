package com.aallam.openai.api.webhook

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Changes a project webhook endpoint. */
@Serializable
public data class WebhookEndpointUpdate(
    /** New subscriptions, or null to leave unchanged. */
    @SerialName("event_types") public val eventTypes: List<String>? = null,
    /** New endpoint name, or null to leave unchanged. */
    @SerialName("name") public val name: String? = null,
    /** New HTTPS receiver URL, or null to leave unchanged. */
    @SerialName("url") public val url: String? = null,
)
