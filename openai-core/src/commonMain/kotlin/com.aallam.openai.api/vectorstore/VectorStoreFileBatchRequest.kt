package com.aallam.openai.api.vectorstore

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

/** Creates a file batch with separate configuration for each attachment. */
@BetaOpenAI
@Serializable
public data class VectorStoreFileBatchRequest(
    /** Files to attach, with their attributes and chunking policies. */
    @SerialName("files") public val files: List<VectorStoreFileCreateRequest>,
    /** Default attributes for attached files. */
    @SerialName("attributes") public val attributes: Map<String, JsonPrimitive>? = null,
    /** Default chunking policy for attached files. */
    @SerialName("chunking_strategy") public val chunkingStrategy: ChunkingStrategy? = null,
)
