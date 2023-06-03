package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.ListResponse
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.Models
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Implementation of [Models] API.
 */
internal class ModelsApi(private val requester: HttpRequester) : Models {

    override suspend fun models(): List<Model> {
        return requester.perform<ListResponse<Model>> {
            it.get { url(path = ApiPath.Models) }
        }.data
    }

    override suspend fun model(modelId: ModelId): Model {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Models}/${modelId.id}")
                contentType(ContentType.Application.Json)
            }
        }
    }
}
