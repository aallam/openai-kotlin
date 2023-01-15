package com.aallam.openai.api.embedding

import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Create an embedding request.
 *
 * [documentation](https://beta.openai.com/docs/api-reference/embeddings)
 */
@Serializable
public class EmbeddingRequest(

    /**
     * ID of the model to use.
     */
    @SerialName("model") public val model: ModelId,

    /**
     * Input text to get embeddings for, encoded as an array of token. Each input must not exceed 2048 tokens in length.
     *
     * Unless you are embedding code, we suggest replacing newlines (`\n`) in your input with a single space, as we have
     * observed inferior results when newlines are present.
     */
    @SerialName("input") public val input: List<String>,

    /**
     * A unique identifier representing your end-user, which will help OpenAI to monitor and detect abuse.
     */
    @SerialName("user") public val user: String? = null,
)

/**
 * Create an embedding request.
 *
 * [documentation](https://beta.openai.com/docs/api-reference/embeddings)
 */
public fun embeddingRequest(block: EmbeddingRequestDSL.() -> Unit): EmbeddingRequest =
    EmbeddingRequestDSL().apply(block).build()

/**
 * DSL to build a [EmbeddingRequest] instance.
 */
public class EmbeddingRequestDSL {

    /**
     * ID of the model to use.
     */
    public var model: ModelId? = null

    /**
     * Input text to get embeddings for, encoded as an array of token. Each input must not exceed 2048 tokens in length.
     *
     * Unless you are embedding code, we suggest replacing newlines (`\n`) in your input with a single space, as we have
     * observed inferior results when newlines are present.
     */
    public var input: List<String>? = null

    /**
     * A unique identifier representing your end-user, which will help OpenAI to monitor and detect abuse.
     */
    public var user: String? = null

    /**
     * Create [EmbeddingRequest] instance.
     */
    public fun build(): EmbeddingRequest = EmbeddingRequest(
        model = requireNotNull(model) { "model is required" },
        input = requireNotNull(input) { "input is required" },
        user = user
    )
}
