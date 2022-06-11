@file:Suppress("DEPRECATION")

package com.aallam.openai.client

import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.api.search.SearchRequest
import com.aallam.openai.api.search.SearchResult

/**
 * Given a query and a set of documents or labels, the model ranks each document based on its semantic similarity
 * to the provided query.
 */
@Deprecated("Search APIs are deprecated")

public interface Searches {

    /**
     * Performs a semantic search over a list of documents.
     * Response includes the list of scored documents (in the same order that they were passed in).
     */
    @Deprecated("Search APIs are deprecated")

    public suspend fun search(
        engineId: EngineId,
        request: SearchRequest
    ): List<SearchResult>
}
