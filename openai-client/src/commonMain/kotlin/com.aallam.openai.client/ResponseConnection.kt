package com.aallam.openai.client

import com.aallam.openai.api.response.ResponseRequest
import com.aallam.openai.api.response.ResponseStreamEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.JsonObject

/** A scoped Responses WebSocket. Keep one event collector active while sending successive turns. */
public interface ResponseConnection {
    /**
     * Single-consumer protocol events. Named streams may interleave; route using streamId.
     * Completing or cancelling collection closes the connection. API errors remain events.
     */
    public val events: Flow<ResponseStreamEvent>

    /**
     * Sends a response.create event with request fields at the top level.
     * Sends are serialized. streamId selects a multiplexed conversation lane; generate=false warms up state.
     * Background requests are unsupported on WebSockets and are rejected before sending.
     */
    public suspend fun create(request: ResponseRequest, streamId: String? = null, generate: Boolean? = null)

    /** Sends an arbitrary protocol event, such as response.steer. Sends are serialized. */
    public suspend fun send(event: JsonObject)

    /** Closes the connection. Safe to call repeatedly; subsequent sends fail. */
    public suspend fun close()
}
