package com.aallam.openai.client.internal.api

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.core.ListResponse
import com.aallam.openai.api.finetunes.FineTune
import com.aallam.openai.api.finetunes.FineTuneEvent
import com.aallam.openai.api.finetunes.FineTuneId
import com.aallam.openai.api.finetunes.FineTuneRequest
import com.aallam.openai.client.FineTunes
import com.aallam.openai.client.internal.http.HttpTransport
import com.aallam.openai.client.internal.http.requestStream
import com.aallam.openai.client.internal.http.streamSSE
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [FineTunes].
 */
internal class FineTunesApi(private val httpTransport: HttpTransport) : FineTunes {

    @ExperimentalOpenAI
    override suspend fun fineTune(request: FineTuneRequest): FineTune {
        return httpTransport.perform {
            it.post {
                url(path = FineTunesPath)
                setBody(request)
            }
        }
    }

    @ExperimentalOpenAI
    override suspend fun fineTunes(): List<FineTune> {
        return httpTransport.perform<ListResponse<FineTune>> {
            it.get { url(path = FineTunesPath) }
        }.data
    }

    @ExperimentalOpenAI
    override suspend fun fineTune(fineTuneId: FineTuneId): FineTune? {
        val response = httpTransport.perform<HttpResponse> {
            it.get { url(path = "$FineTunesPath/$fineTuneId") }
        }
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    @ExperimentalOpenAI
    override suspend fun cancelFineTune(fineTuneId: FineTuneId): FineTune? {
        val response = httpTransport.perform<HttpResponse> {
            it.post { url(path = "$FineTunesPath/$fineTuneId/cancel") }
        }
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    @ExperimentalOpenAI
    override fun fineTuneEvents(fineTuneId: FineTuneId): Flow<FineTuneEvent> {
        return streamSSE {
            httpTransport.perform {
                it.post {
                    url(path = "$FineTunesPath/$fineTuneId/events")
                    setBody(requestStream())
                }
            }
        }
    }

    @ExperimentalOpenAI
    override suspend fun deleteFineTune(fineTuneId: FineTuneId) {
        httpTransport.perform<HttpResponse> { it.delete { url(path = "$ModelPath/$fineTuneId") } }
    }

    companion object {
        private const val FineTunesPath = "/v1/fine-tunes"
        private const val ModelPath = "/v1/models/"
    }
}
