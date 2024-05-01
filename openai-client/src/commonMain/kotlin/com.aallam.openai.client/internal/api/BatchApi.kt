package com.aallam.openai.client.internal.api

import com.aallam.openai.api.batch.BatchId
import com.aallam.openai.api.batch.BatchRequest
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.client.Batch
import com.aallam.openai.client.internal.extension.beta
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import com.aallam.openai.api.batch.Batch as BatchObject

/**
 * Implementation of [Batch].
 */
internal class BatchApi(val requester: HttpRequester) : Batch {

    override suspend fun batch(
        request: BatchRequest,
        requestOptions: RequestOptions?
    ): BatchObject {
        return requester.perform {
            it.post {
                url(path = ApiPath.Batches)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun batch(id: BatchId, requestOptions: RequestOptions?): BatchObject? {
        try {
            return requester.perform<HttpResponse> {
                it.get {
                    url(path = "${ApiPath.Batches}/${id.id}")
                    requestOptions(requestOptions)
                }
            }.body()
        } catch (e: OpenAIAPIException) {
            if (e.statusCode == HttpStatusCode.NotFound.value) return null
            throw e
        }
    }

    override suspend fun cancel(id: BatchId, requestOptions: RequestOptions?): BatchObject? {
        val response = requester.perform<HttpResponse> {
            it.post {
                url(path = "${ApiPath.Batches}/${id.id}/cancel")
                requestOptions(requestOptions)
            }
        }
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    override suspend fun batches(
        after: BatchId?,
        limit: Int?,
        requestOptions: RequestOptions?
    ): PaginatedList<BatchObject> {
        return requester.perform {
            it.get {
                url {
                    path(ApiPath.Batches)
                    limit?.let { parameter("limit", it) }
                    after?.let { parameter("after", it.id) }
                }
                beta("assistants", 2)
                requestOptions(requestOptions)
            }.body()
        }
    }
}
