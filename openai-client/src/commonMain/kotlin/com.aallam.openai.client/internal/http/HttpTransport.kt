package com.aallam.openai.client.internal.http

import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.exception.OpenAIHttpException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.instanceOf
import io.ktor.util.reflect.typeInfo

/** HTTP transport layer */
internal class HttpTransport(private val httpClient: HttpClient) {

    /** Perform an HTTP request and get a result */
    suspend inline fun <reified T> perform(noinline block: suspend (HttpClient) -> HttpResponse): T {
        return perform(typeInfo<T>(), block)
    }

    /** Perform an HTTP request and get a result */
    suspend fun <T : Any> perform(info: TypeInfo, block: suspend (HttpClient) -> HttpResponse): T {
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
            else -> throw OpenAIAPIException(status.value, bodyAsText())
        }
    }
}
