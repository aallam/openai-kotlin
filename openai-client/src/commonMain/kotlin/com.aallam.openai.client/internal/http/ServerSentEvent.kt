package com.aallam.openai.client.internal.http

import com.aallam.openai.client.internal.JsonLenient
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/** SSE stream prefix. */
internal const val StreamPrefix = "data:"

/** SSE stream end token. */
internal const val StreamEndToken = "$StreamPrefix [DONE]"

/**
 * Request events as data-only server-sent events as they become available.
 * The stream will terminate with a data: [DONE] message when the job is finished (succeeded, cancelled, or failed).
 */
internal fun requestStream() = JsonObject(mapOf("stream" to JsonPrimitive(true)))

/**
 * Request data as [Server-Sent Events](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#Event_stream_format).
 */
internal inline fun <reified T> streamSSE(crossinline block: suspend () -> HttpResponse): Flow<T> {
    return flow {
        val response = block()
        val readChannel = response.body<ByteReadChannel>()
        while (!readChannel.isClosedForRead) {
            val line = readChannel.readUTF8Line() ?: ""
            val value: T = when {
                line.startsWith(StreamEndToken) -> break
                line.startsWith(StreamPrefix) -> JsonLenient.decodeFromString(line.removePrefix(StreamPrefix))
                else -> continue
            }
            emit(value)
        }
    }
}
