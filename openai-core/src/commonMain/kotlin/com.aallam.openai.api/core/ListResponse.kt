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

    /**
     * The ID of the first element returned.
     */
    @SerialName("first_id") public val firstId: String? = null,

    /**
     * The ID of the last element returned.
     */
    @SerialName("last_id") public val lastId: String? = null,

    /**
     * If the list is truncated.
     */
    @SerialName("has_more") public val hasMore: Boolean? = null,
) : List<T> by data
