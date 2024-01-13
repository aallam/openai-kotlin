package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.ListResponse
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.Models
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Implementation of [Models] API.
 */
internal class ModelsApi(private val requester: HttpRequester) : Models {

    override suspend fun models(requestOptions: RequestOptions?): List<Model> {
        return requester.perform<ListResponse<Model>> {
            it.get {
                url(path = ApiPath.Models)
                requestOptions(requestOptions)
            }
        }.data
    }

    override suspend fun model(modelId: ModelId, requestOptions: RequestOptions?): Model {
        return requester.perform {
            it.get {
                url(path = "${ApiPath.Models}/${modelId.id}")
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }
        }
    }
}
