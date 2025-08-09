package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.DeleteResponse
import com.aallam.openai.api.core.ListResponse
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.responses.Response
import com.aallam.openai.api.responses.ResponseIncludable
import com.aallam.openai.api.responses.ResponseItem
import com.aallam.openai.api.responses.ResponseRequest
import com.aallam.openai.client.Responses
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class ResponsesApi(private val requester: HttpRequester) : Responses {
    override suspend fun createResponse(request: ResponseRequest, requestOptions: RequestOptions?): Response {
        return requester.perform { client: HttpClient ->
            client.post {
                url(path = ApiPath.Responses)
                setBody(request.copy(stream = false))
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun getResponse(
        responseId: String,
        include: List<ResponseIncludable>?,
        requestOptions: RequestOptions?
    ): Response {
        return requester.perform { client: HttpClient ->
            client.get {
                url(path = "${ApiPath.Responses}/$responseId")
                parameter("include", include)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun deleteResponse(responseId: String, requestOptions: RequestOptions?): Boolean {
        val response = requester.perform<HttpResponse> {
            it.delete {
                url(path = "${ApiPath.Responses}/$responseId")
                requestOptions(requestOptions)
            }
        }

        return when (response.status) {
            HttpStatusCode.NotFound -> false
            else -> response.body<DeleteResponse>().deleted
        }
    }

    override suspend fun cancelResponse(responseId: String, requestOptions: RequestOptions?): Response {
        return requester.perform<HttpResponse> {
            it.post {
                url(path = "${ApiPath.Responses}/$responseId/cancel")
                requestOptions(requestOptions)
            }
        }.body()
    }

    override suspend fun listInputItems(
        responseId: String,
        after: String?,
        before: String?,
        include: List<ResponseIncludable>?,
        limit: Int?,
        order: String?,
        requestOptions: RequestOptions?
    ): List<ResponseItem> {
        return requester.perform<ListResponse<ResponseItem>> {
            it.get {
                url(path = "${ApiPath.Responses}/$responseId/items")
                parameter("after", after)
                parameter("before", before)
                parameter("include", include)
                parameter("limit", limit)
                parameter("order", order)
                requestOptions(requestOptions)
            }
        }.data
    }

    //TODO Add streaming

}
