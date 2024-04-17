package com.aallam.openai.api.vectorstore

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The expiration policy for a vector store.
 */
@Serializable
public data class ExpirationPolicy(
    /**
     * Anchor timestamp after which the expiration policy applies. Supported anchors: last_active_at.
     */
    @SerialName("anchor") public val anchor: String,

    /**
     * The number of days after the anchor time that the vector store will expire.
     */
    @SerialName("days") public val days: Long,
)
