package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.ListResponse
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.image.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.image.internal.ImageCreationRequest
import com.aallam.openai.api.image.internal.ImageResponseFormat
import com.aallam.openai.client.Images
import com.aallam.openai.client.internal.extension.appendFileSource
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.extension.streamEventsFrom
import com.aallam.openai.client.internal.extension.streamRequestOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

internal class ImagesApi(private val requester: HttpRequester) : Images {
    private val defaultEditModel = ModelId("dall-e-2")

    override suspend fun image(creation: ImageGenerationRequest, requestOptions: RequestOptions?): ImageResponse {
        return createImage(ApiPath.ImagesGeneration, creation, requestOptions)
    }

    override suspend fun image(edit: ImageEditRequest, requestOptions: RequestOptions?): ImageResponse =
        createImage(ApiPath.ImagesEdits, edit, requestOptions)

    override fun imageEvents(creation: ImageGenerationRequest, requestOptions: RequestOptions?): Flow<ImageEvent> = channelFlow {
        streamImage(ApiPath.ImagesGeneration, streamRequestOf(creation), requestOptions)
    }

    override fun imageEvents(edit: ImageEditRequest, requestOptions: RequestOptions?): Flow<ImageEvent> = channelFlow {
        streamImage(ApiPath.ImagesEdits, streamRequestOf(edit), requestOptions)
    }

    private suspend inline fun <reified T : Any> createImage(path: String, request: T, options: RequestOptions?): ImageResponse =
        requester.perform {
            it.post {
                url(path = path)
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(options)
            }
        }

    private suspend fun ProducerScope<ImageEvent>.streamImage(
        path: String,
        request: kotlinx.serialization.json.JsonElement,
        options: RequestOptions?,
    ) {
        val builder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(path = path)
            setBody(request)
            contentType(ContentType.Application.Json)
            accept(ContentType.Text.EventStream)
            requestOptions(options)
        }
        requester.perform<Unit>(builder) { response ->
            flow<ImageEvent> { this.streamEventsFrom(response) }.collect { send(it) }
        }
    }

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
                if (creation.model.isGptImage()) {
                    setBody(ImageGenerationRequest(
                        prompt = creation.prompt,
                        model = requireNotNull(creation.model),
                        n = creation.n,
                        size = creation.size,
                        user = creation.user,
                        quality = creation.quality,
                        style = creation.style,
                    ))
                } else {
                    setBody(creation.toJSONRequest())
                }
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
        if (responseFormat == ImageResponseFormat.url || !edit.model.isGptImage()) {
            append(key = "response_format", value = responseFormat.format)
        }
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

private fun ModelId?.isGptImage(): Boolean = this?.id?.let { it.startsWith("gpt-image-") || it == "chatgpt-image-latest" } == true
