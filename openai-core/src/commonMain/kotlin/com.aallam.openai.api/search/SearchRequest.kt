package com.aallam.openai.api.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to the document search API.
 * [documentation](https://beta.openai.com/docs/api-reference/search)
 */
@Serializable
public class SearchRequest(
    /**
     * Query to search against the documents.
     */
    @SerialName("documents") public val documents: List<String>,
    /**
     * Up to 200 documents to search over, provided as a list of strings.
     * The maximum document length (in tokens) is 2034 minus the number of tokens in the query.
     */
    @SerialName("query") public val query: String,
)
