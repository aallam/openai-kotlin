package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.safety.*
import com.aallam.openai.client.Safety
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*

internal class SafetyApi(private val requester: HttpRequester) : Safety {
    override suspend fun safetyAlert(id: String, requestOptions: RequestOptions?): SafetyAlert = get("alerts", id, requestOptions)
    override suspend fun safetyCase(id: String, requestOptions: RequestOptions?): SafetyCase = get("cases", id, requestOptions)

    private suspend inline fun <reified T : Any> get(resource: String, id: String, options: RequestOptions?): T {
        require(id.isNotBlank() && id != "." && id != ".." && id.none { it in "/\\?#%" || it.isISOControl() }) { "Invalid safety resource ID" }
        return requester.perform {
            it.get {
                url(path = "safety/$resource/$id")
                requestOptions(options)
            }.body()
        }
    }
}
