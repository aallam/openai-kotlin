package com.aallam.openai.api.webhook

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Webhook endpoint configuration and optional creation/rotation signing secret. */
@Serializable
public data class WebhookEndpoint(
    /** Endpoint identifier. */
    @SerialName("id") public val id: String,
    /** Creation time in Unix seconds. */
    @SerialName("created_at") public val createdAt: Long,
    /** Subscribed event types. */
    @SerialName("event_types") public val eventTypes: List<String>,
    /** Human-readable endpoint name. */
    @SerialName("name") public val name: String,
    /** HTTPS receiver URL. */
    @SerialName("url") public val url: String,
    /** Masked signing key hint. */
    @SerialName("signing_secret_hint") public val signingSecretHint: String? = null,
    /** Last configuration or signing-key update time. */
    @SerialName("updated_at") public val updatedAt: Long? = null,
    /** Signing key, returned only on creation or rotation. Treat as a secret. */
    @SerialName("signing_secret") public val signingSecret: String? = null,
    /** API object discriminator. */
    @SerialName("object") public val objectType: String = "webhook_endpoint",
) {
    /** Omits signing material from diagnostic strings. */
    override fun toString(): String = "WebhookEndpoint(id=$id, name=$name, url=$url)"
}
