package com.aallam.openai.client.internal.api

import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.api.search.SearchRequest
import com.aallam.openai.api.search.SearchResponse
import com.aallam.openai.api.search.SearchResult
import com.aallam.openai.client.Searches
import com.aallam.openai.client.internal.api.EnginesApi.Companion.EnginesPath
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Implementation of [Searches].
 */
internal class SearchesApi(private val httpTransport: HttpTransport) : Searches {

    override suspend fun search(
        engineId: EngineId,
        request: SearchRequest
    ): List<SearchResult> {
        return httpTransport.perform<SearchResponse> {
            it.post {
                url(path = "${EnginesPath}/$engineId/search")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
        }.data
    }
}
