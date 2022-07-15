package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.ListResponse
import com.aallam.openai.api.embedding.Embedding
import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.client.Embeddings
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Implementation of [Embeddings].
 */
internal class EmbeddingsApi(private val requester: HttpRequester) : Embeddings {

    override suspend fun embeddings(request: EmbeddingRequest): List<Embedding> {
        return requester.perform<ListResponse<Embedding>> {
            it.post {
                url(path = EmbeddingsPathV1)
                setBody(request)
                contentType(ContentType.Application.Json)
            }.body()
        }.data
    }

    companion object {
        private const val EmbeddingsPathV1 = "v1/embeddings"
    }
}
