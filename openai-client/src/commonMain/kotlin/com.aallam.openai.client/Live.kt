package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.live.*

/** Live media-session lifecycle and scoped server WebSockets. Default methods preserve custom-client source compatibility. */
public interface Live {
    /** Creates a WebRTC session or places a real SIP call. Automatic retries are disabled for SIP; manually retrying an uncertain result can create duplicate calls. */
    public suspend fun createLiveSession(request: LiveSessionRequest, requestOptions: RequestOptions? = null): LiveSessionResult =
        throw UnsupportedOperationException("Live sessions are not supported by this client")
    /** Accepts an incoming SIP call. Audio formats are negotiated by SIP; client data-channel settings do not apply. */
    public suspend fun acceptLiveSession(id: String, session: LiveSessionConfig, requestOptions: RequestOptions? = null): Unit =
        throw UnsupportedOperationException("Live sessions are not supported by this client")
    /** Forks stored conversation state into a new WebRTC session. */
    public suspend fun forkLiveSession(id: String, request: LiveSessionForkRequest, requestOptions: RequestOptions? = null): LiveSessionResult =
        throw UnsupportedOperationException("Live sessions are not supported by this client")
    /** Downloads recording content for a session started with storage enabled. */
    public suspend fun downloadLiveRecording(id: String, requestOptions: RequestOptions? = null): ByteArray =
        throw UnsupportedOperationException("Live recordings are not supported by this client")
    /** Ends a SIP call. */
    public suspend fun hangupLiveSession(id: String, requestOptions: RequestOptions? = null): Unit =
        throw UnsupportedOperationException("Live sessions are not supported by this client")
    /** Transfers a SIP call to a nonblank tel: or sip: target URI. */
    public suspend fun referLiveSession(id: String, targetUri: String, requestOptions: RequestOptions? = null): Unit =
        throw UnsupportedOperationException("Live sessions are not supported by this client")
    /** Rejects an incoming SIP call with a required SIP status from 300 to 699. */
    public suspend fun rejectLiveSession(id: String, statusCode: Int, requestOptions: RequestOptions? = null): Unit =
        throw UnsupportedOperationException("Live sessions are not supported by this client")
    /** Opens a primary WebSocket and sends session.start. secure=false is intended only for trusted local test servers. */
    public suspend fun <T> liveSession(session: LiveSessionConfig, secure: Boolean = true, requestOptions: RequestOptions? = null, block: suspend LiveConnection.() -> T): T =
        throw UnsupportedOperationException("Live WebSockets are not supported by this client")
    /** Attaches a trusted sideband to an existing session without sending session.start or replaying earlier events. */
    public suspend fun <T> attachLiveSession(id: String, secure: Boolean = true, requestOptions: RequestOptions? = null, block: suspend LiveConnection.() -> T): T =
        throw UnsupportedOperationException("Live WebSockets are not supported by this client")
    /** Starts a primary WebSocket from stored conversation state; omitted overrides inherit the stored configuration. */
    public suspend fun <T> forkLiveConnection(id: String, session: LiveSessionOverrides = LiveSessionOverrides(), secure: Boolean = true, requestOptions: RequestOptions? = null, block: suspend LiveConnection.() -> T): T =
        throw UnsupportedOperationException("Live WebSockets are not supported by this client")
}
