package com.aallam.openai.client.internal.extension

import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.exception.OpenAIError
import com.aallam.openai.api.exception.OpenAIErrorDetails
import com.aallam.openai.client.internal.JsonLenient
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.isActive
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private const val STREAM_PREFIX = "data:"
private const val STREAM_END_TOKEN = "$STREAM_PREFIX [DONE]"

/**
 * Get data as [Server-Sent Events](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#Event_stream_format).
 */
internal suspend inline fun <reified T> FlowCollector<T>.streamEventsFrom(response: HttpResponse) {
    val channel: ByteReadChannel = response.body()
    try {
        while (currentCoroutineContext().isActive && !channel.isClosedForRead) {
            val line = channel.readUTF8Line() ?: continue
            val value: T = when {
                line.startsWith(STREAM_END_TOKEN) -> break
                line.startsWith(STREAM_PREFIX) -> {
                    val jsonData = line.removePrefix(STREAM_PREFIX)
                    try {
                        JsonLenient.decodeFromString(jsonData)
                    } catch (e: SerializationException) {
                        val body = JsonLenient.decodeFromString<JsonObject>(jsonData)
                        val errorMessageContent = body["error_message"]?.jsonPrimitive?.content
                        if(errorMessageContent != null) {
                            val errorMessageJsonStr = errorMessageContent.substring(errorMessageContent.indexOf("{"))
                            val errorJson = errorMessageJsonStr.let { Json.parseToJsonElement(it) }
                            val errorMsg = errorJson.jsonObject["message"]?.jsonPrimitive?.content
                            if (errorMsg?.startsWith("This model's maximum context length is") == true) {
                                throw InvalidRequestException(400, OpenAIError(detail =
                                    OpenAIErrorDetails(message = errorMsg, code = "context_length_exceeded", param="messages", type = "invalid_request_error")
                                ))
                            }
                        }
                        throw e
                    }
                }
                else -> continue
            }
            emit(value)
        }
    } finally {
        channel.cancel()
    }
}
