package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.DeleteResponse
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.vectorstore.VectorStore
import com.aallam.openai.api.vectorstore.VectorStoreId
import com.aallam.openai.api.vectorstore.VectorStoreRequest
import com.aallam.openai.client.VectorStores
import com.aallam.openai.client.internal.extension.beta
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class VectorStoresApi(private val requester: HttpRequester) : VectorStores {

    override suspend fun createVectorStore(
        request: VectorStoreRequest?,
        requestOptions: RequestOptions?
    ): VectorStore {
        return requester.perform {
            it.post {
                url(path = ApiPath.VectorStores)
                request?.let { req ->
                    setBody(req)
                    contentType(ContentType.Application.Json)
                }
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun vectorStores(
        limit: Int?,
        order: SortOrder?,
        after: VectorStoreId?,
        before: VectorStoreId?,
        requestOptions: RequestOptions?
    ): PaginatedList<VectorStore> {
        return requester.perform {
            it.get {
                url {
                    path(ApiPath.VectorStores)
                    limit?.let { parameter("limit", it) }
                    order?.let { parameter("order", it.order) }
                    after?.let { parameter("after", it.id) }
                    before?.let { parameter("before", it.id) }
                }
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun vectorStore(id: VectorStoreId, requestOptions: RequestOptions?): VectorStore? {
        try {
            return requester.perform<HttpResponse> {
                it.get {
                    url(path = "${ApiPath.VectorStores}/${id.id}")
                    beta("assistants", 2)
                    requestOptions(requestOptions)
                }
            }.body()
        } catch (e: OpenAIAPIException) {
            if (e.statusCode == HttpStatusCode.NotFound.value) return null
            throw e
        }
    }

    override suspend fun updateVectorStore(
        id: VectorStoreId,
        request: VectorStoreRequest,
        requestOptions: RequestOptions?
    ): VectorStore {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.VectorStores}/${id.id}")
                setBody(request)
                contentType(ContentType.Application.Json)
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun delete(id: VectorStoreId, requestOptions: RequestOptions?): Boolean {
        val response = requester.perform<HttpResponse> {
            it.delete {
                url(path = "${ApiPath.VectorStores}/${id.id}")
                beta("assistants", 2)
                requestOptions(requestOptions)
            }
        }
        return when (response.status) {
            HttpStatusCode.NotFound -> false
            else -> response.body<DeleteResponse>().deleted
        }
    }
}
