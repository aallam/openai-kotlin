package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.upload.*
import com.aallam.openai.client.Uploads
import com.aallam.openai.client.internal.extension.appendFileSource
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

internal class UploadsApi(private val requester: HttpRequester) : Uploads {
    override suspend fun upload(request: UploadRequest, requestOptions: RequestOptions?): Upload =
        requester.perform {
            it.post {
                url(path = ApiPath.Uploads)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }

    override suspend fun uploadPart(id: UploadId, data: FileSource, requestOptions: RequestOptions?): UploadPart =
        requester.perform {
            it.submitFormWithBinaryData(
                url = "${ApiPath.Uploads}/${id.id}/parts",
                formData = formData { appendFileSource("data", data) },
            ) {
                requestOptions(requestOptions)
            }
        }

    override suspend fun completeUpload(
        id: UploadId,
        request: UploadCompletionRequest,
        requestOptions: RequestOptions?
    ): Upload = requester.perform {
        it.post {
            url(path = "${ApiPath.Uploads}/${id.id}/complete")
            setBody(request)
            contentType(ContentType.Application.Json)
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun cancelUpload(id: UploadId, requestOptions: RequestOptions?): Upload = requester.perform {
        it.post {
            url(path = "${ApiPath.Uploads}/${id.id}/cancel")
            requestOptions(requestOptions)
        }.body()
    }
}
