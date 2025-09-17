package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.response.Response
import com.aallam.openai.api.response.ResponseChunk
import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.client.Responses
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.extension.streamEventsFrom
import com.aallam.openai.client.internal.extension.streamRequestOf
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementation of [Responses].
 */
internal class ResponsesApi(private val requester: HttpRequester) : Responses {

    override suspend fun createResponse(
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

    override fun createResponseStream(
        request: ResponseRequest,
        requestOptions: RequestOptions?
    ): Flow<ResponseChunk> {
        val builder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(path = ApiPath.Responses)
            setBody(streamRequestOf(request))
            contentType(ContentType.Application.Json)
            accept(ContentType.Text.EventStream)
            headers {
                append(HttpHeaders.CacheControl, "no-cache")
                append(HttpHeaders.Connection, "keep-alive")
            }
            requestOptions(requestOptions)
        }
        return flow {
            requester.perform(builder) { response -> streamEventsFrom(response) }
        }
    }
}
