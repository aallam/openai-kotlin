package com.aallam.openai.client.internal.api

import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.model.ModelsResponse
import com.aallam.openai.client.Models
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Implementation of [Models] API.
 */
internal class ModelsApi(private val requester: HttpRequester) : Models {

    override suspend fun models(): List<Model> {
        return requester.perform<ModelsResponse> {
            it.get { url(path = ModelsPathV1) }
        }.data
    }

    override suspend fun model(modelId: ModelId): Model {
        return requester.perform {
            it.get {
                url(path = "$ModelsPathV1/${modelId.id}")
                contentType(ContentType.Application.Json)
            }
        }
    }

    companion object {
        internal const val ModelsPathV1 = "v1/models"
    }
}
