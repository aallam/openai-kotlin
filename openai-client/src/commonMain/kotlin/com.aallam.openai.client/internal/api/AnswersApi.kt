package com.aallam.openai.client.internal.api

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.answer.Answer
import com.aallam.openai.api.answer.AnswerRequest
import com.aallam.openai.client.Answers
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Implementation of [Answers].
 */
internal class AnswersApi(private val httpClient: HttpClient) : Answers {

    @ExperimentalOpenAI
    override suspend fun answers(request: AnswerRequest): Answer {
        return httpClient.post {
            url(path = AnswersPath)
            setBody(request)
            contentType(ContentType.Application.Json)
        }.body()
    }

    companion object {
        private const val AnswersPath = "/v1/answers"
    }
}
