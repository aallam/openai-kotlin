package com.aallam.openai.api.webhook

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Controls overlap while rotating a webhook signing key. */
@Serializable
public data class WebhookSecretRotation(
    /** Keep the previous signing key valid for 24 hours when true. */
    @SerialName("keep_old_secret_active_for_24_hours") public val keepOldSecretActiveFor24Hours: Boolean? = null,
)
