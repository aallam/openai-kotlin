package com.aallam.openai.client.internal.api

import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.api.response.ResponseStreamEvent
import com.aallam.openai.client.ResponseConnection
import com.aallam.openai.client.internal.JsonLenient
import com.aallam.openai.client.internal.http.RealtimeSocket
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*

internal class ResponseSocketConnection(private val socket: RealtimeSocket) : ResponseConnection {
    private val sends = Mutex()
    private val receiver = Mutex()
    private val closed = MutableStateFlow(false)

    override val events = flow {
        check(receiver.tryLock()) { "Only one Responses event collector is allowed" }
        try {
            socket.incoming.collect { emit(JsonLenient.decodeFromString(ResponseStreamEvent.serializer(), it)) }
        } finally {
            receiver.unlock()
            withContext(NonCancellable) { close() }
        }
    }

    override suspend fun create(request: ResponseRequest, streamId: String?, generate: Boolean?) {
        require(request.background != true) { "Background processing is not supported on Responses WebSockets" }
        require(streamId == null || streamId.matches(Regex("[A-Za-z0-9_.-]{1,256}"))) { "Invalid Responses stream ID" }
        val fields = JsonLenient.encodeToJsonElement(ResponseRequest.serializer(), request).jsonObject.toMutableMap()
        fields.remove("background")
        fields.remove("stream")
        fields["type"] = JsonPrimitive("response.create")
        streamId?.let { fields["stream_id"] = JsonPrimitive(it) }
        generate?.let { fields["generate"] = JsonPrimitive(it) }
        send(JsonObject(fields))
    }

    override suspend fun send(event: JsonObject) {
        require(event["type"] is JsonPrimitive && event["type"]?.jsonPrimitive?.isString == true) { "Protocol events require a type" }
        sends.withLock {
            check(!closed.value) { "Responses connection is closed" }
            socket.send(event.toString())
        }
    }

    override suspend fun close() {
        if (closed.compareAndSet(false, true)) socket.close()
    }
}
