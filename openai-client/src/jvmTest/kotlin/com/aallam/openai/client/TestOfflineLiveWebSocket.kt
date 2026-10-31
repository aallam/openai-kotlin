package com.aallam.openai.client

import com.aallam.openai.api.live.*
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.api.LiveApi
import com.aallam.openai.client.internal.createHttpClient
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.security.MessageDigest
import java.util.Base64
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.test.*

class TestOfflineLiveWebSocket {
    @Test
    fun primarySidebandAndForkHandshakePathsAndInitialEventsReachActualSocket() = runTest {
        val server = ServerSocket(0, 3, InetAddress.getByName("127.0.0.1")).apply { soTimeout = 5000 }
        val failure = AtomicReference<Throwable?>()
        val paths = listOf("/gateway/v1/live/sessions", "/gateway/v1/live/sessions/live_source/attach", "/gateway/v1/live/sessions/live_source/fork")
        val worker = thread(isDaemon = true) {
            try {
                paths.forEachIndexed { index, path ->
                    server.accept().use { socket ->
                        socket.soTimeout = 5000
                        val input = socket.getInputStream().buffered()
                        val output = socket.getOutputStream()
                        val headers = StringBuilder()
                        while (!headers.endsWith("\r\n\r\n")) {
                            val byte = input.read()
                            check(byte >= 0) { "Incomplete handshake" }
                            headers.append(byte.toChar())
                        }
                        assertEquals("GET $path HTTP/1.1", headers.lineSequence().first())
                        val key = headers.lineSequence().first { it.startsWith("Sec-WebSocket-Key:", ignoreCase = true) }.substringAfter(':').trim()
                        val digest = MessageDigest.getInstance("SHA-1").digest((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").toByteArray())
                        output.write("HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Accept: ${Base64.getEncoder().encodeToString(digest)}\r\n\r\n".toByteArray())
                        output.flush()
                        val first = readFrame(input)
                        assertEquals(1, first.first)
                        val event = Json.parseToJsonElement(first.second.decodeToString()).jsonObject
                        assertEquals(if (index == 1) "session.update" else "session.start", event.getValue("type").jsonPrimitive.content)
                        if (index == 0) assertEquals("gpt-live-1", event.getValue("session").jsonObject.getValue("model").jsonPrimitive.content)
                        if (index == 2) assertEquals(buildJsonObject {}, event["session"])
                        writeFrame(output, 1, """{"type":"session.started","session":{"id":"live_local"},"future":true}""".toByteArray())
                        assertEquals(8, readFrame(input).first)
                        writeFrame(output, 8, byteArrayOf())
                    }
                }
            } catch (error: Throwable) { failure.set(error) }
        }
        val transport = HttpTransport(createHttpClient(OpenAIConfig(
            token = "offline", engine = OkHttp.create(),
            host = OpenAIHost("http://127.0.0.1:${server.localPort}/gateway/v1/"),
            retry = RetryStrategy(maxRetries = 0), logging = LoggingConfig(LogLevel.None),
        )))
        try {
            val api = LiveApi(transport)
            val primary = api.liveSession(LiveSessionConfig(ModelId("gpt-live-1")), secure = false) { events.take(1).toList().single() }
            assertEquals("live_local", primary.sessionId)
            assertNotNull(primary.payload["future"])
            api.attachLiveSession("live_source", secure = false) {
                send(LiveEvent.updateSession(buildJsonObject {}))
                assertEquals("session.started", events.take(1).toList().single().type)
            }
            api.forkLiveConnection("live_source", secure = false) { assertEquals("live_local", events.take(1).toList().single().sessionId) }
            worker.join(6000)
            assertFalse(worker.isAlive)
            failure.get()?.let { throw it }
        } finally {
            transport.close()
            server.close()
        }
    }

    private fun readFrame(input: InputStream): Pair<Int, ByteArray> {
        val first = input.read()
        check(first >= 0) { "Unexpected end of WebSocket" }
        val second = input.read()
        check(second and 0x80 != 0) { "Client frames must be masked" }
        val length = when (val size = second and 0x7f) {
            126 -> (input.read() shl 8) or input.read()
            127 -> error("Unexpected fixture frame length")
            else -> size
        }
        val mask = ByteArray(4) { input.read().toByte() }
        val data = ByteArray(length) { index -> (input.read() xor mask[index % 4].toInt()).toByte() }
        return (first and 0x0f) to data
    }

    private fun writeFrame(output: OutputStream, opcode: Int, data: ByteArray) {
        require(data.size < 126)
        output.write(byteArrayOf((0x80 or opcode).toByte(), data.size.toByte()))
        output.write(data)
        output.flush()
    }
}
