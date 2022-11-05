package com.aallam.openai.client.internal.api

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.core.ListResponse
import com.aallam.openai.api.image.*
import com.aallam.openai.client.Images
import com.aallam.openai.client.internal.extension.appendBinaryFile
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import okio.FileSystem

internal class ImagesApi(
    private val requester: HttpRequester,
    private val fileSystem: FileSystem,
) : Images {

    @ExperimentalOpenAI
    override suspend fun images(request: ImageRequestURL): List<ImageURL> {
        return requester.perform<ListResponse<ImageURL>> {
            it.post {
                url(path = ImagesGenerationV1)
                setBody(request)
                contentType(ContentType.Application.Json)
            }
        }.data
    }

    @ExperimentalOpenAI
    override suspend fun images(request: ImageRequestJSON): List<ImageJSON> {
        return requester.perform<ListResponse<ImageJSON>> {
            it.post {
                url(path = ImagesGenerationV1)
                setBody(request)
                contentType(ContentType.Application.Json)
            }
        }.data
    }

    @ExperimentalOpenAI
    override suspend fun image(edit: ImageEditURL): List<ImageURL> {
        return requester.perform<ListResponse<ImageURL>> {
            it.submitFormWithBinaryData(url = ImagesEditsV1, formData = imageEditRequest(edit, ResponseFormat.url))
        }.data
    }

    @ExperimentalOpenAI
    override suspend fun image(edit: ImageEditJSON): List<ImageJSON> {
        return requester.perform<ListResponse<ImageJSON>> {
            it.submitFormWithBinaryData(
                url = ImagesEditsV1,
                formData = imageEditRequest(edit, ResponseFormat.base64Json)
            )
        }.data
    }

    /**
     * Build image edit request.
     */
    private fun imageEditRequest(edit: ImageEdit, responseFormat: ResponseFormat) = formData {
        appendBinaryFile(fileSystem, "image", edit.image)
        appendBinaryFile(fileSystem, "mask", edit.mask)
        append(key = "prompt", value = edit.prompt)
        append(key = "response_format", value = responseFormat.format)
        edit.n?.let { n -> append(key = "n", value = n) }
        edit.size?.let { dim -> append(key = "size", value = dim.size) }
        edit.user?.let { user -> append(key = "user", value = user) }
    }

    companion object {
        private const val ImagesGenerationV1 = "v1/images/generations"
        private const val ImagesEditsV1 = "v1/images/edits"
    }
}
