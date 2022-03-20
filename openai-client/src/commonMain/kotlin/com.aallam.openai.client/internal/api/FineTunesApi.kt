package com.aallam.openai.client.internal.api

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.finetunes.FineTune
import com.aallam.openai.api.finetunes.FineTuneEvent
import com.aallam.openai.api.finetunes.FineTuneId
import com.aallam.openai.api.finetunes.FineTuneRequest
import com.aallam.openai.client.FineTunes
import com.aallam.openai.client.internal.http.HttpTransport
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [FineTunes].
 */
internal class FineTunesApi(private val httpTransport: HttpTransport) : FineTunes {

    @ExperimentalOpenAI
    override fun fineTune(request: FineTuneRequest): FineTune {
        TODO("Not yet implemented")
    }

    @ExperimentalOpenAI
    override fun fineTune(id: FineTuneId): FineTune? {
        TODO("Not yet implemented")
    }

    @ExperimentalOpenAI
    override fun fineTunes(): List<FineTune> {
        TODO("Not yet implemented")
    }

    @ExperimentalOpenAI
    override fun cancelFineTune(id: FineTuneId): FineTune? {
        TODO("Not yet implemented")
    }

    @ExperimentalOpenAI
    override fun fineTuneEvents(id: FineTuneId): Flow<FineTuneEvent> {
        TODO("Not yet implemented")
    }

    @ExperimentalOpenAI
    override fun deleteFineTune(id: FineTuneId): Boolean {
        TODO("Not yet implemented")
    }

    companion object {
        private const val FineTunesPath = "/v1/fine-tunes"
    }
}
