package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import com.aallam.openai.api.responses.Response
import com.aallam.openai.api.responses.ResponseRequest
import com.aallam.openai.client.Responses

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class ResponsesApi(private val requester: HttpRequester) : Responses {
    override suspend fun createResponse(request: ResponseRequest, requestOptions: RequestOptions?): Response {
        return requester.perform { client: HttpClient ->
            client.post {
                url(path = ApiPath.Responses)
                setBody(request.copy(stream = false))
                contentType(ContentType.Application.Json)
            }.body()
        }
    }

    //TODO Add streaming

}