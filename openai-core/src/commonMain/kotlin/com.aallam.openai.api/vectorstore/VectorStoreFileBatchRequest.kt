package com.aallam.openai.api.vectorstore

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.file.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

/**
 * Creates a batch using shared configuration or a separate configuration for each attachment.
 *
 * @throws IllegalArgumentException if modes conflict, settings would be ignored, a per-file ID is
 * absent, or the batch contains fewer than 1 or more than 2000 files.
 */
@BetaOpenAI
@Serializable
public data class VectorStoreFileBatchRequest(
    /** File IDs receiving shared attributes and chunking. Mutually exclusive with [files]. */
    @SerialName("file_ids") public val fileIds: List<FileId>? = null,
    /** Per-file configuration. Shared attributes/chunking cannot be used in this mode. */
    @SerialName("files") public val files: List<VectorStoreFileCreateRequest>? = null,
    /** Attributes applied to every file in [fileIds]. */
    @SerialName("attributes") public val attributes: Map<String, JsonPrimitive>? = null,
    /** Chunking policy applied to every file in [fileIds]. */
    @SerialName("chunking_strategy") public val chunkingStrategy: ChunkingStrategy? = null,
) {
    init {
        require((fileIds != null) xor (files != null)) { "Specify exactly one of fileIds or files" }
        require(files == null || (attributes == null && chunkingStrategy == null)) {
            "With files, specify attributes and chunkingStrategy on each file"
        }
        require((fileIds?.size ?: files!!.size) in 1..2000) { "A batch must contain between 1 and 2000 files" }
        require(files?.all { it.fileId != null } != false) { "Each file configuration requires a fileId" }
    }
}
