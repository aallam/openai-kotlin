package com.aallam.openai.api.vectorstore

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Creates a file batch with separate configuration for each attachment. */
@BetaOpenAI
@Serializable
public data class VectorStoreFileBatchRequest(
    /** Files to attach, with their attributes and chunking policies. */
    @SerialName("files") public val files: List<VectorStoreFileRequest>,
)
