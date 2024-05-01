package com.aallam.openai.api.vectorstore

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.batch.BatchId
import com.aallam.openai.api.core.Status
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A batch of files attached to a vector store.
 */
@BetaOpenAI
@Serializable
public data class FilesBatch(

    /**
     * The identifier, which can be referenced in API endpoints.
     */
    @SerialName("id") public val id: BatchId,

    /**
     * The Unix timestamp (in seconds) for when the vector store files batch was created.
     */
    @SerialName("created_at") public val createdAt: Long,

    /**
     * The ID of the vector store that the File is attached to.
     */
    @SerialName("vector_store_id") public val vectorStoreId: VectorStoreId,

    /**
     * The status of the vector store files batch, which can be either [Status.InProgress], [Status.Completed],
     * [Status.Cancelled] or [Status.Failed].
     */
    @SerialName("status") public val status: Status,

    /**
     * Files that are currently being processed.
     */
    @SerialName("file_counts") public val fileCounts: FileCounts,
)
