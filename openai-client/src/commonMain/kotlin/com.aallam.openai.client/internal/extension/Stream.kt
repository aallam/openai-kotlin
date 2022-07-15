package com.aallam.openai.client.internal.extension

import com.aallam.openai.client.internal.JsonLenient
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString

private const val StreamPrefix = "data:"
private const val StreamEndToken = "$StreamPrefix [DONE]"

/**
 * Request data as [Server-Sent Events](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#Event_stream_format).
 */
internal inline fun <reified T> streamEventsOf(crossinline block: suspend () -> HttpResponse): Flow<T> {
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
