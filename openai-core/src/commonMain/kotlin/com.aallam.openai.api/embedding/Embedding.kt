package com.aallam.openai.api.embedding

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An embedding result.
 * [documentation](https://beta.openai.com/docs/api-reference/embeddings)
 */
@Serializable
public class Embedding(
    @SerialName("embedding") public val embedding: List<Double>,
    @SerialName("index") public val index: Int,
)
