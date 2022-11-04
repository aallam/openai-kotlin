package com.aallam.openai.client.internal.api

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.core.ListResponse
import com.aallam.openai.api.image.ImageRequest
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.client.Images
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

internal class ImagesApi(private val requester: HttpRequester) : Images {

    @ExperimentalOpenAI
    override suspend fun images(request: ImageRequest): List<ImageURL> {
        return requester.perform<ListResponse<ImageURL>> {
            it.post {
                url(path = ImagesGenerationV1)
                setBody(request)
                contentType(ContentType.Application.Json)
            }
        }.data
    }

    companion object {
        private const val ImagesGenerationV1 = "v1/images/generations"
    }
}
