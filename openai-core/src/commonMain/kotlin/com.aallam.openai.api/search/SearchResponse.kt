@file:Suppress("DEPRECATION")

package com.aallam.openai.api.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response to a search query.
 */
@Deprecated("Search APIs are deprecated")

@Serializable
public class SearchResponse(
    /**
     * List containing the actual results.
     */
    @SerialName("data") public val data: List<SearchResult>,

    /**
     * The model that executed the request.
     */
    @SerialName("model") public val model: String,
)
