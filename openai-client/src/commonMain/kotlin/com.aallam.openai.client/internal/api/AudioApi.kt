package com.aallam.openai.client.internal.api

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.*
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.client.Audio
import com.aallam.openai.client.internal.extension.appendFileSource
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

/**
 * Implementation of [Audio].
 */
internal class AudioApi(val requester: HttpRequester) : Audio {
    @BetaOpenAI
    override suspend fun transcription(request: TranscriptionRequest, requestOptions: RequestOptions?): Transcription {
        return when (request.responseFormat) {
            AudioResponseFormat.Json, AudioResponseFormat.VerboseJson, null -> transcriptionAsJson(
                request,
                requestOptions
            )

            else -> transcriptionAsString(request, requestOptions)
        }
    }

    private suspend fun transcriptionAsJson(
        request: TranscriptionRequest,
        requestOptions: RequestOptions?
    ): Transcription {
        return requester.perform {
            it.submitFormWithBinaryData(url = ApiPath.Transcription, formData = formDataOf(request)) {
                requestOptions(requestOptions)
            }
        }
    }

    private suspend fun transcriptionAsString(
        request: TranscriptionRequest,
        requestOptions: RequestOptions?
    ): Transcription {
        val text = requester.perform<String> {
            it.submitFormWithBinaryData(url = ApiPath.Transcription, formData = formDataOf(request)) {
                requestOptions(requestOptions)
            }
        }
        return Transcription(text)
    }

    /**
     * Build transcription request as form-data.
     */
    private fun formDataOf(request: TranscriptionRequest) = formData {
        appendFileSource("file", request.audio)
        append(key = "model", value = request.model.id)
        request.prompt?.let { prompt -> append(key = "prompt", value = prompt) }
        request.responseFormat?.let { append(key = "response_format", value = it.value) }
        request.temperature?.let { append(key = "temperature", value = it) }
        request.language?.let { append(key = "language", value = it) }
        if (request.responseFormat == AudioResponseFormat.VerboseJson) {
            for (timestampGranularity in request.timestampGranularities.orEmpty()) {
                append(key = "timestamp_granularities[]", value = timestampGranularity.value)
            }
        }
    }

    @BetaOpenAI
    override suspend fun translation(request: TranslationRequest, requestOptions: RequestOptions?): Translation {
        return when (request.responseFormat) {
            "json", "verbose_json", null -> translationAsJson(request, requestOptions)
            else -> translationAsString(request, requestOptions)
        }
    }

    private suspend fun translationAsJson(request: TranslationRequest, requestOptions: RequestOptions?): Translation {
        return requester.perform {
            it.submitFormWithBinaryData(url = ApiPath.Translation, formData = formDataOf(request)) {
                requestOptions(requestOptions)
            }
        }
    }

    private suspend fun translationAsString(request: TranslationRequest, requestOptions: RequestOptions?): Translation {
        val text = requester.perform<String> {
            it.submitFormWithBinaryData(url = ApiPath.Translation, formData = formDataOf(request)) {
                requestOptions(requestOptions)
            }
        }
        return Translation(text)
    }

    /**
     * Build transcription request as form-data.
     */
    private fun formDataOf(request: TranslationRequest) = formData {
        appendFileSource("file", request.audio)
        append(key = "model", value = request.model.id)
        request.prompt?.let { prompt -> append(key = "prompt", value = prompt) }
        request.responseFormat?.let { append(key = "response_format", value = it) }
        request.temperature?.let { append(key = "temperature", value = it) }
    }

    override suspend fun speech(request: SpeechRequest, requestOptions: RequestOptions?): ByteArray {
        return requester.perform {
            it.post {
                url(ApiPath.Speech)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }
        }
    }
}
