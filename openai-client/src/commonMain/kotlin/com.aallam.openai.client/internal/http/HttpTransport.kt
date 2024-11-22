package com.aallam.openai.client.internal.http

import com.aallam.openai.api.exception.*
import com.aallam.openai.api.run.AssistantStreamEvent
import com.aallam.openai.client.extension.toAssistantStreamEvent
import com.aallam.openai.client.internal.api.ApiPath
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sseSession
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.ContentType
import io.ktor.sse.ServerSentEvent
import io.ktor.util.reflect.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

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

    override suspend fun performSse(
        builderBlock: HttpRequestBuilder.() -> Unit
    ): Flow<AssistantStreamEvent> {
        try {
            return httpClient
                .sseSession(block = builderBlock)
                .incoming
                .map(ServerSentEvent::toAssistantStreamEvent)
        } catch (e: Exception) {
            throw handleException(e)
        }
    }

    override fun close() {
        httpClient.close()
    }

    /**
     * Handles various exceptions that can occur during an API request and converts them into appropriate
     * [OpenAIException] instances.
     */
    private suspend fun handleException(e: Throwable) = when (e) {
        is CancellationException -> e // propagate coroutine cancellation
        is ClientRequestException -> openAIAPIException(e)
        is ServerResponseException -> OpenAIServerException(e)
        is HttpRequestTimeoutException, is SocketTimeoutException, is ConnectTimeoutException -> OpenAITimeoutException(e)
        is IOException -> GenericIOException(e)
        else -> OpenAIHttpException(e)
    }

    /**
     * Converts a [ClientRequestException] into a corresponding [OpenAIAPIException] based on the HTTP status code.
     * This function helps in handling specific API errors and categorizing them into appropriate exception classes.
     */
    private suspend fun openAIAPIException(exception: ClientRequestException): OpenAIAPIException {
        val response = exception.response
        val status = response.status.value
        val error = response.body<OpenAIError>()
        return when(status) {
            429 -> RateLimitException(status, error, exception)
            400, 404, 409, 415 -> InvalidRequestException(status, error, exception)
            401 -> AuthenticationException(status, error, exception)
            403 -> PermissionException(status, error, exception)
            else -> UnknownAPIException(status, error, exception)
        }
    }
}
