package com.aallam.openai.api.embedding

import com.aallam.openai.api.core.Usage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Create embeddings response.
 */
@Serializable
public class EmbeddingResponse(

    /**
     * An embedding results.
     */
    @SerialName("data") public val embeddings: List<Embedding>,

    /**
     * Embedding usage data.
     */
    @SerialName("usage") public val usage: Usage,
)
