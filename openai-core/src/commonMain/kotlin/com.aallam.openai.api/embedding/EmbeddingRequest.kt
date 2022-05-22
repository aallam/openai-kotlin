package com.aallam.openai.api.embedding

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An embedding request.
 * [documentation](https://beta.openai.com/docs/api-reference/embeddings)
 */
@Serializable
public class EmbeddingRequest(

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
