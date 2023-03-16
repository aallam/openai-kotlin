package com.aallam.openai.client.internal.http

import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.exception.OpenAIHttpException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.reflect.*

/** HTTP transport layer */
internal class HttpTransport(private val httpClient: HttpClient) : HttpRequester {

    /** Perform an HTTP request and get a result */
    override suspend fun <T : Any> perform(info: TypeInfo, block: suspend (HttpClient) -> HttpResponse): T {
        return try {
            val response = block(httpClient)
            @Suppress("UNCHECKED_CAST")
            if (response.instanceOf(info.type)) return response as T
            response.bodyOrThrow(info)
        } catch (e: Exception) {
            throw OpenAIHttpException(throwable = e)
        }
    }

    /** Get [body] when the response is success (2XX), otherwise throw an exception */
    private suspend fun <T> HttpResponse.bodyOrThrow(info: TypeInfo): T {
        return when {
            status.isSuccess() -> body(info)
            else -> throw OpenAIAPIException(status.value, body())
        }
    }

    override suspend fun <T : Any> perform(
        builder: HttpRequestBuilder,
        block: suspend (response: HttpResponse) -> T
    ) {
        try {
            HttpStatement(builder = builder, client = httpClient).execute {
                when {
                    it.status.isSuccess() -> block(it)
                    else -> throw OpenAIAPIException(it.status.value, it.body())
                }
            }
        } catch (e: Exception) {
            throw OpenAIHttpException(throwable = e)
        }
    }
}
