package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.realtime.*
import com.aallam.openai.client.internal.JsonLenient
import com.aallam.openai.client.internal.api.RealtimeApi
import com.aallam.openai.client.internal.createHttpClient
import com.aallam.openai.client.internal.http.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.TextContent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineRealtime {
    @Test
    fun ephemeralCredentialsUseGaNestedConfigurationAndPreserveServerFields() = runTest {
        val engine = MockEngine { request ->
            assertEquals("/v1/realtime/client_secrets", request.url.encodedPath)
            assertNull(request.headers["OpenAI-Beta"])
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertEquals("realtime", body["session"]!!.jsonObject["type"]!!.jsonPrimitive.content)
            assertEquals(60, body["expires_after"]!!.jsonObject["seconds"]!!.jsonPrimitive.int)
            assertEquals("hashed-user", request.headers["OpenAI-Safety-Identifier"])
            respond("""{"value":"ephemeral-secret","expires_at":123,"session":{"id":"sess_1","new_field":true}}""", headers = jsonHeaders)
        }
        OpenAI(OpenAIConfig(token = "offline", engine = engine)).use { client ->
            val result = client.realtimeClientSecret(
                RealtimeClientSecretRequest(RealtimeSessionConfig(model = ModelId("realtime-model")), RealtimeSecretExpiry(60)),
                RequestOptions(headers = mapOf("OpenAI-Safety-Identifier" to "hashed-user")),
            )
            assertEquals("ephemeral-secret", result.value)
            assertEquals(true, result.session["new_field"]!!.jsonPrimitive.boolean)
            assertFalse(result.toString().contains("ephemeral-secret"))
        }
    }

    @Test
    fun sdpSignalingAndSipControlsUseSeparateContracts() = runTest {
        val seen = mutableListOf<String>()
        val engine = MockEngine { request ->
            seen += request.url.encodedPath
            when (request.url.encodedPath) {
                "/v1/realtime/calls" -> {
                    val multipart = request.body.toByteArray().decodeToString()
                    assertContains(multipart, "name=sdp")
                    assertContains(multipart, "v=0")
                    assertContains(multipart, "name=session")
                    respond("v=0\ns=answer", headers = headersOf(HttpHeaders.ContentType to listOf("application/sdp"), HttpHeaders.Location to listOf("/v1/realtime/calls/rtc_123")))
                }
                "/v1/realtime/calls/rtc_123/refer" -> {
                    val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
                    assertEquals("sip:help@example.com", body["target_uri"]!!.jsonPrimitive.content)
                    respond("", HttpStatusCode.OK)
                }
                else -> respond("", HttpStatusCode.OK)
            }
        }
        OpenAI(OpenAIConfig(token = "offline", engine = engine)).use { client ->
            val call = client.realtimeCall("v=0", RealtimeSessionConfig(model = ModelId("realtime-model")))
            assertEquals("rtc_123", call.id)
            assertEquals("v=0\ns=answer", call.sdp)
            client.acceptRealtimeCall("rtc_123", RealtimeSessionConfig())
            client.rejectRealtimeCall("rtc_123", 486)
            client.referRealtimeCall("rtc_123", "sip:help@example.com")
            client.hangupRealtimeCall("rtc_123")
        }
        assertEquals(5, seen.size)
    }

    @Test
    fun websocketAudioAndToolsPreserveEventsAndCloseOnEarlyCollection() = runTest {
        val socket = FakeSocket()
        socket.messages.trySend("""{"type":"response.output_audio.delta","delta":"YQ==","event_id":"evt_1","future":{"retained":true}}""")
        val engine = MockEngine { error("WebSocket seam should handle this request") }
        val transport = HttpTransport(createHttpClient(OpenAIConfig(token = "offline", engine = engine)))
        val requester = object : HttpRequester by transport {
            override suspend fun realtimeSocket(builder: HttpRequestBuilder.() -> Unit): RealtimeSocket {
                val request = HttpRequestBuilder().apply(builder)
                assertEquals("wss", request.url.protocol.name)
                assertEquals("realtime-model", request.url.parameters["model"])
                return socket
            }
        }
        try {
            RealtimeApi(requester).realtimeSession(ModelId("realtime-model")) {
                send(RealtimeEvent.appendAudio("YQ=="))
                send(RealtimeEvent.functionOutput("call_1", "result"))
                val event = events.take(1).toList().single()
                assertEquals("YQ==", event.delta)
                assertNotNull(event.payload["future"])
                assertTrue(socket.closed)
            }
            assertTrue(socket.closed)
            assertEquals("input_audio_buffer.append", JsonLenient.decodeFromString<RealtimeEvent>(socket.sent[0]).type)
            val output = JsonLenient.decodeFromString<RealtimeEvent>(socket.sent[1])
            assertEquals("call_1", output.payload["item"]!!.jsonObject["call_id"]!!.jsonPrimitive.content)
        } finally { transport.close() }
    }

    @Test
    fun scopeFailureClosesSocketAndTranslationHasRequiredModelQuery() = runTest {
        val socket = FakeSocket()
        val engine = MockEngine { error("Unexpected HTTP request") }
        val transport = HttpTransport(createHttpClient(OpenAIConfig(token = "offline", engine = engine)))
        val requester = object : HttpRequester by transport {
            override suspend fun realtimeSocket(builder: HttpRequestBuilder.() -> Unit): RealtimeSocket {
                val request = HttpRequestBuilder().apply(builder)
                assertEquals("realtime/translations", request.url.encodedPath.trimStart('/'))
                assertEquals("translation-model", request.url.parameters["model"])
                return socket
            }
        }
        try {
            assertFailsWith<IllegalStateException> {
                RealtimeApi(requester).realtimeTranslation(ModelId("translation-model")) { error("cancel scope") }
            }
            assertTrue(socket.closed)
        } finally { transport.close() }
    }

    @Test
    fun configuredWebSocketTransportConnectsToLocalServerAndCloses() = runTest {
        val server = java.net.ServerSocket(0, 1, java.net.InetAddress.getLoopbackAddress())
        val serverFailure = java.util.concurrent.atomic.AtomicReference<Throwable?>()
        val worker = kotlin.concurrent.thread(isDaemon = true) {
            try {
                server.accept().use { socket ->
                    socket.soTimeout = 5000
                    val input = socket.getInputStream().buffered()
                    val header = StringBuilder()
                    while (!header.endsWith("\r\n\r\n")) {
                        val byte = input.read()
                        check(byte >= 0) { "Incomplete handshake" }
                        header.append(byte.toChar())
                    }
                    check(header.startsWith("GET /v1/realtime?model=local-test ")) { "Unexpected handshake path: ${header.lineSequence().first()}" }
                    val key = header.lineSequence().first { it.startsWith("Sec-WebSocket-Key:", ignoreCase = true) }.substringAfter(':').trim()
                    val digest = java.security.MessageDigest.getInstance("SHA-1").digest((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").toByteArray())
                    val accept = java.util.Base64.getEncoder().encodeToString(digest)
                    val output = socket.getOutputStream()
                    output.write("HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Accept: $accept\r\n\r\n".toByteArray())
                    val event = "{\"type\":\"session.created\",\"session\":{\"id\":\"local\"}}".toByteArray()
                    output.write(byteArrayOf(0x81.toByte(), event.size.toByte()))
                    output.write(event)
                    output.flush()
                    // The client closes its socket when collection stops.
                    check(input.read() >= 0)
                    output.write(byteArrayOf(0x88.toByte(), 0))
                    output.flush()
                }
            } catch (failure: Throwable) { serverFailure.set(failure) }
        }
        try {
            val config = OpenAIConfig(token = "offline", host = OpenAIHost("http://127.0.0.1:${server.localPort}/v1/"), engine = io.ktor.client.engine.okhttp.OkHttp.create())
            OpenAI(config).use { client ->
                val events = client.realtimeSession(ModelId("local-test"), secure = false) { events.take(1).toList() }
                assertEquals("session.created", events.single().type)
            }
            worker.join(6000)
            assertFalse(worker.isAlive)
            serverFailure.get()?.let { throw it }
        } finally { server.close() }
    }

    private class FakeSocket : RealtimeSocket {
        val messages = Channel<String>(Channel.UNLIMITED)
        val sent = mutableListOf<String>()
        var closed = false
        override val incoming = messages.receiveAsFlow()
        override suspend fun send(text: String) { sent += text }
        override suspend fun close() { closed = true; messages.close() }
    }
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
}
