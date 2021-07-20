package com.aallam.openai.client.internal.api

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.answer.Answer
import com.aallam.openai.api.answer.AnswerRequest
import com.aallam.openai.client.Answers
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Implementation of [Answers].
 */
internal class AnswersApi(private val httpClient: HttpClient) : Answers {

    @ExperimentalOpenAI
    override suspend fun answers(request: AnswerRequest): Answer {
        return httpClient.post(path = AnswersPath, body = request) {
            contentType(ContentType.Application.Json)
        }
    }

    companion object {
        private const val AnswersPath = "/v1/answers"
    }
}
