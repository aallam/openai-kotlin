package com.aallam.openai.api.thread

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a thread that contains messages.
 */
@BetaOpenAI
@Serializable
public data class Thread(
    /**
     * The identifier of the thread, which can be referenced in API endpoints.
     */
    @SerialName("id") val id: ThreadId,
    /**
     * The Unix timestamp (in seconds) for when the thread was created.
     */
    @SerialName("created_at") val createdAt: Int,

    /**
     * A map representing a set of key-value pairs that can be attached to the thread.
     * This can be useful for storing additional information about the thread in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    @SerialName("metadata") val metadata: Map<String, String>
)
