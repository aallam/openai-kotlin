package com.aallam.openai.client.internal.api

import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.model.ModelsResponse
import com.aallam.openai.client.Models
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Implementation of [Models] API.
 */
internal class ModelsApi(private val httpTransport: HttpTransport) : Models {

    override suspend fun models(): List<Model> {
        return httpTransport.perform<ModelsResponse> {
            it.get { url(path = ModelsPathV1) }
        }.data
    }

    override suspend fun model(modelId: ModelId): Model {
        return httpTransport.perform {
            it.get {
                url(path = "$ModelsPathV1/$modelId")
                contentType(ContentType.Application.Json)
            }
        }
    }

    companion object {
        private const val ModelsPathV1 = "v1/models"
    }
}
