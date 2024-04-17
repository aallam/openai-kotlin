package com.aallam.openai.client.internal.extension

import com.aallam.openai.api.core.CustomEvent
import com.aallam.openai.api.core.Event
import com.aallam.openai.api.core.EventType
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.run.Run
import com.aallam.openai.api.run.RunDelta
import com.aallam.openai.api.run.RunStep
import com.aallam.openai.api.thread.ThreadMessage
import com.aallam.openai.client.internal.JsonLenient
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.serialization.KSerializer

private const val STREAM_PREFIX = "data: "
private const val EVENT_PREFIX = "event: "
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


internal suspend inline fun FlowCollector<Event>.assistantStreamEvents(response: HttpResponse) {
    val channel: ByteReadChannel = response.body()
    while (!channel.isClosedForRead) {
        val line = channel.readUTF8Line() ?: continue
        println(">>>>>>>>>>>>>>>")
        println("event -> $line")
        println(">>>>>>>>>>>>>>>")
        val value: Event = when {
            line.startsWith(EventType.Done.value) -> break
            line.startsWith(EventType.Error.value) -> throw RuntimeException("Stream error event received")
            line.startsWith(EVENT_PREFIX) -> {
                val event = EventType(line.removePrefix(EVENT_PREFIX))
                val data = channel.readUTF8Line() ?: continue
                println("data -> $data")
                val serializer = eventTypeSerializer(event.value)
                if (serializer == null) CustomEvent(JsonLenient.parseToJsonElement(data))
                else JsonLenient.decodeFromString(serializer, data.removePrefix(STREAM_PREFIX))
            }

            else -> continue
        }
        emit(value)
    }
}

private fun eventTypeSerializer(eventType: String): KSerializer<out Event>? {
    //"thread.created"
    //"thread.run.created"
    //"thread.run.queued"
    //"thread.run.in_progress"
    //"thread.run.requires_action"
    //"thread.run.completed"
    //"thread.run.failed"
    //"thread.run.cancelling"
    //"thread.run.cancelled"
    //"thread.run.expired"
    //"thread.run.step.created"
    //"thread.run.step.in_progress"
    //"thread.run.step.delta"
    //"thread.run.step.completed"
    //"thread.run.step.failed"
    //"thread.run.step.cancelled"
    //"thread.run.step.expired"
    //"thread.message.created"
    //"thread.message.in_progress"
    //"thread.message.delta"
    //"thread.message.completed"
    //"thread.message.incomplete"
    return when {
        eventType.startsWith("thread.run.step.delta") -> RunDelta.serializer()
        eventType.startsWith("thread.run.step") -> RunStep.serializer()
        eventType.startsWith("thread.run") -> Run.serializer()
        eventType.startsWith("thread.message.delta") -> RunDelta.serializer()
        eventType.startsWith("thread.message") -> Message.serializer()
        //eventType == "thread.created" -> Thread.serializer()
        else -> null
    }
}