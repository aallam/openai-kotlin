package com.aallam.openai.client.internal.api

import com.aallam.openai.api.edits.EditsRequest
import com.aallam.openai.api.edits.EditsResponse
import com.aallam.openai.client.Edits
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Implementation of [Edits]
 */
internal class EditsApi(private val httpRequester: HttpTransport) : Edits {

    override suspend fun edit(request: EditsRequest): EditsResponse {
        return httpRequester.perform {
            it.post {
                url(path = EditsPathV1)
                setBody(request)
                contentType(ContentType.Application.Json)
            }.body()
        }
    }

    companion object {
        private const val EditsPathV1 = "v1/edits"
    }
}
