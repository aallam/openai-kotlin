package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.DeleteResponse
import com.aallam.openai.api.file.File
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileRequest
import com.aallam.openai.api.file.FileResponse
import com.aallam.openai.client.Files
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
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
import okio.FileSystem
import okio.Path.Companion.toPath

/**
 * Implementation of [Files].
 */
internal class FilesApi(
    private val requester: HttpRequester, private val fileSystem: FileSystem
) : Files {

    override suspend fun file(request: FileRequest): File {
        val data: List<PartData> = formData {
            appendFile(fileSystem, "file", request.file)
            append(key = "purpose", value = request.purpose.raw)
        }
        return requester.perform {
            it.submitFormWithBinaryData(url = FilesPath, formData = data)
        }
    }

    override suspend fun files(): List<File> {
        return requester.perform<FileResponse> { it.get { url(path = FilesPath) } }.data
    }

    override suspend fun file(fileId: FileId): File? {
        val response = requester.perform<HttpResponse> {
            it.get { url(path = "$FilesPath/${fileId.id}") }
        }
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    override suspend fun delete(fileId: FileId): Boolean {
        val response = requester.perform<HttpResponse> {
            it.delete {
                url(path = "$FilesPath/${fileId.id}")
            }
        }

        return when (response.status) {
            HttpStatusCode.NotFound -> false
            else -> response.body<DeleteResponse>().deleted
        }
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
