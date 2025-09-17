package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.response.Response
import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.client.Responses
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

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
}
