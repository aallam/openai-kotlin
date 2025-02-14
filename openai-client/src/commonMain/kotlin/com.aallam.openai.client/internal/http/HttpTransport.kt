package com.aallam.openai.client.internal.http

import com.aallam.openai.api.exception.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.lang.RuntimeException

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
        is ServerResponseException -> handleServerResponseException(e)
        is HttpRequestTimeoutException, is SocketTimeoutException, is ConnectTimeoutException -> OpenAITimeoutException(e)
        is InvalidRequestException -> e
        is IOException -> GenericIOException(e)
        else -> OpenAIHttpException(e)
    }

    private suspend fun handleServerResponseException(e: ServerResponseException): RuntimeException {
        // DeepInfra API returns a 500 status code when the context length exceeds the maximum allowed length, while
        // the openAI API returns a 400 status code. This is a workaround to handle the DeepInfra API response.
        if(e.response.status.value == 500) {
            val body = e.response.body<JsonObject>()
            val errorContent = body["error"]?.jsonObject?.get("message")?.jsonPrimitive?.content
            val errorJson = errorContent?.let { Json.parseToJsonElement(it) }
            val errorMsg = errorJson?.jsonObject?.get("message")?.jsonPrimitive?.content
            if (errorMsg?.startsWith("This model's maximum context length is") == true) {
                return InvalidRequestException(400, OpenAIError(detail =
                    OpenAIErrorDetails(message = errorMsg, code = "context_length_exceeded", param="messages", type = "invalid_request_error")), e)
            }
            return OpenAIServerException(e)
        } else {
            return OpenAIServerException(e)
        }
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
