package com.aallam.openai.api.vectorstore

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.file.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

/**
 * A batch file request.
 */
@BetaOpenAI
@Serializable
public data class FileBatchRequest(

    /**
     * A list of File IDs that the vector store should use. Useful for tools like file_search that can access files.
     */
    @SerialName("file_ids") public val fileIds: List<FileId>,

    /** File attributes used for filtering search results. */
    @SerialName("attributes") public val attributes: Map<String, JsonPrimitive>? = null,
    /** Chunking strategy used to process the file(s). */
    @SerialName("chunking_strategy") public val chunkingStrategy: ChunkingStrategy? = null,

)
