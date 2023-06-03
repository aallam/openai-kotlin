package com.aallam.openai.client.internal.api

import com.aallam.openai.api.moderation.ModerationRequest
import com.aallam.openai.api.moderation.TextModeration
import com.aallam.openai.client.Moderations
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Implementation of [Moderations].
 */
internal class ModerationsApi(private val requester: HttpRequester) : Moderations {

    override suspend fun moderations(request: ModerationRequest): TextModeration {
        return requester.perform {
            it.post {
                url(path = ApiPath.Moderations)
                setBody(request)
                contentType(ContentType.Application.Json)
            }.body()
        }
    }
}
