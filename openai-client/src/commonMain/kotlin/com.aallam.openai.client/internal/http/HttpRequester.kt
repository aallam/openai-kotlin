package com.aallam.openai.client.internal.http

import com.aallam.openai.client.Closeable
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*

/**
 * Http request performer.
 */
internal interface HttpRequester : Closeable {

    /**
     * Perform an HTTP request and get a result.
     */
    suspend fun <T : Any> perform(info: TypeInfo, block: suspend (HttpClient) -> HttpResponse): T

    /**
     * Perform an HTTP request and get a result.
     *
     * Note: [HttpResponse] instance shouldn't be passed outside of [block].
     */
    suspend fun <T : Any> perform(
        builder: HttpRequestBuilder,
        block: suspend (response: HttpResponse) -> T
    )
}

/**
 * Perform an HTTP request and get a result
 */
internal suspend inline fun <reified T> HttpRequester.perform(noinline block: suspend (HttpClient) -> HttpResponse): T {
    return perform(typeInfo<T>(), block)
}
