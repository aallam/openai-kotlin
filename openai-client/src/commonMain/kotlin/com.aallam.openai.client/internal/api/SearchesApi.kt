package com.aallam.openai.client.internal.api

import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.api.search.SearchRequest
import com.aallam.openai.api.search.SearchResponse
import com.aallam.openai.api.search.SearchResult
import com.aallam.openai.client.Searches
import com.aallam.openai.client.internal.api.EnginesApi.Companion.EnginesPath
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Implementation of [Searches].
 */
internal class SearchesApi(private val httpClient: HttpClient) : Searches {

    override suspend fun search(
        engineId: EngineId,
        request: SearchRequest
    ): List<SearchResult> {
        return httpClient.post {
            url(path = "${EnginesPath}/$engineId/search")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<SearchResponse>().data
    }
}
