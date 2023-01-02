package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.DeleteResponse
import com.aallam.openai.api.core.ListResponse
import com.aallam.openai.api.file.File
import com.aallam.openai.api.file.FileCreate
import com.aallam.openai.api.file.FileId
import com.aallam.openai.client.Files
import com.aallam.openai.client.internal.extension.appendTextFile
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode

/**
 * Implementation of [Files].
 */
internal class FilesApi(private val requester: HttpRequester) : Files {

    override suspend fun file(request: FileCreate): File {
        return requester.perform {
            it.submitFormWithBinaryData(url = FilesPath, formData = formData {
                appendTextFile("file", request.file)
                append(key = "purpose", value = request.purpose.raw)
            })
        }
    }

    override suspend fun files(): List<File> {
        return requester.perform<ListResponse<File>> { it.get { url(path = FilesPath) } }.data
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

    companion object {
        private const val FilesPath = "/v1/files"
    }
}
