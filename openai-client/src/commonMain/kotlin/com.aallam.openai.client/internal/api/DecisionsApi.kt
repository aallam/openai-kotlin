package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.decision.Decision
import com.aallam.openai.api.decision.DecisionRequest
import com.aallam.openai.client.Decisions
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class DecisionsApi(private val requester: HttpRequester) : Decisions {
    override suspend fun decision(request: DecisionRequest, requestOptions: RequestOptions?): Decision =
        requester.perform {
            it.post {
                url(path = ApiPath.Decisions)
                contentType(ContentType.Application.Json)
                setBody(request)
                requestOptions(requestOptions)
            }.body()
        }
}
