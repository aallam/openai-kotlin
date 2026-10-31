package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.exception.GenericIOException
import com.aallam.openai.api.live.*
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.JsonLenient
import com.aallam.openai.client.internal.api.LiveApi
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
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineLive {
    @Test
    fun mediaSessionLifecycleUsesSeparateTransportContractsAndBinaryContent() = runTest {
        val seen = mutableListOf<String>()
        val recording = byteArrayOf(0, 1, 127, -128, -1)
        val engine = MockEngine { request ->
            seen += "${request.method.value} ${request.url.encodedPath}"
            assertEquals("value", request.headers["X-Test"])
            when (seen.size) {
                1 -> {
                    assertEquals("/v1/live/sessions", request.url.encodedPath)
                    assertEquals(Json.parseToJsonElement("""{"session":{"model":"gpt-live-1","instructions":"Help","store":true},"transport":{"type":"webrtc","sdp":"offer"}}"""), body(request))
                    respond(resultJson, HttpStatusCode.Created, jsonHeaders)
                }
                2 -> {
                    val transport = body(request).getValue("transport").jsonObject
                    assertEquals("sip", transport.getValue("type").jsonPrimitive.content)
                    assertEquals("+12025550123", transport.getValue("destination").jsonPrimitive.content)
                    assertEquals("digest", transport.getValue("trunk").jsonObject.getValue("auth").jsonObject.getValue("type").jsonPrimitive.content)
                    respond("""{"session":{"id":"live_sip"},"transport":{"type":"sip"}}""", HttpStatusCode.Created, jsonHeaders)
                }
                3 -> {
                    assertEquals("/v1/live/sessions/live_1/accept", request.url.encodedPath)
                    assertEquals(Json.parseToJsonElement("""{"session":{"type":"live","model":"gpt-live-1"}}"""), body(request))
                    respond("", HttpStatusCode.OK)
                }
                4 -> {
                    assertEquals("/v1/live/sessions/live_1/fork", request.url.encodedPath)
                    assertEquals(Json.parseToJsonElement("""{"transport":{"type":"webrtc","sdp":"fork-offer"},"session":{"store":false}}"""), body(request))
                    respond(resultJson, HttpStatusCode.Created, jsonHeaders)
                }
                5 -> {
                    assertEquals("/v1/live/sessions/live_1/refer", request.url.encodedPath)
                    assertEquals(Json.parseToJsonElement("""{"target_uri":"sip:agent@example.com"}"""), body(request))
                    respond("", HttpStatusCode.OK)
                }
                6 -> {
                    assertEquals("/v1/live/sessions/live_1/reject", request.url.encodedPath)
                    assertEquals(Json.parseToJsonElement("""{"status_code":486}"""), body(request))
                    respond("", HttpStatusCode.OK)
                }
                7 -> {
                    assertEquals("/v1/live/sessions/live_1/hangup", request.url.encodedPath)
                    assertTrue(request.body.toByteArray().isEmpty())
                    respond("", HttpStatusCode.OK)
                }
                8 -> {
                    assertEquals("/v1/live/sessions/live_1/content", request.url.encodedPath)
                    assertEquals(HttpMethod.Get, request.method)
                    respond(recording, headers = headersOf(HttpHeaders.ContentType, "application/octet-stream"))
                }
                else -> error("Unexpected request")
            }
        }
        withApi(engine) { api ->
            val options = RequestOptions(headers = mapOf("X-Test" to "value"))
            val request = liveSessionRequest {
                session = LiveSessionConfig(ModelId("gpt-live-1"), instructions = "Help", store = true)
                transport = LiveTransport.WebRtc("offer")
            }
            val created = api.createLiveSession(request, options)
            assertEquals("live_1", created.session.id)
            assertEquals("answer", assertIs<LiveTransportResult.WebRtc>(created.transport).sdp)
            val sip = api.createLiveSession(LiveSessionRequest(config(), sipTransport()), options)
            assertEquals(LiveTransportResult.Sip, sip.transport)
            api.acceptLiveSession("live_1", config(), options)
            api.forkLiveSession("live_1", LiveSessionForkRequest(LiveTransport.WebRtc("fork-offer"), LiveSessionOverrides(store = false)), options)
            api.referLiveSession("live_1", "sip:agent@example.com", options)
            api.rejectLiveSession("live_1", 486, options)
            api.hangupLiveSession("live_1", options)
            assertContentEquals(recording, api.downloadLiveRecording("live_1", options))
        }
        assertEquals(8, seen.size)
        assertTrue(seen.take(7).all { it.startsWith("POST ") })
    }

    @Test
    fun serverErrorsAndLocalContractFailuresDoNotBecomeSuccessfulSessions() = runTest {
        var requests = 0
        withApi(MockEngine {
            requests++
            respond("""{"error":{"message":"Invalid session","type":"invalid_request_error","param":"session","code":"invalid_value"}}""", HttpStatusCode.BadRequest, jsonHeaders)
        }) { api ->
            val error = assertFailsWith<InvalidRequestException> { api.createLiveSession(LiveSessionRequest(config(), LiveTransport.WebRtc("offer"))) }
            assertEquals("session", error.error.detail?.param)
            assertFailsWith<IllegalArgumentException> { api.rejectLiveSession("live_1", 200) }
            assertFailsWith<IllegalArgumentException> { api.referLiveSession("live_1", " ") }
            assertFailsWith<IllegalArgumentException> { api.hangupLiveSession("live_1/other") }
            assertFailsWith<IllegalArgumentException> { api.acceptLiveSession("live_1", config().copy(audio = LiveAudioConfig(LiveAudioFormat()))) }
        }
        assertEquals(1, requests)
    }

    @Test
    fun uncertainOutboundSipCallsAreNotAutomaticallyRetried() = runTest {
        var requests = 0
        val engine = MockEngine { requests++; throw java.io.IOException("connection lost after sending") }
        val transport = HttpTransport(createHttpClient(OpenAIConfig(token = "offline", engine = engine, retry = RetryStrategy(maxRetries = 2))))
        try {
            assertFailsWith<GenericIOException> { LiveApi(transport).createLiveSession(LiveSessionRequest(config(), sipTransport())) }
            assertEquals(1, requests)
        } finally { transport.close() }
    }

    @Test
    fun primarySidebandAndForkUseTheirOwnPathsAndStartRules() = runTest {
        val paths = mutableListOf<String>()
        val sockets = mutableListOf<FakeSocket>()
        val transport = HttpTransport(createHttpClient(OpenAIConfig(token = "offline", engine = MockEngine { error("Unexpected HTTP request") })))
        val requester = object : HttpRequester by transport {
            override suspend fun realtimeSocket(builder: HttpRequestBuilder.() -> Unit): RealtimeSocket {
                val request = HttpRequestBuilder().apply(builder)
                assertEquals("wss", request.url.protocol.name)
                assertTrue(request.url.parameters.isEmpty())
                assertEquals("value", request.headers["X-Test"])
                paths += request.url.encodedPath.trimStart('/')
                return FakeSocket().also {
                    it.messages.trySend("""{"type":"session.output_audio.delta","delta":"AQID","start_ms":1,"end_ms":2,"future":{"retained":true}}""")
                    sockets += it
                }
            }
        }
        try {
            val api = LiveApi(requester)
            val options = RequestOptions(headers = mapOf("X-Test" to "value"))
            api.liveSession(config(), requestOptions = options) {
                assertEquals("session.start", JsonLenient.decodeFromString<LiveEvent>(sockets.last().sent.single()).type)
                val event = events.take(1).toList().single()
                assertEquals("AQID", event.delta)
                assertEquals(1L, event.startMs)
                assertNotNull(event.payload["future"])
                assertTrue(sockets.last().closed)
            }
            api.attachLiveSession("live_1", requestOptions = options) {
                assertTrue(sockets.last().sent.isEmpty())
                events.take(1).toList()
            }
            api.forkLiveConnection("live_1", requestOptions = options) {
                val start = JsonLenient.decodeFromString<LiveEvent>(sockets.last().sent.single())
                assertEquals("session.start", start.type)
                assertEquals(buildJsonObject {}, start.session)
                events.take(1).toList()
            }
            assertEquals(listOf("live/sessions", "live/sessions/live_1/attach", "live/sessions/live_1/fork"), paths)
            assertTrue(sockets.all { it.closed })
        } finally { transport.close() }
    }

    @Test
    fun scopeAndInitialSendFailuresCloseTheConnection() = runTest {
        val sockets = mutableListOf<FakeSocket>()
        val transport = HttpTransport(createHttpClient(OpenAIConfig(token = "offline", engine = MockEngine { error("Unexpected HTTP") })))
        val requester = object : HttpRequester by transport {
            override suspend fun realtimeSocket(builder: HttpRequestBuilder.() -> Unit): RealtimeSocket =
                FakeSocket(failSend = sockets.isNotEmpty()).also { sockets += it }
        }
        try {
            val api = LiveApi(requester)
            assertFailsWith<IllegalStateException> { api.attachLiveSession("live_1") { error("scope failure") } }
            assertTrue(sockets.single().closed)
            assertFailsWith<IllegalStateException> { api.liveSession(config()) { error("unreachable") } }
            assertTrue(sockets.last().closed)
        } finally { transport.close() }
    }

    @Test
    fun optionalCapabilityCanBeOmittedByCustomClients() = runTest {
        val client = object : Live {}
        assertFailsWith<UnsupportedOperationException> { client.createLiveSession(LiveSessionRequest(config(), LiveTransport.WebRtc("offer"))) }
        assertFailsWith<UnsupportedOperationException> { client.acceptLiveSession("live_1", config()) }
        assertFailsWith<UnsupportedOperationException> { client.forkLiveSession("live_1", LiveSessionForkRequest(LiveTransport.WebRtc("offer"))) }
        assertFailsWith<UnsupportedOperationException> { client.downloadLiveRecording("live_1") }
        assertFailsWith<UnsupportedOperationException> { client.hangupLiveSession("live_1") }
        assertFailsWith<UnsupportedOperationException> { client.referLiveSession("live_1", "sip:example.com") }
        assertFailsWith<UnsupportedOperationException> { client.rejectLiveSession("live_1", 486) }
        assertFailsWith<UnsupportedOperationException> { client.liveSession(config()) {} }
        assertFailsWith<UnsupportedOperationException> { client.attachLiveSession("live_1") {} }
        assertFailsWith<UnsupportedOperationException> { client.forkLiveConnection("live_1") {} }
    }

    private suspend fun withApi(engine: MockEngine, block: suspend (LiveApi) -> Unit) {
        val transport = HttpTransport(createHttpClient(OpenAIConfig(token = "offline", engine = engine, retry = RetryStrategy(maxRetries = 0), logging = LoggingConfig(LogLevel.None))))
        try { block(LiveApi(transport)) } finally { transport.close() }
    }

    private fun body(request: io.ktor.client.request.HttpRequestData): JsonObject = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
    private fun config() = LiveSessionConfig(ModelId("gpt-live-1"))
    private fun sipTransport() = LiveTransport.Sip("+12025550123", LiveSipTrunk(LiveSipAuthentication("user", "password"), "+12025550124", "sips:trunk.example.com"))
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val resultJson = """{"session":{"id":"live_1"},"transport":{"type":"webrtc","sdp":"answer"}}"""

    private class FakeSocket(private val failSend: Boolean = false) : RealtimeSocket {
        val messages = Channel<String>(Channel.UNLIMITED)
        val sent = mutableListOf<String>()
        var closed = false
        override val incoming = messages.receiveAsFlow()
        override suspend fun send(text: String) { if (failSend) error("send failed"); sent += text }
        override suspend fun close() { closed = true; messages.close() }
    }
}
