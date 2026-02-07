package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.DeleteResponse
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.response.Response
import com.aallam.openai.api.response.ResponseId
import com.aallam.openai.api.response.ResponseInputItem
import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.client.Responses
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class ResponsesApi(private val requester: HttpRequester) : Responses {
    override suspend fun response(
        request: ResponseRequest,
        requestOptions: RequestOptions?
    ): Response {
        return requester.perform {
            it.post {
                url(path = ApiPath.Responses)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun response(id: ResponseId, requestOptions: RequestOptions?): Response? {
        val response = requester.perform<HttpResponse> {
            it.get {
                url(path = "${ApiPath.Responses}/${id.id}")
                requestOptions(requestOptions)
            }
        }
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    override suspend fun delete(id: ResponseId, requestOptions: RequestOptions?): Boolean {
        val response = requester.perform<HttpResponse> {
            it.delete {
                url(path = "${ApiPath.Responses}/${id.id}")
                requestOptions(requestOptions)
            }
        }
        return when (response.status) {
            HttpStatusCode.NotFound -> false
            else -> response.body<DeleteResponse>().deleted
        }
    }

    override suspend fun cancel(id: ResponseId, requestOptions: RequestOptions?): Response? {
        val response = requester.perform<HttpResponse> {
            it.post {
                url(path = "${ApiPath.Responses}/${id.id}/cancel")
                requestOptions(requestOptions)
            }
        }
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    override suspend fun responseInputItems(
        id: ResponseId,
        limit: Int?,
        order: SortOrder?,
        after: String?,
        before: String?,
        requestOptions: RequestOptions?
    ): PaginatedList<ResponseInputItem> {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Responses}/${id.id}/input_items") {
                    limit?.let { value -> parameter("limit", value) }
                    order?.let { value -> parameter("order", value.order) }
                    after?.let { value -> parameter("after", value) }
                    before?.let { value -> parameter("before", value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }
}
