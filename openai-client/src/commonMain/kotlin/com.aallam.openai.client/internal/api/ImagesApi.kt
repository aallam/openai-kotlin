package com.aallam.openai.client.internal.api

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.core.ListResponse
import com.aallam.openai.api.image.*
import com.aallam.openai.client.Images
import com.aallam.openai.client.internal.extension.appendBinaryFile
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import com.aallam.openai.client.internal.model.ImageResponseFormat
import com.aallam.openai.client.internal.model.toJSONRequest
import com.aallam.openai.client.internal.model.toURLRequest
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class ImagesApi(private val requester: HttpRequester) : Images {

    @ExperimentalOpenAI
    override suspend fun imageURL(creation: ImageCreation): List<ImageURL> {
        return requester.perform<ListResponse<ImageURL>> {
            it.post {
                url(path = ImagesGenerationV1)
                setBody(creation.toURLRequest())
                contentType(ContentType.Application.Json)
            }
        }.data
    }

    @ExperimentalOpenAI
    override suspend fun imageJSON(creation: ImageCreation): List<ImageJSON> {
        return requester.perform<ListResponse<ImageJSON>> {
            it.post {
                url(path = ImagesGenerationV1)
                setBody(creation.toJSONRequest())
                contentType(ContentType.Application.Json)
            }
        }.data
    }

    @ExperimentalOpenAI
    override suspend fun imageURL(edit: ImageEdit): List<ImageURL> {
        return requester.perform<ListResponse<ImageURL>> {
            it.submitFormWithBinaryData(url = ImagesEditsV1, formData = imageEditRequest(edit, ImageResponseFormat.url))
        }.data
    }

    @ExperimentalOpenAI
    override suspend fun imageJSON(edit: ImageEdit): List<ImageJSON> {
        return requester.perform<ListResponse<ImageJSON>> {
            it.submitFormWithBinaryData(
                url = ImagesEditsV1, formData = imageEditRequest(edit, ImageResponseFormat.base64Json)
            )
        }.data
    }

    /**
     * Build image edit request.
     */
    @ExperimentalOpenAI
    private fun imageEditRequest(edit: ImageEdit, responseFormat: ImageResponseFormat) = formData {
        appendBinaryFile("image", edit.imageFilename, edit.imageSource)
        appendBinaryFile("mask", edit.maskFilename, edit.maskSource)
        append(key = "prompt", value = edit.prompt)
        append(key = "response_format", value = responseFormat.format)
        edit.n?.let { n -> append(key = "n", value = n) }
        edit.size?.let { dim -> append(key = "size", value = dim.size) }
        edit.user?.let { user -> append(key = "user", value = user) }
    }

    @ExperimentalOpenAI
    override suspend fun imageURL(variation: ImageVariation): List<ImageURL> {
        return requester.perform<ListResponse<ImageURL>> {
            it.submitFormWithBinaryData(
                url = ImagesVariantsV1, formData = imageVariantRequest(variation, ImageResponseFormat.url)
            )
        }.data
    }

    @ExperimentalOpenAI
    override suspend fun imageJSON(variation: ImageVariation): List<ImageJSON> {
        return requester.perform<ListResponse<ImageJSON>> {
            it.submitFormWithBinaryData(
                url = ImagesVariantsV1, formData = imageVariantRequest(variation, ImageResponseFormat.base64Json)
            )
        }.data
    }

    /**
     * Build image variant request.
     */
    @ExperimentalOpenAI
    private fun imageVariantRequest(edit: ImageVariation, responseFormat: ImageResponseFormat) = formData {
        appendBinaryFile("image", edit.filename, edit.source)
        append(key = "response_format", value = responseFormat.format)
        edit.n?.let { n -> append(key = "n", value = n) }
        edit.size?.let { dim -> append(key = "size", value = dim.size) }
        edit.user?.let { user -> append(key = "user", value = user) }

    }

    companion object {
        private const val ImagesGenerationV1 = "v1/images/generations"
        private const val ImagesEditsV1 = "v1/images/edits"
        private const val ImagesVariantsV1 = "v1/images/variations"
    }
}
