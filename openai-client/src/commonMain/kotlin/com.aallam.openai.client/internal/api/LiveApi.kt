package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.live.*
import com.aallam.openai.client.Live
import com.aallam.openai.client.LiveConnection
import com.aallam.openai.client.internal.JsonLenient
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.plugins.retry
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

internal class LiveApi(private val requester: HttpRequester) : Live {
    override suspend fun createLiveSession(request: LiveSessionRequest, requestOptions: RequestOptions?): LiveSessionResult =
        post("live/sessions", request, requestOptions, disableRetries = request.transport is LiveTransport.Sip)

    override suspend fun acceptLiveSession(id: String, session: LiveSessionConfig, requestOptions: RequestOptions?) {
        require(session.audio?.format == null && session.client == null) { "SIP acceptance does not accept WebSocket audio formats or WebRTC frontend permissions" }
        val body = JsonLenient.encodeToJsonElement(session).jsonObject
        postEmpty(sessionPath(id, "accept"), buildJsonObject { put("session", JsonObject(body + ("type" to JsonPrimitive("live")))) }, requestOptions)
    }

    override suspend fun forkLiveSession(id: String, request: LiveSessionForkRequest, requestOptions: RequestOptions?): LiveSessionResult =
        post(sessionPath(id, "fork"), request, requestOptions)

    override suspend fun downloadLiveRecording(id: String, requestOptions: RequestOptions?): ByteArray = requester.perform {
        it.get { url(path = sessionPath(id, "content")); requestOptions(requestOptions) }
    }

    override suspend fun hangupLiveSession(id: String, requestOptions: RequestOptions?) {
        postEmpty(sessionPath(id, "hangup"), null, requestOptions)
    }

    override suspend fun referLiveSession(id: String, targetUri: String, requestOptions: RequestOptions?) {
        require(targetUri.isNotBlank()) { "A nonblank target URI is required" }
        postEmpty(sessionPath(id, "refer"), buildJsonObject { put("target_uri", targetUri) }, requestOptions)
    }

    override suspend fun rejectLiveSession(id: String, statusCode: Int, requestOptions: RequestOptions?) {
        require(statusCode in 300..699) { "SIP rejection status must be between 300 and 699" }
        postEmpty(sessionPath(id, "reject"), buildJsonObject { put("status_code", statusCode) }, requestOptions)
    }

    override suspend fun <T> liveSession(session: LiveSessionConfig, secure: Boolean, requestOptions: RequestOptions?, block: suspend LiveConnection.() -> T): T =
        connect("live/sessions", LiveEvent.start(session), secure, requestOptions, block)

    override suspend fun <T> attachLiveSession(id: String, secure: Boolean, requestOptions: RequestOptions?, block: suspend LiveConnection.() -> T): T =
        connect(sessionPath(id, "attach"), null, secure, requestOptions, block)

    override suspend fun <T> forkLiveConnection(id: String, session: LiveSessionOverrides, secure: Boolean, requestOptions: RequestOptions?, block: suspend LiveConnection.() -> T): T =
        connect(sessionPath(id, "fork"), LiveEvent.startFork(session), secure, requestOptions, block)

    private suspend fun <T> connect(path: String, start: LiveEvent?, secure: Boolean, options: RequestOptions?, block: suspend LiveConnection.() -> T): T {
        val socket = requester.realtimeSocket {
            url(path = path) { protocol = if (secure) URLProtocol.WSS else URLProtocol.WS }
            requestOptions(options)
        }
        val connection = object : LiveConnection {
            override val events = flow {
                try { socket.incoming.collect { emit(JsonLenient.decodeFromString<LiveEvent>(it)) } }
                finally { withContext(NonCancellable) { socket.close() } }
            }
            override suspend fun send(event: LiveEvent) { socket.send(JsonLenient.encodeToString(event)) }
            override suspend fun close() { withContext(NonCancellable) { socket.close() } }
        }
        try {
            start?.let { connection.send(it) }
            return connection.block()
        } finally { withContext(NonCancellable) { socket.close() } }
    }

    private fun sessionPath(id: String, operation: String): String {
        require(id.isNotEmpty() && id.none { it in "/?#%\\" }) { "Invalid Live session ID" }
        return "live/sessions/$id/$operation"
    }

    private suspend inline fun <reified T : Any, reified B : Any> post(path: String, body: B, options: RequestOptions?, disableRetries: Boolean = false): T = requester.perform {
        it.post {
            url(path = path)
            contentType(ContentType.Application.Json)
            setBody(body)
            requestOptions(options)
            if (disableRetries) retry { maxRetries = 0 }
        }.body()
    }

    private suspend fun postEmpty(path: String, body: JsonObject?, options: RequestOptions?) {
        val builder = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(path = path)
            body?.let { contentType(ContentType.Application.Json); setBody(it) }
            requestOptions(options)
        }
        requester.perform(builder) { }
    }
}
