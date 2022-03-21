package com.aallam.openai.client.internal.api

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.classification.Classification
import com.aallam.openai.api.classification.ClassificationRequest
import com.aallam.openai.client.Classifications
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

/**
 * Implementation of [Classifications].
 */
internal class ClassificationsApi(private val httpTransport: HttpTransport) : Classifications {

    @ExperimentalOpenAI
    override suspend fun classifications(request: ClassificationRequest): Classification {
        return httpTransport.perform {
            it.post {
                url(path = ClassificationsPath)
                setBody(request)
            }.body()
        }
    }

    companion object {
        private const val ClassificationsPath = "/v1/classifications"
    }
}
