package com.aallam.openai.client.internal.api

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.classification.Classification
import com.aallam.openai.api.classification.ClassificationRequest
import com.aallam.openai.client.Classifications
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Implementation of [Classifications].
 */
internal class ClassificationsApi(private val httpClient: HttpClient) : Classifications {

    @ExperimentalOpenAI
    override suspend fun classifications(request: ClassificationRequest): Classification {
        return httpClient.post(path = ClassificationsPath, body = request) {
            contentType(ContentType.Application.Json)
        }
    }

    companion object {
        private const val ClassificationsPath = "/v1/classifications"
    }
}
