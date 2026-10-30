package com.aallam.openai.client.internal.api

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.*
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.client.Audio
import com.aallam.openai.client.internal.JsonLenient
import com.aallam.openai.client.internal.extension.audioEventsFrom
import io.ktor.client.call.*
import io.ktor.utils.io.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.*
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
            AudioResponseFormat.Json, AudioResponseFormat.VerboseJson, null ->
                transcriptionAsJson(request, requestOptions)
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
    private fun formDataOf(request: TranscriptionRequest, options: TranscriptionOptions? = null, stream: Boolean = false) = formData {
        appendFileSource("file", request.audio)
        append(key = "model", value = request.model.id)
        request.prompt?.let { prompt -> append(key = "prompt", value = prompt) }
        request.responseFormat?.let { append(key = "response_format", value = it.value) }
        request.temperature?.let { append(key = "temperature", value = it) }
        request.language?.let { append(key = "language", value = it) }
        if (stream) append("stream", "true")
        options?.let { extra ->
            extra.chunkingStrategy?.let { chunking ->
                val json = JsonLenient.encodeToJsonElement(TranscriptionChunkingStrategy.serializer(), chunking)
                if (json is JsonPrimitive) append("chunking_strategy", json.content)
                else json.jsonObject.forEach { (key, value) -> append("chunking_strategy[$key]", value.jsonPrimitive.content) }
            }
            extra.include?.forEach { append("include[]", it) }
            extra.keywords?.forEach { append("keywords[]", it) }
            extra.knownSpeakerNames?.forEach { append("known_speaker_names[]", it) }
            extra.knownSpeakerReferences?.forEach { append("known_speaker_references[]", it) }
            extra.languages?.forEach { append("languages[]", it) }
        }
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

    override suspend fun transcription(
        request: TranscriptionRequest,
        options: TranscriptionOptions,
        requestOptions: RequestOptions?,
    ): AudioTranscription {
        val format = request.responseFormat
        return if (format == null || format == AudioResponseFormat.Json || format == AudioResponseFormat.VerboseJson || format == AudioResponseFormat.DiarizedJson) {
            requester.perform {
                it.submitFormWithBinaryData(url = ApiPath.Transcription, formData = formDataOf(request, options)) {
                    requestOptions(requestOptions)
                }
            }
        } else {
            val text = requester.perform<String> {
                it.submitFormWithBinaryData(url = ApiPath.Transcription, formData = formDataOf(request, options)) {
                    requestOptions(requestOptions)
                }
            }
            AudioTranscription(text)
        }
    }

    override fun transcriptions(
        request: TranscriptionRequest,
        options: TranscriptionOptions,
        requestOptions: RequestOptions?,
    ): Flow<TranscriptionEvent> = flow {
        val builder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(path = ApiPath.Transcription)
            setBody(MultiPartFormDataContent(formDataOf(request, options, stream = true)))
            accept(ContentType.Text.EventStream)
            requestOptions(requestOptions)
        }
        requester.perform(builder) { response -> audioEventsFrom(response) }
    }

    override suspend fun speech(request: SpeechRequest, options: SpeechOptions, requestOptions: RequestOptions?): ByteArray {
        require(options.streamFormat != SpeechStreamFormat.Sse) { "Use speechEvents for SSE output" }
        return requester.perform {
            it.post {
                url(ApiPath.Speech)
                setBody(speechBody(request, options))
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }
        }
    }

    override fun speechAudio(request: SpeechRequest, options: SpeechOptions, requestOptions: RequestOptions?): Flow<ByteArray> = flow {
        val builder = speechBuilder(request, options.copy(streamFormat = SpeechStreamFormat.Audio), requestOptions)
        requester.perform(builder) { response ->
            val channel: ByteReadChannel = response.body()
            try {
                val buffer = ByteArray(8192)
                while (currentCoroutineContext().isActive) {
                    val read = channel.readAvailable(buffer)
                    if (read == -1) break
                    if (read > 0) emit(buffer.copyOf(read))
                }
            } finally {
                channel.cancel()
            }
        }
    }

    override fun speechEvents(request: SpeechRequest, options: SpeechOptions, requestOptions: RequestOptions?): Flow<SpeechEvent> = flow {
        val builder = speechBuilder(request, options.copy(streamFormat = SpeechStreamFormat.Sse), requestOptions).apply {
            accept(ContentType.Text.EventStream)
        }
        requester.perform(builder) { response -> audioEventsFrom(response) }
    }

    private fun speechBuilder(request: SpeechRequest, options: SpeechOptions, requestOptions: RequestOptions?) = HttpRequestBuilder().apply {
        method = HttpMethod.Post
        url(path = ApiPath.Speech)
        setBody(speechBody(request, options))
        contentType(ContentType.Application.Json)
        requestOptions(requestOptions)
    }

    private fun speechBody(request: SpeechRequest, options: SpeechOptions): JsonObject = JsonObject(
        JsonLenient.encodeToJsonElement(SpeechRequest.serializer(), request).jsonObject +
            JsonLenient.encodeToJsonElement(SpeechOptions.serializer(), options).jsonObject
    )
}
