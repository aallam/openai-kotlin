package com.aallam.openai.client

import com.aallam.openai.client.internal.createHttpClient
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.url
import io.ktor.http.URLProtocol
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import java.net.InetAddress
import java.net.ServerSocket
import java.security.MessageDigest
import java.util.Base64
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class TestOfflineWebSocketTransport {
    @Test
    fun configuredBasePathPortAndQuerySurviveHandshakeAndSocketCloses() = runTest {
        val server = ServerSocket(0, 1, InetAddress.getLoopbackAddress()).apply { soTimeout = 5000 }
        val failure = AtomicReference<Throwable?>()
        val worker = thread(isDaemon = true) {
            try {
                server.accept().use { socket ->
                    socket.soTimeout = 5000
                    val input = socket.getInputStream().buffered()
                    val headers = StringBuilder()
                    while (!headers.endsWith("\r\n\r\n")) {
                        val byte = input.read()
                        check(byte >= 0) { "Incomplete handshake" }
                        headers.append(byte.toChar())
                    }
                    assertEquals("GET /gateway/v1/events?model=local-test HTTP/1.1", headers.lineSequence().first())
                    val key = headers.lineSequence().first { it.startsWith("Sec-WebSocket-Key:", ignoreCase = true) }
                        .substringAfter(':').trim()
                    val digest = MessageDigest.getInstance("SHA-1")
                        .digest((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").toByteArray())
                    val accept = Base64.getEncoder().encodeToString(digest)
                    val output = socket.getOutputStream()
                    output.write("HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Accept: $accept\r\n\r\n".toByteArray())
                    output.flush()

                    assertEquals(1, input.read() and 0x0f)
                    val length = input.read()
                    check(length and 0x80 != 0) { "Client frames must be masked" }
                    val mask = ByteArray(4) { input.read().toByte() }
                    val text = ByteArray(length and 0x7f) { index -> (input.read() xor mask[index % 4].toInt()).toByte() }
                    assertEquals("client-event", text.decodeToString())
                    val event = "server-event".toByteArray()
                    output.write(byteArrayOf(0x81.toByte(), event.size.toByte()))
                    output.write(event)
                    output.flush()
                    assertEquals(8, input.read() and 0x0f)
                    output.write(byteArrayOf(0x88.toByte(), 0))
                    output.flush()
                }
            } catch (error: Throwable) { failure.set(error) }
        }
        val config = OpenAIConfig(
            token = "offline",
            host = OpenAIHost("http://127.0.0.1:${server.localPort}/gateway/v1/"),
            engine = OkHttp.create(),
            retry = RetryStrategy(maxRetries = 0),
        )
        val transport = HttpTransport(createHttpClient(config))
        try {
            val socket = transport.realtimeSocket {
                url(path = "events") {
                    protocol = URLProtocol.WS
                    parameters.append("model", "local-test")
                }
            }
            try {
                socket.send("client-event")
                assertEquals("server-event", socket.incoming.first())
            } finally { socket.close() }
            worker.join(6000)
            assertFalse(worker.isAlive)
            failure.get()?.let { throw it }
        } finally {
            transport.close()
            server.close()
        }
    }
}
