package com.aallam.openai.client.internal.extension

import com.aallam.openai.api.core.EventType
import com.aallam.openai.api.core.Event
import com.aallam.openai.client.internal.JsonLenient
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.FlowCollector

private const val STREAM_PREFIX = "data:"
private const val EVENT_PREFIX = "event:"
private const val STREAM_END_TOKEN = "$STREAM_PREFIX [DONE]"

/**
 * Get data as [Server-Sent Events](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#Event_stream_format).
 */
internal suspend inline fun <reified T> FlowCollector<T>.streamEventsFrom(response: HttpResponse) {
    val channel: ByteReadChannel = response.body()
    while (!channel.isClosedForRead) {
        val line = channel.readUTF8Line() ?: continue
        val value: T = when {
            line.startsWith(STREAM_END_TOKEN) -> break
            line.startsWith(STREAM_PREFIX) -> JsonLenient.decodeFromString(line.removePrefix(STREAM_PREFIX))
            else -> continue
        }
        emit(value)
    }
}


internal suspend inline fun <reified T> FlowCollector<Event<T>>.assistantStreamEvents(response: HttpResponse) {
    val channel: ByteReadChannel = response.body()
    while (!channel.isClosedForRead) {
        val line = channel.readUTF8Line() ?: continue
        println(">>>>>>>>>>>>>>>")
        println(line)
        println(">>>>>>>>>>>>>>>")
        val value: Event<T> = when {
            line.startsWith(EventType.Done.value) -> break
            line.startsWith(EventType.Error.value) -> TODO()
            line.startsWith(EVENT_PREFIX) -> {
                val event = EventType(line.removePrefix(STREAM_PREFIX))
                val data = channel.readUTF8Line() ?: continue
                println("data -> $data")
                println(">>>>>>>>>>>>>>>")
                val eventData: T = JsonLenient.decodeFromString(data.removePrefix(STREAM_PREFIX))
                Event(event, eventData)
            }
            else -> continue
        }
        emit(value)
    }
}