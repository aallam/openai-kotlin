package com.aallam.openai.client.internal.api

import com.aallam.openai.api.edits.Edit
import com.aallam.openai.api.edits.EditsRequest
import com.aallam.openai.client.Edits
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Implementation of [Edits]
 */
internal class EditsApi(private val requester: HttpRequester) : Edits {

    override suspend fun edit(request: EditsRequest): Edit {
        return requester.perform {
            it.post {
                url(path = ApiPath.Edits)
                setBody(request)
                contentType(ContentType.Application.Json)
            }.body()
        }
    }
}
