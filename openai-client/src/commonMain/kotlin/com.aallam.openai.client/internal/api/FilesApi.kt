package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.DeleteResponse
import com.aallam.openai.api.core.ListResponse
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.file.File
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.client.Files
import com.aallam.openai.client.internal.extension.appendFileSource
import com.aallam.openai.client.internal.extension.requestOptions
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

    override suspend fun file(request: FileUpload, requestOptions: RequestOptions?): File {
        return requester.perform {
            it.submitFormWithBinaryData(url = ApiPath.Files, formData = formData {
                appendFileSource("file", request.file)
                append(key = "purpose", value = request.purpose.raw)
                request.expiresAfter?.let {
                    append("expires_after[anchor]", it.anchor)
                    append("expires_after[seconds]", it.seconds)
                }
            }) {
                requestOptions(requestOptions)
            }
        }
    }

    override suspend fun files(requestOptions: RequestOptions?): List<File> {
        return requester.perform<ListResponse<File>> {
            it.get {
                url(path = ApiPath.Files)
                requestOptions(requestOptions)
            }
        }.data
    }

    override suspend fun filesPage(
        after: FileId?, limit: Int?, order: SortOrder?, purpose: Purpose?, requestOptions: RequestOptions?
    ): PaginatedList<File> = requester.perform {
        it.get {
            url(path = ApiPath.Files) {
                after?.let { parameter("after", it.id) }
                limit?.let { parameter("limit", it) }
                order?.let { parameter("order", it.order) }
                purpose?.let { parameter("purpose", it.raw) }
            }
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun file(fileId: FileId, requestOptions: RequestOptions?): File? {
        try {
            return requester.perform<HttpResponse> {
                it.get {
                    url(path = "${ApiPath.Files}/${fileId.id}")
                    requestOptions(requestOptions)
                }
            }.body()
        } catch (e: OpenAIAPIException) {
            if (e.statusCode == HttpStatusCode.NotFound.value) return null
            throw e
        }
    }

    override suspend fun delete(fileId: FileId, requestOptions: RequestOptions?): Boolean {
        val response = requester.perform<HttpResponse> {
            it.delete {
                url(path = "${ApiPath.Files}/${fileId.id}")
                requestOptions(requestOptions)
            }
        }

        return when (response.status) {
            HttpStatusCode.NotFound -> false
            else -> response.body<DeleteResponse>().deleted
        }
    }

    override suspend fun download(fileId: FileId, requestOptions: RequestOptions?): ByteArray {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Files}/${fileId.id}/content")
                requestOptions(requestOptions)
            }
        }
    }

}
