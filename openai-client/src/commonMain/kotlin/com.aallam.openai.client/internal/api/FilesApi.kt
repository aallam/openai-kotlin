package com.aallam.openai.client.internal.api

import com.aallam.openai.api.file.File
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileRequest
import com.aallam.openai.api.file.FileResponse
import com.aallam.openai.client.Files
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * Implementation of [Files].
 */
internal class FilesApi(
    private val httpClient: HttpClient,
    private val fileSystem: FileSystem
) : Files {

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

    /**
     * Appends file content to the given FormBuilder.
     */
    private fun FormBuilder.appendFile(fileSystem: FileSystem, key: String, filePath: String) {
        val path = filePath.toPath()
        append(key, path.name, ContentType.Application.OctetStream) {
            fileSystem.read(path) {
                while (true) {
                    val line = readUtf8Line() ?: break
                    appendLine(line)
                }
            }
        }
    }

    companion object {
        private const val FilesPath = "/v1/files"
    }
}
