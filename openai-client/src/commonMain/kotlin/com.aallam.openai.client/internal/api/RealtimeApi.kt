package com.aallam.openai.client.internal.api

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.realtime.*
import com.aallam.openai.client.Realtime
import com.aallam.openai.client.RealtimeConnection
import com.aallam.openai.client.internal.JsonLenient
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

internal class RealtimeApi(private val requester: HttpRequester) : Realtime {
    override suspend fun realtimeClientSecret(request: RealtimeClientSecretRequest, requestOptions: RequestOptions?): RealtimeClientSecret = post("realtime/client_secrets", request, requestOptions)
    override suspend fun realtimeTranslationSecret(request: RealtimeTranslationSecretRequest, requestOptions: RequestOptions?): RealtimeClientSecret = post("realtime/translations/client_secrets", request, requestOptions)

    override suspend fun realtimeCall(sdp: String, session: RealtimeSessionConfig?, requestOptions: RequestOptions?): RealtimeCall {
        lateinit var result: RealtimeCall
        val request = HttpRequestBuilder().apply {
            method = HttpMethod.Post
            url(path = "realtime/calls")
            setBody(MultiPartFormDataContent(formData {
                append("sdp", sdp, headersOf(HttpHeaders.ContentType, "application/sdp"))
                session?.let { append("session", JsonLenient.encodeToString(it), headersOf(HttpHeaders.ContentType, "application/json")) }
            }))
            requestOptions(requestOptions)
        }
        requester.perform<Unit>(request) { response -> result = RealtimeCall(response.bodyAsText(), response.headers[HttpHeaders.Location]) }
        return result
    }

    override suspend fun acceptRealtimeCall(id: String, session: RealtimeSessionConfig, requestOptions: RequestOptions?) { post<HttpResponse, RealtimeSessionConfig>(callPath(id, "accept"), session, requestOptions) }
    override suspend fun rejectRealtimeCall(id: String, statusCode: Int?, requestOptions: RequestOptions?) { post<HttpResponse, JsonObject>(callPath(id, "reject"), buildJsonObject { statusCode?.let { put("status_code", it) } }, requestOptions) }
    override suspend fun referRealtimeCall(id: String, targetUri: String, requestOptions: RequestOptions?) { post<HttpResponse, JsonObject>(callPath(id, "refer"), buildJsonObject { put("target_uri", targetUri) }, requestOptions) }
    override suspend fun hangupRealtimeCall(id: String, requestOptions: RequestOptions?) { post<HttpResponse, JsonObject>(callPath(id, "hangup"), buildJsonObject {}, requestOptions) }

    override suspend fun <T> realtimeSession(model: ModelId?, callId: String?, secure: Boolean, requestOptions: RequestOptions?, block: suspend RealtimeConnection.() -> T): T {
        require((model == null) != (callId == null)) { "Provide a model for a new session or a call ID for sideband" }
        return connect("realtime", model, callId, secure, requestOptions, block)
    }
    override suspend fun <T> realtimeTranslation(model: ModelId, secure: Boolean, requestOptions: RequestOptions?, block: suspend RealtimeConnection.() -> T): T =
        connect("realtime/translations", model, null, secure, requestOptions, block)

    private suspend fun <T> connect(path: String, model: ModelId?, callId: String?, secure: Boolean, options: RequestOptions?, block: suspend RealtimeConnection.() -> T): T {
        val socket = requester.realtimeSocket {
            url(path = path) {
                protocol = if (secure) URLProtocol.WSS else URLProtocol.WS
                model?.let { parameters.append("model", it.id) }
                callId?.let { parameters.append("call_id", it) }
            }
            requestOptions(options)
        }
        val connection = object : RealtimeConnection {
            override val events = flow {
                try { socket.incoming.collect { emit(JsonLenient.decodeFromString<RealtimeEvent>(it)) } }
                finally { withContext(NonCancellable) { socket.close() } }
            }
            override suspend fun send(event: RealtimeEvent) { socket.send(JsonLenient.encodeToString(event)) }
            override suspend fun close() { socket.close() }
        }
        try { return connection.block() }
        finally { withContext(NonCancellable) { socket.close() } }
    }

    private fun callPath(id: String, operation: String): String {
        require(id.isNotEmpty() && id.none { it == '/' || it == '?' || it == '#' || it == '%' }) { "Invalid call ID" }
        return "realtime/calls/$id/$operation"
    }
    private suspend inline fun <reified R : Any, reified B : Any> post(path: String, body: B, options: RequestOptions?): R = requester.perform {
        it.post {
            url(path = path)
            contentType(ContentType.Application.Json)
            setBody(body)
            requestOptions(options)
        }
    }
}
