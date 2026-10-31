package com.aallam.openai.api.embedding

import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Embedding request supporting text, token IDs and encoded result vectors. */
@Serializable
public data class EmbeddingCreateRequest(
    /** Embedding model ID. */
    public val model: ModelId,
    /** Text or token IDs, optionally batched. */
    public val input: EmbeddingInput,
    /** Optional output dimensions for models supporting dimensionality reduction. */
    public val dimensions: Int? = null,
    /** End-user identifier. */
    public val user: String? = null,
    /** Response vector encoding; omitted uses the API default. */
    @SerialName("encoding_format") public val encodingFormat: EmbeddingEncodingFormat? = null,
)

/** Encoding used for embedding vectors. */
@Serializable
public enum class EmbeddingEncodingFormat {
    /** Numeric JSON arrays. */
    @SerialName("float") Float,
    /** Base64-encoded float32 values. */
    @SerialName("base64") Base64,
}
