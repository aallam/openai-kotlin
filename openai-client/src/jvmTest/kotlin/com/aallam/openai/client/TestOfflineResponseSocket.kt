package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.*
import com.aallam.openai.client.internal.createHttpClient
import com.aallam.openai.client.internal.api.ResponsesApi
import com.aallam.openai.client.internal.api.ResponseSocketConnection
import com.aallam.openai.client.internal.http.*
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineResponseSocket {
    @Test
    fun createsTopLevelProtocolEventsAndPreservesMultiplexedErrors() = runTest {
        val socket = FakeSocket()
        val connection = ResponseSocketConnection(socket)
        connection.create(ResponseCreateRequest(ModelId("model"), ResponseInput("hello"), background = false), "main", false)
        val request = Json.parseToJsonElement(socket.sent.single()).jsonObject
        assertEquals("response.create", request["type"]?.jsonPrimitive?.content)
        assertEquals("model", request["model"]?.jsonPrimitive?.content)
        assertEquals("main", request["stream_id"]?.jsonPrimitive?.content)
        assertEquals(false, request["generate"]?.jsonPrimitive?.boolean)
        assertFalse("background" in request)
        assertFalse("stream" in request)
        assertFalse("response" in request)
        socket.messages.send("""{"type":"error","stream_id":"main","error":{"code":"previous_response_not_found"},"future":{"retained":true}}""")
        val event = connection.events.take(1).toList().single()
        assertEquals("main", event.streamId)
        assertEquals("previous_response_not_found", event.data["error"]?.jsonObject?.get("code")?.jsonPrimitive?.content)
        assertNotNull(event.data["future"])
        assertEquals(1, socket.closes)
        connection.close()
        assertEquals(1, socket.closes)
        assertFailsWith<IllegalStateException> { connection.create(ResponseCreateRequest(ModelId("model"))) }
    }

    @Test
    fun validatesUnsupportedBackgroundAndStreamIdsBeforeWriting() = runTest {
        val socket = FakeSocket()
        val connection = ResponseSocketConnection(socket)
        assertFailsWith<IllegalArgumentException> { connection.create(ResponseCreateRequest(ModelId("model"), background = true)) }
        assertFailsWith<IllegalArgumentException> { connection.create(ResponseCreateRequest(ModelId("model")), "bad lane", null) }
        assertFailsWith<IllegalArgumentException> { connection.create(ResponseCreateRequest(ModelId("model")), "", null) }
        assertTrue(socket.sent.isEmpty())
        connection.close()
    }

    @Test
    fun concurrentSendsAreSerializedAndCollectionCancellationClosesScope() = runTest {
        val socket = FakeSocket()
        val connection = ResponseSocketConnection(socket)
        coroutineScope {
            repeat(8) { index -> launch { connection.create(ResponseCreateRequest(ModelId("model")), "lane_$index", null) } }
        }
        assertEquals(8, socket.sent.size)
        val receiver = launch { connection.events.toList() }
        yield()
        assertFailsWith<IllegalStateException> { connection.events.toList() }
        receiver.cancelAndJoin()
        assertEquals(1, socket.closes)
    }

    @Test
    fun closingDoesNotWaitForABackpressuredSend() = runTest {
        val started = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        var closes = 0
        val socket = object : RealtimeSocket {
            override val incoming = kotlinx.coroutines.flow.emptyFlow<String>()
            override suspend fun send(text: String) { started.complete(Unit); release.await() }
            override suspend fun close() { closes++; release.complete(Unit) }
        }
        val connection = ResponseSocketConnection(socket)
        val writer = launch { connection.create(ResponseCreateRequest(ModelId("model"))) }
        started.await()
        connection.close()
        writer.join()
        assertEquals(1, closes)
    }

    @Test
    fun malformedIncomingFramesCloseTheConnection() = runTest {
        val socket = FakeSocket()
        socket.messages.send("invalid JSON")
        val connection = ResponseSocketConnection(socket)
        assertFailsWith<kotlinx.serialization.SerializationException> { connection.events.toList() }
        assertEquals(1, socket.closes)
    }

    @Test
    fun scopedFailureClosesSocketAndRequestOptionsReachHandshake() = runTest {
        val socket = FakeSocket()
        val transport = HttpTransport(createHttpClient(OpenAIConfig(token = "offline", engine = MockEngine { error("Unexpected HTTP") })))
        val requester = object : HttpRequester by transport {
            override suspend fun realtimeSocket(builder: HttpRequestBuilder.() -> Unit): RealtimeSocket {
                val request = HttpRequestBuilder().apply(builder)
                assertEquals("wss", request.url.protocol.name)
                assertEquals("responses", request.url.encodedPath.trimStart('/'))
                assertEquals("trace", request.headers["X-Trace"])
                return socket
            }
        }
        try {
            assertFailsWith<IllegalStateException> {
                ResponsesApi(requester).responseSession(requestOptions = RequestOptions(headers = mapOf("X-Trace" to "trace"))) { error("consumer failure") }
            }
            assertEquals(1, socket.closes)
        } finally { transport.close() }
    }

    @Test
    fun actualConfiguredWebSocketPreservesBasePathPortAndCloses() = runTest {
        val server = java.net.ServerSocket(0, 1, java.net.InetAddress.getByName("127.0.0.1"))
        val failure = java.util.concurrent.atomic.AtomicReference<Throwable?>()
        val worker = kotlin.concurrent.thread(isDaemon = true) {
            try {
                server.accept().use { socket ->
                    socket.soTimeout = 5000
                    val input = socket.getInputStream().buffered()
                    val headers = StringBuilder()
                    while (!headers.endsWith("\r\n\r\n")) {
                        val byte = input.read()
                        check(byte >= 0)
                        headers.append(byte.toChar())
                    }
                    check(headers.startsWith("GET /custom/v1/responses?inspect=true "))
                    check(headers.contains("X-Trace: loopback", ignoreCase = true))
                    val key = headers.lineSequence().first { it.startsWith("Sec-WebSocket-Key:", ignoreCase = true) }.substringAfter(':').trim()
                    val digest = java.security.MessageDigest.getInstance("SHA-1").digest((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").toByteArray())
                    val accept = java.util.Base64.getEncoder().encodeToString(digest)
                    val output = socket.getOutputStream()
                    output.write("HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Accept: $accept\r\n\r\n".toByteArray())
                    output.flush()
                    check(input.read() == 0x81)
                    val second = input.read()
                    check(second and 0x80 != 0)
                    var length = second and 0x7f
                    if (length == 126) length = (input.read() shl 8) or input.read()
                    check(length < 65536)
                    val mask = ByteArray(4) { input.read().toByte() }
                    val bytes = ByteArray(length) { index -> (input.read() xor mask[index % 4].toInt()).toByte() }
                    val request = Json.parseToJsonElement(bytes.decodeToString()).jsonObject
                    check(request["type"]?.jsonPrimitive?.content == "response.create")
                    check(request["stream_id"]?.jsonPrimitive?.content == "main")
                    check(request["model"]?.jsonPrimitive?.content == "socket-model")
                    val event = """{"type":"response.completed","stream_id":"main","response":{"id":"resp_socket"}}""".toByteArray()
                    output.write(byteArrayOf(0x81.toByte(), event.size.toByte()))
                    output.write(event)
                    output.flush()
                    check(input.read() == 0x88)
                    output.write(byteArrayOf(0x88.toByte(), 0))
                    output.flush()
                }
            } catch (exception: Throwable) { failure.set(exception) }
        }
        try {
            val client = OpenAI(OpenAIConfig(token = "offline", host = OpenAIHost("http://127.0.0.1:${server.localPort}/custom/v1/"), engine = io.ktor.client.engine.okhttp.OkHttp.create()))
            client.use {
                val events = client.responseSession(secure = false, requestOptions = RequestOptions(headers = mapOf("X-Trace" to "loopback"), urlParameters = mapOf("inspect" to "true"))) {
                    create(ResponseCreateRequest(ModelId("socket-model"), ResponseInput("hello")), "main")
                    events.take(1).toList()
                }
                assertEquals("resp_socket", events.single().response?.id?.id)
            }
            worker.join(6000)
            assertFalse(worker.isAlive)
            failure.get()?.let { throw it }
        } finally { server.close() }
    }

    private class FakeSocket : RealtimeSocket {
        val messages = Channel<String>(Channel.UNLIMITED)
        val sent = mutableListOf<String>()
        var closes = 0
        private var writing = false
        override val incoming = messages.receiveAsFlow()
        override suspend fun send(text: String) {
            check(!writing)
            writing = true
            yield()
            sent += text
            writing = false
        }
        override suspend fun close() { closes++; messages.close() }
    }
}
