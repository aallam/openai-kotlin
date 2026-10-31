package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.realtime.*
import kotlinx.coroutines.flow.Flow

/** Scoped bidirectional GA Realtime connection. Audio capture/playback and media encoding belong to the caller. */
public interface RealtimeConnection {
    /** Single-consumer server events. Cancelling collection closes the socket. Service errors remain protocol events. */
    public val events: Flow<RealtimeEvent>
    /** Sends one JSON event. Audio bytes must already be encoded as base64 by the caller. */
    public suspend fun send(event: RealtimeEvent)
    /** Closes the connection. Safe to call more than once. */
    public suspend fun close()
}

/** Realtime credentials, call signaling and scoped server WebSocket connections. */
public interface Realtime {
    /** Creates an ephemeral credential without exposing the server API key to a client. */
    public suspend fun realtimeClientSecret(request: RealtimeClientSecretRequest = RealtimeClientSecretRequest(), requestOptions: RequestOptions? = null): RealtimeClientSecret =
        throw UnsupportedOperationException("Realtime credentials are not implemented by this client")
    /** Creates a continuous translation credential. */
    public suspend fun realtimeTranslationSecret(request: RealtimeTranslationSecretRequest, requestOptions: RequestOptions? = null): RealtimeClientSecret =
        throw UnsupportedOperationException("Realtime translation is not implemented by this client")
    /** Exchanges an SDP offer for an answer. The caller owns the WebRTC peer connection and media tracks. */
    public suspend fun realtimeCall(sdp: String, session: RealtimeSessionConfig? = null, requestOptions: RequestOptions? = null): RealtimeCall =
        throw UnsupportedOperationException("Realtime calls are not implemented by this client")
    /** Accepts an incoming SIP call using the supplied realtime session configuration. */
    public suspend fun acceptRealtimeCall(id: String, session: RealtimeSessionConfig, requestOptions: RequestOptions? = null): Unit =
        throw UnsupportedOperationException("Realtime calls are not implemented by this client")
    /** Rejects an incoming SIP call; statusCode is an optional SIP status. */
    public suspend fun rejectRealtimeCall(id: String, statusCode: Int? = null, requestOptions: RequestOptions? = null): Unit =
        throw UnsupportedOperationException("Realtime calls are not implemented by this client")
    /** Transfers a SIP call to a tel: or sip: URI. */
    public suspend fun referRealtimeCall(id: String, targetUri: String, requestOptions: RequestOptions? = null): Unit =
        throw UnsupportedOperationException("Realtime calls are not implemented by this client")
    /** Hangs up a call. */
    public suspend fun hangupRealtimeCall(id: String, requestOptions: RequestOptions? = null): Unit =
        throw UnsupportedOperationException("Realtime calls are not implemented by this client")
    /** Opens a scoped WebSocket conversation. Use model for a new session or callId for an existing call's sideband. secure=false is only for trusted local test servers. */
    public suspend fun <T> realtimeSession(model: ModelId? = null, callId: String? = null, secure: Boolean = true, requestOptions: RequestOptions? = null, block: suspend RealtimeConnection.() -> T): T =
        throw UnsupportedOperationException("Realtime WebSockets are not implemented by this client")
    /** Opens a scoped continuous translation WebSocket. The server requires the model query parameter. */
    public suspend fun <T> realtimeTranslation(model: ModelId, secure: Boolean = true, requestOptions: RequestOptions? = null, block: suspend RealtimeConnection.() -> T): T =
        throw UnsupportedOperationException("Realtime translation is not implemented by this client")
}
