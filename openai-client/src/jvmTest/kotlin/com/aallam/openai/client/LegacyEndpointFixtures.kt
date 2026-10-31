package com.aallam.openai.client

import com.aallam.openai.api.logging.LogLevel
import com.sun.net.httpserver.HttpServer
import io.ktor.client.engine.okhttp.OkHttp
import java.net.InetSocketAddress
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicInteger
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// The removed Assistants API remains covered as an SDK wire contract, without live requests.
internal data class LegacyExchange(
    val method: String,
    val path: String,
    val response: String,
    val body: String? = null,
    val status: HttpStatusCode = HttpStatusCode.OK,
    val streaming: Boolean = false,
)

internal class LegacyClientScope(val openAI: OpenAI)

internal fun legacyTest(vararg exchanges: LegacyExchange, block: suspend LegacyClientScope.() -> Unit) = runTest {
    val index = AtomicInteger()
    fun exchange(method: String, path: String, beta: String?, body: String): LegacyExchange {
        assertTrue(index.get() < exchanges.size, "Unexpected request: $method $path")
        val expected = exchanges[index.getAndIncrement()]
        assertEquals(expected.method, method)
        assertEquals(expected.path, path)
        assertEquals("assistants=v2", beta)
        expected.body?.let { expectedBody ->
            val actual = Json.parseToJsonElement(body).jsonObject
            Json.parseToJsonElement(expectedBody).jsonObject.forEach { (name, value) ->
                assertEquals(value, actual[name], "Request field $name")
            }
        }
        return expected
    }
    if (exchanges.any { it.streaming }) {
        // Ktor 3.0 MockEngine has no SSE capability; exercise that transport on loopback.
        val failure = AtomicReference<Throwable>()
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/") { request ->
            try {
                val expected = exchange(request.requestMethod, request.requestURI.path,
                    request.requestHeaders.getFirst("OpenAI-Beta"), request.requestBody.bufferedReader().readText())
                val bytes = expected.response.toByteArray()
                request.responseHeaders.add("Content-Type", if (expected.streaming) "text/event-stream" else "application/json")
                request.sendResponseHeaders(expected.status.value, bytes.size.toLong())
                request.responseBody.use { it.write(bytes) }
            } catch (cause: Throwable) {
                failure.set(cause)
                request.sendResponseHeaders(500, -1)
            } finally { request.close() }
        }
        server.start()
        try {
            OpenAI(OpenAIConfig("offline-test", engine = OkHttp.create(),
                host = OpenAIHost("http://127.0.0.1:${server.address.port}/v1/"),
                retry = RetryStrategy(maxRetries = 0), logging = LoggingConfig(LogLevel.None))).use { client ->
                LegacyClientScope(client).block()
            }
            failure.get()?.let { throw it }
        } finally { server.stop(0) }
    } else {
        val engine = MockEngine { request ->
            val expected = exchange(request.method.value, request.url.encodedPath, request.headers["OpenAI-Beta"],
                (request.body as? TextContent)?.text.orEmpty())
            respond(expected.response, expected.status, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        OpenAI(OpenAIConfig("offline-test", engine = engine, retry = RetryStrategy(maxRetries = 0),
            logging = LoggingConfig(LogLevel.None))).use { client -> LegacyClientScope(client).block() }
    }
    assertEquals(exchanges.size, index.get(), "Every expected operation should execute")
}

private const val assistantPath = "/v1/assistants/asst_1"
private const val threadPath = "/v1/threads/thread_1"
private const val runPath = "$threadPath/runs/run_1"
private const val originalMetadata = "{}"
private const val updatedMetadata = """{"modified":"true","user":"aallam"}"""
private const val schemaFormat = """{"type":"json_schema","json_schema":{"name":"TestSchema","description":"A test schema","schema":{"type":"object","properties":{"name":{"type":"string"}},"required":["name"],"additionalProperties":false},"strict":true}}"""

private fun assistant(name: String = "Math Tutor", schema: Boolean = false, format: String? = null): String =
    """{"id":"asst_1","object":"assistant","created_at":1,"name":"$name","model":"${if (schema) "gpt-4o-mini" else "gpt-4o"}","tools":${if (schema) "[]" else "[{\"type\":\"code_interpreter\"}]"},"metadata":{},"response_format":${format ?: if (schema) schemaFormat else "\"text\""}}"""

internal fun assistantLifecycle(schema: Boolean = false): Array<LegacyExchange> {
    val name = if (schema) "Schema Assistant" else "Math Tutor"
    val updatedName = if (schema) "Updated Schema Assistant" else "Super Math Tutor"
    val initial = assistant(name, schema)
    val body = """{"name":"$name","model":"${if (schema) "gpt-4o-mini" else "gpt-4o"}","response_format":${if (schema) schemaFormat else "{\"type\":\"text\"}"}${if (schema) "" else ",\"tools\":[{\"type\":\"code_interpreter\"}]"}}"""
    return arrayOf(
        LegacyExchange("POST", "/v1/assistants", initial, body),
        LegacyExchange("GET", assistantPath, initial),
        LegacyExchange("GET", "/v1/assistants", """{"object":"list","data":[$initial]}"""),
        LegacyExchange("POST", assistantPath, assistant(updatedName, schema, "\"auto\""),
            """{"name":"$updatedName","response_format":"auto"}"""),
        LegacyExchange("DELETE", assistantPath, deleted("asst_1", "assistant")),
        LegacyExchange("GET", assistantPath, """{"error":{"message":"missing","type":"invalid_request_error"}}""",
            status = HttpStatusCode.NotFound),
    )
}

private fun thread(metadata: String = originalMetadata, resources: Boolean = false): String =
    """{"id":"thread_1","object":"thread","created_at":1,"metadata":$metadata${if (resources) ",\"tool_resources\":{\"code_interpreter\":{\"file_ids\":[]}}" else ""}}"""

private fun deleted(id: String, type: String): String =
    """{"id":"$id","object":"$type.deleted","deleted":true}"""

internal fun threadLifecycle(): Array<LegacyExchange> = arrayOf(
    LegacyExchange("POST", "/v1/threads", thread()),
    LegacyExchange("GET", threadPath, thread(resources = true)),
    LegacyExchange("POST", threadPath, thread(updatedMetadata), """{"metadata":$updatedMetadata}"""),
    LegacyExchange("DELETE", threadPath, deleted("thread_1", "thread")),
)

private fun message(text: String, metadata: String = originalMetadata): String =
    """{"id":"msg_1","created_at":1,"thread_id":"thread_1","role":"user","content":[{"type":"text","text":{"value":"$text","annotations":[]}}],"metadata":$metadata}"""

internal fun messageLifecycle(): Array<LegacyExchange> {
    val text = "How does AI work? Explain it in simple terms."
    return arrayOf(
        LegacyExchange("POST", "/v1/threads", thread()),
        LegacyExchange("POST", "$threadPath/messages", message(text), """{"role":"user","content":"$text"}"""),
        LegacyExchange("GET", "$threadPath/messages/msg_1", message(text)),
        LegacyExchange("POST", "$threadPath/messages/msg_1", message(text, updatedMetadata), """{"metadata":$updatedMetadata}"""),
        LegacyExchange("GET", "$threadPath/messages", """{"object":"list","data":[${message(text, updatedMetadata)}]}"""),
        LegacyExchange("DELETE", threadPath, deleted("thread_1", "thread")),
    )
}

private fun run(status: String): String =
    """{"id":"run_1","created_at":1,"thread_id":"thread_1","assistant_id":"asst_1","status":"$status","model":"gpt-4o"}"""

private fun runEvents(combined: Boolean): String = buildString {
    if (combined) append("event: thread.created\ndata: ${thread()}\n\n")
    append("event: thread.run.created\ndata: ${run("in_progress")}\n\n")
    append("event: thread.run.completed\ndata: ${run("completed")}\n\n")
    append("data: [DONE]\n\n")
}

internal fun runLifecycle(streaming: Boolean = false, combined: Boolean = false): Array<LegacyExchange> = buildList {
    add(LegacyExchange("POST", "/v1/assistants", assistant(),
        """{"name":"Math Tutor","tools":[{"type":"code_interpreter"}],"model":"gpt-4o"}"""))
    if (!combined) {
        add(LegacyExchange("POST", "/v1/threads", thread()))
        add(LegacyExchange("POST", "$threadPath/messages", message("solve me 1 + 1"),
            """{"role":"user","content":"solve me 1 + 1","metadata":{}}"""))
    }
    val requestBody = if (combined)
        """{"assistant_id":"asst_1","thread":{"messages":[{"role":"user","content":"solve 1 + 2"}]}}"""
    else """{"assistant_id":"asst_1"}"""
    val body = Json.parseToJsonElement(requestBody).jsonObject.toMutableMap().apply {
        if (streaming) put("stream", kotlinx.serialization.json.JsonPrimitive(true))
    }.let { kotlinx.serialization.json.JsonObject(it) }.toString()
    add(LegacyExchange("POST", if (combined) "/v1/threads/runs" else "$threadPath/runs",
        if (streaming) runEvents(combined) else run("in_progress"), body, streaming = streaming))
    if (combined) {
        val steps = if (streaming)
            """{"id":"step_1","created_at":1,"run_id":"run_1","assistant_id":"asst_1","thread_id":"thread_1","type":"message_creation","status":"completed","step_details":{"type":"message_creation","message_creation":{"message_id":"msg_1"}}}"""
        else ""
        add(LegacyExchange("GET", "$runPath/steps", """{"object":"list","data":[$steps]}"""))
    } else {
        add(LegacyExchange("GET", runPath, run(if (streaming) "completed" else "in_progress")))
        add(LegacyExchange("GET", "$threadPath/runs", """{"object":"list","data":[${run(if (streaming) "completed" else "in_progress")}]}"""))
    }
    if (!streaming) add(LegacyExchange("POST", "$runPath/cancel", run("cancelled")))
    add(LegacyExchange("DELETE", threadPath, deleted("thread_1", "thread")))
    add(LegacyExchange("DELETE", assistantPath, deleted("asst_1", "assistant")))
}.toTypedArray()
