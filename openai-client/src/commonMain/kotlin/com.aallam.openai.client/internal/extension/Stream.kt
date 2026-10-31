package com.aallam.openai.client.internal.extension

import com.aallam.openai.client.internal.JsonLenient
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.isActive

private const val STREAM_PREFIX = "data:"
private const val STREAM_END_TOKEN = "$STREAM_PREFIX [DONE]"

/**
 * Get data as [Server-Sent Events](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#Event_stream_format).
 */
internal suspend inline fun <reified T> SendChannel<T>.streamEventsFrom(response: HttpResponse) {
    readStreamEventsFrom<T>(response) { send(it) }
}

/** Reads events into a collector that already runs in the response callback's coroutine context. */
internal suspend inline fun <reified T> FlowCollector<T>.streamEventsFrom(response: HttpResponse) {
    readStreamEventsFrom<T>(response) { emit(it) }
}

private suspend inline fun <reified T> readStreamEventsFrom(
    response: HttpResponse,
    consume: (T) -> Unit,
) {
    val channel: ByteReadChannel = response.body()
    try {
        while (currentCoroutineContext().isActive && !channel.isClosedForRead) {
            val line = channel.readUTF8Line() ?: continue
            val value: T = when {
                line.startsWith(STREAM_END_TOKEN) -> break
                line.startsWith(STREAM_PREFIX) -> JsonLenient.decodeFromString(line.removePrefix(STREAM_PREFIX))
                else -> continue
            }
            consume(value)
        }
    } finally {
        channel.cancel()
    }
}
