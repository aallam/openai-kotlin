package com.aallam.openai.client.internal

import com.aallam.openai.api.ExperimentalOpenAI
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
import com.aallam.openai.api.file.FileId
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
import okio.FileSystem

/**
 * Implementation of [OpenAI].
 *
 * @param config client configuration
 * @param fileSystem access to read and write files
 */
internal class OpenAIApi(
    config: OpenAIConfig,
    private val fileSystem: FileSystem
) : OpenAI {

    private val httpClient: HttpClient = createHttpClient(config)

    override suspend fun search(
        engineId: EngineId,
        request: SearchRequest
    ): List<SearchResult> {
        return httpClient.post<SearchResponse>(
            path = "$EnginesPath/$engineId/search",
            body = request
        ) {
            contentType(ContentType.Application.Json)
        }.data
    }

    override suspend fun engines(): List<Engine> {
        return httpClient.get<EnginesResponse>(path = EnginesPath).data
    }

    override suspend fun engine(engineId: EngineId): Engine {
        return httpClient.get(path = "$EnginesPath/$engineId")
    }

    override suspend fun completion(engineId: EngineId, request: CompletionRequest?): TextCompletion {
        return httpClient.post(path = "$EnginesPath/$engineId/completions", body = request ?: EmptyContent) {
            contentType(ContentType.Application.Json)
        }
    }

    override fun completions(engineId: EngineId, request: CompletionRequest?): Flow<TextCompletion> {
        return flow {
            httpClient.post<HttpStatement>(
                path = "$EnginesPath/$engineId/completions",
                body = request.toStreamRequest()
            ) {
                contentType(ContentType.Application.Json)
            }.execute { response ->
                val readChannel = response.receive<ByteReadChannel>()
                while (!readChannel.isClosedForRead) {
                    val line = readChannel.readUTF8Line() ?: ""
                    val value: TextCompletion = when {
                        line.startsWith(StreamEndToken) -> break
                        line.startsWith(StreamPrefix) -> JsonLenient.decodeFromString(line.removePrefix(StreamPrefix))
                        else -> continue
                    }
                    emit(value)
                }
            }
        }
    }

    @ExperimentalOpenAI
    override suspend fun classifications(request: ClassificationRequest): Classification {
        return httpClient.post(path = ClassificationsPath, body = request) {
            contentType(ContentType.Application.Json)
        }
    }

    @ExperimentalOpenAI
    override suspend fun answers(request: AnswerRequest): Answer {
        return httpClient.post(path = AnswersPath, body = request) {
            contentType(ContentType.Application.Json)
        }
    }


    override suspend fun file(request: FileRequest): File {
        val data: List<PartData> = formData {
            appendFile(fileSystem, "file", request.file)
            append("purpose", request.purpose.raw)
        }
        return httpClient.submitFormWithBinaryData(url = FilesPath, formData = data)
    }

    override suspend fun files(): List<File> {
        return httpClient.get<FileResponse>(path = FilesPath).data
    }

    override suspend fun file(fileId: FileId): File? {
        return try {
            httpClient.get(path = "$FilesPath/$fileId")
        } catch (exception: ClientRequestException) {
            if (exception.response.status == HttpStatusCode.NotFound) return null
            throw exception
        }
    }

    override suspend fun delete(fileId: FileId) {
        return httpClient.delete(path = "$FilesPath/$fileId")
    }

    companion object {
        // API Paths
        private const val EnginesPath = "/v1/engines"
        private const val ClassificationsPath = "/v1/classifications"
        private const val AnswersPath = "/v1/answers"
        private const val FilesPath = "/v1/files"

        // Stream Tokens
        private const val StreamPrefix = "data:"
        private const val StreamEndToken = "$StreamPrefix [DONE]"
    }
}
