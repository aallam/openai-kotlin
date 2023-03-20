package com.aallam.openai.client.internal.http

import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.exception.OpenAIHttpException
import com.aallam.openai.api.exception.OpenAIServerException
import com.aallam.openai.api.exception.OpenAITimeoutException
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.CancellationException

/** HTTP transport layer */
internal class HttpTransport(private val httpClient: HttpClient) : HttpRequester {

    /** Perform an HTTP request and get a result */
    override suspend fun <T : Any> perform(info: TypeInfo, block: suspend (HttpClient) -> HttpResponse): T {
        try {
            val response = block(httpClient)
            return response.body(info)
        } catch (e: Exception) {
            throw handleException(e)
        }
    }

    override suspend fun <T : Any> perform(
        builder: HttpRequestBuilder,
        block: suspend (response: HttpResponse) -> T
    ) {
        try {
            HttpStatement(builder = builder, client = httpClient).execute(block)
        } catch (e: Exception) {
            throw handleException(e)
        }
    }

    /** Handle different error cases. */
    private suspend fun handleException(e: Exception) = when (e) {
        is CancellationException -> e // propagate coroutine cancellation
        is ClientRequestException -> OpenAIAPIException(e.response.status.value, e.response.body(), e)
        is ServerResponseException -> OpenAIServerException(e)
        is HttpRequestTimeoutException, is SocketTimeoutException, is ConnectTimeoutException -> OpenAITimeoutException(e)
        else -> OpenAIHttpException(e)
    }
}
