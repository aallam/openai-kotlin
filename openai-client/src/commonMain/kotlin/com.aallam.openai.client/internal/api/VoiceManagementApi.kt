package com.aallam.openai.client.internal.api

import com.aallam.openai.client.internal.SingleUseRequest
import com.aallam.openai.api.core.DeleteResponse
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.voice.*
import com.aallam.openai.client.VoiceManagement
import com.aallam.openai.client.internal.extension.appendFileSource
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.plugins.retry
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.io.RawSource
import kotlinx.io.buffered

internal class VoiceManagementApi(private val requester: HttpRequester) : VoiceManagement {
    override suspend fun createVoice(request: VoiceCreateRequest, requestOptions: RequestOptions?): VoiceResource = when (request) {
        is VoiceCreateRequest.Prompt -> requester.perform {
            it.post {
                url(path = "audio/voices")
                contentType(ContentType.Application.Json)
                setBody(request)
                requestOptions(requestOptions)
            }.body()
        }
        is VoiceCreateRequest.Sample -> withUploadSource(request.audioSample) { sample ->
            val mediaType = audioContentType(sample, request.mediaType)
            requester.perform {
                it.submitFormWithBinaryData(url = "audio/voices", formData = formData {
                    append("type", "audio_sample")
                    append("name", request.name)
                    append("consent", request.consent)
                    appendFileSource("audio_sample", sample, mediaType)
                }) { requestOptions(requestOptions); attributes.put(SingleUseRequest, Unit)
                retry { maxRetries = 0 } }.body()
            }
        }
    }

    override suspend fun deleteVoice(id: String, requestOptions: RequestOptions?): Boolean =
        deleteResource(resourcePath("audio/voices", id), requestOptions)

    override suspend fun createVoiceConsent(request: VoiceConsentRequest, requestOptions: RequestOptions?): VoiceConsent = withUploadSource(request.recording) { recording ->
        val mediaType = audioContentType(recording, request.mediaType)
        requester.perform {
            it.submitFormWithBinaryData(url = "audio/voice_consents", formData = formData {
                append("name", request.name)
                append("language", request.language)
                appendFileSource("recording", recording, mediaType)
            }) { requestOptions(requestOptions); attributes.put(SingleUseRequest, Unit)
                retry { maxRetries = 0 } }.body()
        }
    }

    override suspend fun voiceConsent(id: String, requestOptions: RequestOptions?): VoiceConsent? {
        val path = resourcePath("audio/voice_consents", id)
        return try {
            requester.perform<VoiceConsent> {
                it.get {
                    url(path = path)
                    requestOptions(requestOptions)
                }
            }
        } catch (error: OpenAIAPIException) {
            if (error.statusCode == HttpStatusCode.NotFound.value) null else throw error
        }
    }

    override suspend fun updateVoiceConsent(id: String, request: VoiceConsentUpdateRequest, requestOptions: RequestOptions?): VoiceConsent {
        val path = resourcePath("audio/voice_consents", id)
        return requester.perform {
            it.post {
                url(path = path)
                contentType(ContentType.Application.Json)
                setBody(request)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun voiceConsents(after: String?, limit: Int?, requestOptions: RequestOptions?): VoiceConsentPage =
        requester.perform {
            it.get {
                url(path = "audio/voice_consents") {
                    after?.let { parameter("after", it) }
                    limit?.let { parameter("limit", it) }
                }
                requestOptions(requestOptions)
            }.body()
        }

    override suspend fun deleteVoiceConsent(id: String, requestOptions: RequestOptions?): Boolean =
        deleteResource(resourcePath("audio/voice_consents", id), requestOptions)

    private suspend fun deleteResource(path: String, options: RequestOptions?): Boolean = try {
        requester.perform<DeleteResponse> {
            it.delete {
                url(path = path)
                requestOptions(options)
            }
        }.deleted
    } catch (error: OpenAIAPIException) {
        if (error.statusCode == HttpStatusCode.NotFound.value) false else throw error
    }

    private fun resourcePath(base: String, id: String): String {
        require(id.isNotEmpty() && id.none { it == '/' || it == '?' || it == '#' || it == '%' }) { "Invalid voice resource ID" }
        return "$base/$id"
    }

    private suspend fun <T> withUploadSource(file: FileSource, block: suspend (FileSource) -> T): T =
        file.source.buffered().use { source ->
            // The multipart helper borrows the stream; this outer scope closes the original exactly once.
            val borrowed = object : RawSource by source { override fun close() {} }
            block(FileSource(file.name, borrowed))
        }

    private fun audioContentType(file: FileSource, explicit: String?): ContentType {
        val mediaType = explicit ?: when (file.name.substringAfterLast('.', "").lowercase()) {
            "mp3", "mpeg", "mpga" -> "audio/mpeg"
            "wav" -> "audio/wav"
            "ogg", "oga" -> "audio/ogg"
            "aac" -> "audio/aac"
            "flac" -> "audio/flac"
            "webm" -> "audio/webm"
            "mp4", "m4a" -> "audio/mp4"
            else -> throw IllegalArgumentException("Provide a supported audio mediaType for ${file.name}")
        }
        val type = ContentType.parse(mediaType).withoutParameters()
        require(type.toString().lowercase() in setOf("audio/mpeg", "audio/wav", "audio/x-wav", "audio/ogg", "audio/aac", "audio/flac", "audio/webm", "audio/mp4")) {
            "Unsupported voice audio mediaType: $type"
        }
        return type
    }
}
