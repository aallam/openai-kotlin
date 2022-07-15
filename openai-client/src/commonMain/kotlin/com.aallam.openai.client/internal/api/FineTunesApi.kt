package com.aallam.openai.client.internal.api

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.finetune.FineTune
import com.aallam.openai.api.finetune.FineTuneEvent
import com.aallam.openai.api.finetune.FineTuneId
import com.aallam.openai.api.finetune.FineTuneRequest
import com.aallam.openai.client.FineTunes
import com.aallam.openai.client.internal.api.ModelsApi.Companion.ModelsPathV1
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.request.delete
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [FineTunes].
 */
internal class FineTunesApi(private val requester: HttpRequester) : FineTunes {

    @ExperimentalOpenAI
    override suspend fun fineTune(request: FineTuneRequest): FineTune {
        TODO("Not yet implemented")
    }

    @ExperimentalOpenAI
    override suspend fun fineTune(fineTuneId: FineTuneId): FineTune? {
        TODO("Not yet implemented")
    }

    @ExperimentalOpenAI
    override suspend fun fineTunes(): List<FineTune> {
        TODO("Not yet implemented")
    }

    @ExperimentalOpenAI
    override suspend fun cancelFineTune(fineTuneId: FineTuneId): FineTune? {
        TODO("Not yet implemented")
    }

    @ExperimentalOpenAI
    override fun fineTuneEvents(fineTuneId: FineTuneId): Flow<FineTuneEvent> {
        TODO("Not yet implemented")
    }

    @ExperimentalOpenAI
    override suspend fun deleteFineTune(fineTuneId: FineTuneId) {
        requester.perform<HttpResponse> {
            it.delete {
                url(path = "$ModelsPathV1/$fineTuneId")
            }
        }
    }

    companion object {
        private const val FineTunesPathV1 = "v1/fine-tunes"
    }
}
