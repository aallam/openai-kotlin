package com.aallam.openai.client.internal.api

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.client.Completions
import com.aallam.openai.client.internal.extension.streamEventsOf
import com.aallam.openai.client.internal.extension.toStreamRequest
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [Completions].
 */
internal class CompletionsApi(private val requester: HttpRequester) : Completions {

    override suspend fun completion(request: CompletionRequest): TextCompletion {
        return requester.perform {
            it.post {
                url(path = CompletionsPathV1)
                setBody(request)
                contentType(ContentType.Application.Json)
            }.body()
        }
    }

    override fun completions(request: CompletionRequest): Flow<TextCompletion> {
        return streamEventsOf {
            requester.perform {
                it.post {
                    url(path = CompletionsPathV1)
                    setBody(request.toStreamRequest())
                    contentType(ContentType.Application.Json)
                }
            }
        }
    }

    companion object {
        private const val CompletionsPathV1 = "v1/completions"
    }
}
