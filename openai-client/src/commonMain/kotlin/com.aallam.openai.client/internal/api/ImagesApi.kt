package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.ListResponse
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.image.*
import com.aallam.openai.api.image.internal.ImageCreationRequest
import com.aallam.openai.api.image.internal.ImageResponseFormat
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.Images
import com.aallam.openai.client.internal.extension.appendFileSource
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

internal class ImagesApi(private val requester: HttpRequester) : Images {
    private val defaultEditModel = ModelId("dall-e-2")

    override suspend fun imageURL(creation: ImageCreation, requestOptions: RequestOptions?): List<ImageURL> {
        return requester.perform<ListResponse<ImageURL>> {
            it.post {
                url(path = ApiPath.ImagesGeneration)
                setBody(creation.toURLRequest())
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }
        }.data
    }

    override suspend fun imageJSON(creation: ImageCreation, requestOptions: RequestOptions?): List<ImageJSON> {
        return requester.perform<ListResponse<ImageJSON>> {
            it.post {
                url(path = ApiPath.ImagesGeneration)
                setBody(creation.toJSONRequest())
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }
        }.data
    }

    override suspend fun imageURL(edit: ImageEdit, requestOptions: RequestOptions?): List<ImageURL> {
        return requester.perform<ListResponse<ImageURL>> {
            it.submitFormWithBinaryData(
                url = ApiPath.ImagesEdits,
                formData = imageEditRequest(edit, ImageResponseFormat.url),
            ) {
                requestOptions(requestOptions)
            }
        }.data
    }

    override suspend fun imageJSON(edit: ImageEdit, requestOptions: RequestOptions?): List<ImageJSON> {
        return requester.perform<ListResponse<ImageJSON>> {
            it.submitFormWithBinaryData(
                url = ApiPath.ImagesEdits,
                formData = imageEditRequest(edit, ImageResponseFormat.base64Json),
            ) {
                requestOptions(requestOptions)
            }
        }.data
    }

    /**
     * Build image edit request.
     */
    private fun imageEditRequest(edit: ImageEdit, responseFormat: ImageResponseFormat) = formData {
        appendFileSource("image", edit.image, ContentType.Image.PNG)
        appendFileSource("mask", edit.mask, ContentType.Image.PNG)
        append(key = "prompt", value = edit.prompt)
        append(key = "response_format", value = responseFormat.format)
        edit.n?.let { n -> append(key = "n", value = n) }
        edit.size?.let { dim -> append(key = "size", value = dim.size) }
        edit.user?.let { user -> append(key = "user", value = user) }
        append(key = "model", value = (edit.model ?: defaultEditModel).id)
    }

    override suspend fun imageURL(variation: ImageVariation, requestOptions: RequestOptions?): List<ImageURL> {
        return requester.perform<ListResponse<ImageURL>> {
            it.submitFormWithBinaryData(
                url = ApiPath.ImagesVariants,
                formData = imageVariantRequest(variation, ImageResponseFormat.url),
            ) {
                requestOptions(requestOptions)
            }
        }.data
    }

    override suspend fun imageJSON(variation: ImageVariation, requestOptions: RequestOptions?): List<ImageJSON> {
        return requester.perform<ListResponse<ImageJSON>> {
            it.submitFormWithBinaryData(
                url = ApiPath.ImagesVariants,
                formData = imageVariantRequest(variation, ImageResponseFormat.base64Json),
            ) {
                requestOptions(requestOptions)
            }
        }.data
    }

    /**
     * Build image variant request.
     */
    private fun imageVariantRequest(edit: ImageVariation, responseFormat: ImageResponseFormat) = formData {
        appendFileSource("image", edit.image, ContentType.Image.PNG)
        append(key = "response_format", value = responseFormat.format)
        edit.n?.let { n -> append(key = "n", value = n) }
        edit.size?.let { dim -> append(key = "size", value = dim.size) }
        edit.user?.let { user -> append(key = "user", value = user) }
        edit.model?.let { model -> append(key = "model", value = model.id) }
    }

    /** Convert [ImageCreation] instance to base64 JSON request */
    private fun ImageCreation.toJSONRequest() = ImageCreationRequest(
        prompt = prompt,
        n = n,
        size = size,
        user = user,
        responseFormat = ImageResponseFormat.base64Json,
        model = model?.id,
        quality = quality,
        style = style,
    )

    /** Convert [ImageCreation] instance to URL request */
    private fun ImageCreation.toURLRequest() = ImageCreationRequest(
        prompt = prompt,
        n = n,
        size = size,
        user = user,
        responseFormat = ImageResponseFormat.url,
        model = model?.id,
        quality = quality,
        style = style,
    )
}
