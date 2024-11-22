package com.aallam.openai.client.internal.http

import com.aallam.openai.api.run.AssistantStreamEvent
import io.ktor.client.*
import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.sse.ServerSentEvent
import io.ktor.util.reflect.*
import kotlinx.coroutines.flow.Flow

/**
 * Http request performer.
 */
internal interface HttpRequester : AutoCloseable {

    /**
     * Perform an HTTP request and get a result.
     */
    suspend fun <T : Any> perform(info: TypeInfo, block: suspend (HttpClient) -> HttpResponse): T

    /**
     * Perform an HTTP request and process emitted server-side events.
     *
     */
    suspend fun performSse(
        builderBlock: HttpRequestBuilder.() -> Unit
    ): Flow<AssistantStreamEvent>

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
