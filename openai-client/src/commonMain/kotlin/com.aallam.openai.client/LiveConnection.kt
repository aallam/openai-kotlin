package com.aallam.openai.client

import com.aallam.openai.api.live.LiveEvent
import kotlinx.coroutines.flow.Flow

/** Scoped Live primary, sideband or fork WebSocket. Device audio and media processing belong to the caller. */
public interface LiveConnection {
    /** Single-consumer event stream. Stopping collection closes the socket; service errors remain protocol events. */
    public val events: Flow<LiveEvent>
    /** Sends one client event. Wait for session.started before primary/fork audio or commands. */
    public suspend fun send(event: LiveEvent)
    /** Closes this socket. Closing a sideband does not hang up the underlying session. */
    public suspend fun close()
}
