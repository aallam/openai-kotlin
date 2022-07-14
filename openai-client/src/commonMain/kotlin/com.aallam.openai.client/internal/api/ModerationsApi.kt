package com.aallam.openai.client.internal.api

import com.aallam.openai.api.moderation.ModerationRequest
import com.aallam.openai.api.moderation.ModerationResponse
import com.aallam.openai.client.Moderations
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Implementation of [Moderations].
 */
internal class ModerationsApi(private val httpTransport: HttpTransport) : Moderations {

    override suspend fun moderations(request: ModerationRequest): ModerationResponse {
        return httpTransport.perform {
            it.post {
                url(path = ModerationsPathV1)
                setBody(request)
                contentType(ContentType.Application.Json)
            }.body()
        }
    }

    companion object {
        const val ModerationsPathV1 = "v1/moderations"
    }
}
