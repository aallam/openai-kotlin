package com.aallam.openai.client.internal.api

import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.embedding.EmbeddingResponse
import com.aallam.openai.api.embedding.Embedding
import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.client.Embeddings
import com.aallam.openai.client.internal.api.EnginesApi.Companion.EnginesPath
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Implementation of [Embeddings].
 */
internal class EmbeddingsApi(private val httpRequester: HttpTransport) : Embeddings {

    override suspend fun embeddings(engineId: EngineId, request: EmbeddingRequest): List<Embedding> {
        return httpRequester.perform<EmbeddingResponse> {
            it.post {
                url(path = "$EnginesPath/$engineId/embeddings")
                setBody(request)
                contentType(ContentType.Application.Json)
            }.body()
        }.data
    }
}
