package com.aallam.openai.client.internal.api

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.core.DeleteResponse
import com.aallam.openai.api.core.ListResponse
import com.aallam.openai.api.finetune.FineTune
import com.aallam.openai.api.finetune.FineTuneEvent
import com.aallam.openai.api.finetune.FineTuneId
import com.aallam.openai.api.finetune.FineTuneRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.FineTunes
import com.aallam.openai.client.internal.api.ModelsApi.Companion.ModelsPathV1
import com.aallam.openai.client.internal.extension.streamEventsFrom
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Implementation of [FineTunes].
 */
internal class FineTunesApi(private val requester: HttpRequester) : FineTunes {

    @ExperimentalOpenAI
    override suspend fun fineTune(request: FineTuneRequest): FineTune {
        return requester.perform {
            it.post {
                url(path = FineTunesPathV1)
                setBody(request)
                contentType(ContentType.Application.Json)
            }
        }
    }

    @ExperimentalOpenAI
    override suspend fun fineTune(fineTuneId: FineTuneId): FineTune? {
        val response = requester.perform<HttpResponse> {
            it.get { url(path = "$FineTunesPathV1/${fineTuneId.id}") }
        }
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    @ExperimentalOpenAI
    override suspend fun fineTunes(): List<FineTune> {
        return requester.perform<ListResponse<FineTune>> {
            it.get { url(path = FineTunesPathV1) }
        }.data
    }

    @ExperimentalOpenAI
    override suspend fun cancel(fineTuneId: FineTuneId): FineTune? {
        val response = requester.perform<HttpResponse> {
            it.post { url(path = "$FineTunesPathV1/${fineTuneId.id}/cancel") }
        }
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    @ExperimentalOpenAI
    override suspend fun fineTuneEvents(fineTuneId: FineTuneId): List<FineTuneEvent> {
        return requester.perform<ListResponse<FineTuneEvent>> {
            it.get { url(path = "$FineTunesPathV1/${fineTuneId.id}/events") }
        }.data
    }

    @ExperimentalOpenAI
    override fun fineTuneEventsFlow(fineTuneId: FineTuneId): Flow<FineTuneEvent> {
        val request = HttpRequestBuilder().apply {
            method = HttpMethod.Get
            url(path = "$FineTunesPathV1/${fineTuneId.id}/events") {
                parameters.append("stream", "true")
            }
            accept(ContentType.Text.EventStream)
            headers {
                append(HttpHeaders.CacheControl, "no-cache")
                append(HttpHeaders.Connection, "keep-alive")
            }
        }
        return flow {
            requester.perform(request) { response -> streamEventsFrom(response) }
        }
    }

    @ExperimentalOpenAI
    override suspend fun delete(fineTuneModel: ModelId): Boolean {
        val response = requester.perform<HttpResponse> {
            it.delete {
                url(path = "$ModelsPathV1/${fineTuneModel.id}")
            }
        }
        return when (response.status) {
            HttpStatusCode.NotFound -> false
            else -> response.body<DeleteResponse>().deleted
        }
    }

    companion object {
        private const val FineTunesPathV1 = "v1/fine-tunes"
    }
}
