package com.aallam.openai.client.internal.api

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.*
import com.aallam.openai.client.Audio
import com.aallam.openai.client.internal.extension.appendFileSource
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
    override suspend fun transcription(request: TranscriptionRequest): Transcription {
        return requester.perform {
            it.submitFormWithBinaryData(url = TranscriptionPathV1, formData = formDataOf(request))
        }
    }

    @BetaOpenAI
    override suspend fun transcriptionText(request: TranscriptionRequest): TranscriptionText {
        val text = requester.perform<String> {
            it.submitFormWithBinaryData(url = TranscriptionPathV1, formData = formDataOf(request, "text")) {
                accept(ContentType.Text.Plain)
            }
        }
        return TranscriptionText(text)
    }

    @BetaOpenAI
    override suspend fun transcriptionJson(request: TranscriptionRequest): TranscriptionJson {
        return requester.perform {
            it.submitFormWithBinaryData(url = TranscriptionPathV1, formData = formDataOf(request, "json"))
        }
    }

    @BetaOpenAI
    override suspend fun transcriptionJsonVerbose(request: TranscriptionRequest): TranscriptionJson {
        return requester.perform {
            it.submitFormWithBinaryData(url = TranscriptionPathV1, formData = formDataOf(request, "verbose_json"))
        }
    }

    @BetaOpenAI
    override suspend fun transcriptionVTT(request: TranscriptionRequest): TranscriptionVTT {
        val vtt = requester.perform<String> {
            it.submitFormWithBinaryData(url = TranscriptionPathV1, formData = formDataOf(request, "vtt")) {
                accept(ContentTypeVTT)
            }
        }
        return TranscriptionVTT(vtt)
    }

    @BetaOpenAI
    override suspend fun transcriptionSRT(request: TranscriptionRequest): TranscriptionSRT {
        val srt = requester.perform<String> {
            it.submitFormWithBinaryData(url = TranscriptionPathV1, formData = formDataOf(request, "srt")) {
                accept(ContentType.Text.Plain)
            }
        }
        return TranscriptionSRT(srt)
    }

    /**
     * Build transcription request as form-data.
     */
    private fun formDataOf(request: TranscriptionRequest, format: String? = null) = formData {
        appendFileSource("file", request.audio)
        append(key = "model", value = request.model.id)
        request.prompt?.let { prompt -> append(key = "prompt", value = prompt) }
        val responseFormat = format ?: request.responseFormat
        responseFormat?.let { append(key = "response_format", value = it) }
        request.temperature?.let { append(key = "temperature", value = it) }
        request.language?.let { append(key = "language", value = it) }
    }

    @BetaOpenAI
    override suspend fun translation(request: TranslationRequest): Translation {
        return requester.perform {
            it.submitFormWithBinaryData(url = TranslationPathV1, formData = formDataOf(request))
        }
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

    companion object {
        private const val TranslationPathV1 = "v1/audio/translations"
        private const val TranscriptionPathV1 = "v1/audio/transcriptions"
        private val ContentTypeVTT = ContentType("text", "vtt")
    }
}
