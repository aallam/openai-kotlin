package com.aallam.openai.api.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response as List of [T].
 */
@Serializable
public class ListResponse<T>(

    /**
     * List containing the actual results.
     */
    @SerialName("data") public val data: List<T>,

    /**
     * Embedding usage data.
     */
    @SerialName("usage") public val usage: Usage? = null,
)
