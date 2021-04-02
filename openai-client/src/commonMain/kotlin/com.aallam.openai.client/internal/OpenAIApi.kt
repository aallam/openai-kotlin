package com.aallam.openai.client.internal

import com.aallam.openai.api.answer.Answer
import com.aallam.openai.api.answer.AnswerRequest
import com.aallam.openai.api.classification.Classification
import com.aallam.openai.api.classification.ClassificationRequest
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.engine.Engine
import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.api.engine.EnginesResponse
import com.aallam.openai.api.file.File
import com.aallam.openai.api.file.FileRequest
import com.aallam.openai.api.file.FileResponse
import com.aallam.openai.api.search.SearchRequest
import com.aallam.openai.api.search.SearchResponse
import com.aallam.openai.api.search.SearchResult
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.internal.extension.toStreamRequest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString

internal class OpenAIApi(config: OpenAIConfig) : OpenAI {

    private val httpClient: HttpClient = createHttpClient(config)

    override suspend fun search(
        engineId: EngineId,
        request: SearchRequest
    ): List<SearchResult> {
        return httpClient.post<SearchResponse>(
            path = "/v1/engines/$engineId/search",
            body = request
        ) {
            contentType(ContentType.Application.Json)
        }.data
    }

    override suspend fun engines(): List<Engine> {
        return httpClient.get<EnginesResponse>(path = "/v1/engines").data
    }

    override suspend fun engine(engineId: EngineId): Engine {
        return httpClient.get(path = "/v1/engines/$engineId")
    }

    override suspend fun completion(engineId: EngineId, request: CompletionRequest?): TextCompletion {
        return httpClient.post(path = "/v1/engines/$engineId/completions", body = request ?: EmptyContent) {
            contentType(ContentType.Application.Json)
        }
    }

    override fun completions(engineId: EngineId, request: CompletionRequest?): Flow<TextCompletion> {
        return flow {
            httpClient.post<HttpStatement>(
                path = "/v1/engines/$engineId/completions",
                body = request.toStreamRequest()
            ) {
                contentType(ContentType.Application.Json)
            }.execute { response ->
                val readChannel = response.receive<ByteReadChannel>()
                while (!readChannel.isClosedForRead) {
                    val line = readChannel.readUTF8Line() ?: ""
                    val value: TextCompletion = when {
                        line.startsWith(STREAM_END_TOKEN) -> break
                        line.startsWith(STREAM_PREFIX) -> JsonLenient.decodeFromString(line.removePrefix(STREAM_PREFIX))
                        else -> continue
                    }
                    emit(value)
                }
            }
        }
    }

    override suspend fun classifications(request: ClassificationRequest): Classification {
        return httpClient.post(path = "/v1/classifications", body = request) {
            contentType(ContentType.Application.Json)
        }
    }

    override suspend fun answers(request: AnswerRequest): Answer {
        return httpClient.post(path = "/v1/answers", body = request) {
            contentType(ContentType.Application.Json)
        }
    }


    override suspend fun file(request: FileRequest): File {
        val data: List<PartData> = formData {
            append("file", request.filename, ContentType.Application.OctetStream) {
                append(request.content)
            }
            append("purpose", request.purpose.raw)
        }
        return httpClient.submitFormWithBinaryData(url = "/v1/files", formData = data)
    }

    override suspend fun files(): List<File> {
        return httpClient.get<FileResponse>(path = "/v1/files").data
    }

    override suspend fun file(id: String): File? {
        return try {
            httpClient.get(path = "/v1/files/$id")
        } catch (exception: ClientRequestException) {
            if (exception.response.status == HttpStatusCode.NotFound) return null
            throw exception
        }
    }

    companion object {
        private const val STREAM_PREFIX = "data:"
        private const val STREAM_END_TOKEN = "$STREAM_PREFIX [DONE]"
    }
}
