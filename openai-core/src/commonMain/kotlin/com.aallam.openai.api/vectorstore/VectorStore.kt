package com.aallam.openai.api.vectorstore

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.core.Status
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The vector store object.
 */
@BetaOpenAI
@Serializable
public data class VectorStore(
    /**
     * The identifier, which can be referenced in API endpoints.
     */
    @SerialName("id") public val id: VectorStoreId,

    /**
     * The Unix timestamp (in seconds) for when the vector store was created.
     */
    @SerialName("created_at") public val createdAt: Long,

    /**
     * The name of the vector store.
     */
    @SerialName("name") public val name: String? = null,

    /**
     * The byte size of the vector store that is currently in use.
     */
    @SerialName("usage_bytes") public val usageBytes: Long,

    /**
     * Files that are currently being processed.
     */
    @SerialName("file_counts") public val fileCounts: FileCounts,

    /**
     * The status of the vector store, which can be either expired, in_progress, or completed. A status of completed indicates that the vector store is ready for use.
     */
    @SerialName("status") public val status: Status,

    /**
     * The expiration policy for a vector store.
     */
    @SerialName("expires_after") public val expiresAfter: ExpirationPolicy? = null,

    /**
     * The Unix timestamp (in seconds) for when the vector store will expire.
     */
    @SerialName("expires_at") public val expiresAt: Long? = null,

    /**
     * The Unix timestamp (in seconds) for when the vector store was last active.
     */
    @SerialName("last_active_at") public val lastActiveAt: Long? = null,

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
)
