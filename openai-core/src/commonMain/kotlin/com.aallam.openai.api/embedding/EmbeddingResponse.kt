package com.aallam.openai.api.embedding

import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response to an embedding request.
 */
@Serializable
public class EmbeddingResponse(
    /**
     * List containing the actual results.
     */
    @SerialName("data") public val data: List<Embedding>,

    /**
     * The model that executed the request.
     */
    @SerialName("model") public val model: ModelId,
)
