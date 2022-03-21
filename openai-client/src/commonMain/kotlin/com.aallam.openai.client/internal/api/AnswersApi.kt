package com.aallam.openai.client.internal.api

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.answer.Answer
import com.aallam.openai.api.answer.AnswerRequest
import com.aallam.openai.client.Answers
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url

/**
 * Implementation of [Answers].
 */
internal class AnswersApi(private val httpTransport: HttpTransport) : Answers {

    @ExperimentalOpenAI
    override suspend fun answers(request: AnswerRequest): Answer {
        return httpTransport.perform {
            it.post {
                url(path = AnswersPath)
                setBody(request)
            }.body()
        }
    }

    companion object {
        private const val AnswersPath = "/v1/answers"
    }
}
