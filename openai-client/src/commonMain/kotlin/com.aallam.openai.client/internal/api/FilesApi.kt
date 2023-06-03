package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.DeleteResponse
import com.aallam.openai.api.core.ListResponse
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.file.File
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.client.Files
import com.aallam.openai.client.internal.extension.appendFileSource
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * Implementation of [Files].
 */
internal class FilesApi(private val requester: HttpRequester) : Files {

    override suspend fun file(request: FileUpload): File {
        return requester.perform {
            it.submitFormWithBinaryData(url = ApiPath.Files, formData = formData {
                appendFileSource("file", request.file)
                append(key = "purpose", value = request.purpose.raw)
            })
        }
    }

    override suspend fun files(): List<File> {
        return requester.perform<ListResponse<File>> { it.get { url(path = ApiPath.Files) } }.data
    }

    override suspend fun file(fileId: FileId): File? {
        try {
            return requester.perform<HttpResponse> {
                it.get { url(path = "${ApiPath.Files}/${fileId.id}") }
            }.body()
        } catch (e: OpenAIAPIException) {
            if (e.statusCode == HttpStatusCode.NotFound.value) return null
            throw e
        }
    }

    override suspend fun delete(fileId: FileId): Boolean {
        val response = requester.perform<HttpResponse> {
            it.delete {
                url(path = "${ApiPath.Files}/${fileId.id}")
            }
        }

        return when (response.status) {
            HttpStatusCode.NotFound -> false
            else -> response.body<DeleteResponse>().deleted
        }
    }

    override suspend fun download(fileId: FileId): ByteArray {
        return requester.perform {
            it.get { url(path = "${ApiPath.Files}/${fileId.id}/content") }
        }
    }

}
