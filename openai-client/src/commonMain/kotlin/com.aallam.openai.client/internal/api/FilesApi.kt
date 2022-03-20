package com.aallam.openai.client.internal.api

import com.aallam.openai.api.file.File
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileRequest
import com.aallam.openai.api.file.FileResponse
import com.aallam.openai.client.Files
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.append
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.contentType
import io.ktor.util.InternalAPI
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * Implementation of [Files].
 */
internal class FilesApi(
    private val httpRequester: HttpTransport,
    private val fileSystem: FileSystem
) : Files {

    @OptIn(InternalAPI::class)
    override suspend fun file(request: FileRequest): File {
        val data: List<PartData> = formData {
            appendFile(fileSystem, "file", request.file)
            append("purpose", request.purpose.raw)
        }
        return httpRequester.perform {
            it.submitFormWithBinaryData(url = FilesPath, formData = data) {
                contentType(ContentType.MultiPart.FormData)
            }
        }
    }

    override suspend fun files(): List<File> {
        return httpRequester.perform<FileResponse> { it.get { url(path = FilesPath) } }.data
    }

    override suspend fun file(fileId: FileId): File? {
        val response = httpRequester.perform<HttpResponse> {
            it.get { url(path = "$FilesPath/$fileId") }
        }
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    override suspend fun delete(fileId: FileId) {
        httpRequester.perform<HttpResponse> { it.delete { url(path = "$FilesPath/$fileId") } }
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
