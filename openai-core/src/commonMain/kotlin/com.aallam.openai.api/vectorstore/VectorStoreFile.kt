package com.aallam.openai.api.vectorstore

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.core.LastError
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.file.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A list of files attached to a vector store.
 */
@BetaOpenAI
@Serializable
public data class VectorStoreFile(
    /**
     * The identifier, which can be referenced in API endpoints.
     */
    @SerialName("id") public val id: FileId,

    /**
     * Usage bytes.
     */
    @SerialName("usage_bytes") public val usageBytes: Long? = null,

    /**
     * The Unix timestamp (in seconds) for when the vector store file was created.
     */
    @SerialName("created_at") public val createdAt: Long,

    /**
     * The ID of the vector store that the File is attached to.
     */
    @SerialName("vector_store_id") public val vectorStoreId: VectorStoreId,

    /**
     * The status of the vector store file, which can be either in_progress, completed, cancelled, or failed.
     * The status completed indicates that the vector store file is ready for use.
     */
    @SerialName("status") public val status: Status,

    /**
     * The last error associated with this vector store file. Will be `null` if there are no errors.
     */
    @SerialName("last_error") public val lastError: LastError? = null,
)
