package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.exception.PermissionException
import com.aallam.openai.api.exception.OpenAIException
import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.provenance.ProvenanceResult
import com.aallam.openai.client.internal.api.ContentProvenanceApi
import com.aallam.openai.client.internal.api.SafetyApi
import com.aallam.openai.client.internal.createHttpClient
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.io.Buffer
import kotlinx.io.RawSource
import java.io.IOException
import kotlin.test.*
import kotlin.time.Duration.Companion.milliseconds

class TestOfflineSafetyAndProvenance {
    @Test
    fun bothSafetyResourcesAndMultipartProvenancePreserveTheContract() = runTest {
        val operations = mutableListOf<Pair<HttpMethod, String>>()
        val payload = byteArrayOf(0, 1, -1, 127)
        var closed = false
        val buffer = Buffer().apply { write(payload) }
        val source = object : RawSource by buffer {
            override fun close() { closed = true }
        }
        val options = RequestOptions(headers = mapOf("X-Test" to "safety"), urlParameters = mapOf("trace" to "yes"))
        val engine = MockEngine { request ->
            val path = request.url.encodedPath
            operations += request.method to path
            assertEquals("safety", request.headers["X-Test"])
            assertEquals("yes", request.url.parameters["trace"])
            when (path) {
                "/v1/safety/alerts/alert_1" -> {
                    assertEquals(HttpMethod.Get, request.method)
                    respond(alertJson, headers = jsonHeaders)
                }
                "/v1/safety/cases/C-1" -> {
                    assertEquals(HttpMethod.Get, request.method)
                    respond(caseJson, headers = jsonHeaders)
                }
                "/v1/content_provenance_checks" -> {
                    assertEquals(HttpMethod.Post, request.method)
                    assertTrue(request.body.contentType!!.match(ContentType.MultiPart.FormData))
                    val bytes = request.body.toByteArray()
                    val text = bytes.decodeToString()
                    val disposition = text.lineSequence().first { it.startsWith("Content-Disposition:", ignoreCase = true) }
                    val parameters = ContentDisposition.parse(disposition.substringAfter(':').trim()).parameters
                    assertEquals("file", parameters.single { it.name == "name" }.value)
                    assertEquals("fixture.png", parameters.single { it.name == "filename" }.value)
                    assertContains(text, "Content-Type: image/png")
                    assertTrue(bytes.asList().windowed(payload.size).contains(payload.asList()))
                    respond(provenanceJson, headers = jsonHeaders)
                }
                else -> error("Unexpected route $path")
            }
        }
        withClients(engine) { safety, provenance ->
            val alert = safety.safetyAlert("alert_1", options)
            assertTrue(alert.requestPaused)
            assertNull(alert.reason)
            assertEquals("temporary explanation", alert.detailedExplanation)
            val case = safety.safetyCase("C-1", options)
            assertEquals("entity-1", case.entityIdentifier)
            assertEquals("deactivation", case.notice.type)
            assertNull(case.reason)
            val check = provenance.checkContentProvenance(FileSource("fixture.png", source), "image/png", options)
            val c2pa = assertIs<ProvenanceResult.C2pa>(check.results[0])
            assertEquals("trusted", c2pa.validationState)
            assertEquals("OpenAI OpCo, LLC", c2pa.issuer)
            assertEquals("2026-07-27T18:34:12Z", c2pa.generatedAt)
            val synthId = assertIs<ProvenanceResult.SynthId>(check.results[1])
            assertEquals("not_detected", synthId.outcome)
            assertNull(synthId.generatedAt)
            assertNull(synthId.model)
            assertTrue(closed)
        }
        assertEquals(listOf(HttpMethod.Get to "/v1/safety/alerts/alert_1", HttpMethod.Get to "/v1/safety/cases/C-1", HttpMethod.Post to "/v1/content_provenance_checks"), operations)
    }

    @Test
    fun apiErrorsPropagateWithoutInventingMissingResourcesOrDetectionResults() = runTest {
        var status = HttpStatusCode.BadRequest
        withClients(MockEngine { respond("""{"error":{"message":"unavailable","type":"invalid_request_error","param":"file","code":"unavailable"}}""", status, jsonHeaders) }) { safety, provenance ->
            for (current in listOf(HttpStatusCode.BadRequest, HttpStatusCode.NotFound, HttpStatusCode.Forbidden)) {
                status = current
                if (current == HttpStatusCode.Forbidden) {
                    assertFailsWith<PermissionException> { safety.safetyAlert("alert_1") }
                    assertFailsWith<PermissionException> { safety.safetyCase("C-1") }
                    assertFailsWith<PermissionException> { provenance.checkContentProvenance(FileSource("fixture.png", Buffer()), "image/png") }
                } else {
                    assertFailsWith<InvalidRequestException> { safety.safetyAlert("alert_1") }
                    assertFailsWith<InvalidRequestException> { safety.safetyCase("C-1") }
                    val error = assertFailsWith<InvalidRequestException> { provenance.checkContentProvenance(FileSource("fixture.png", Buffer()), "image/png") }
                    assertEquals("file", error.error.detail?.param)
                    assertEquals("unavailable", error.error.detail?.code)
                }
            }
        }
    }

    @Test
    fun resourcePathEscapesAreRejectedBeforeSendingAnyRequest() = runTest {
        var requests = 0
        withClients(MockEngine { requests++; error("Unexpected request") }) { safety, _ ->
            for (id in listOf("", " ", ".", "..", "alert/1", "alert\\1", "alert?x=1", "alert#fragment", "alert%2F1", "alert\n1")) {
                assertFailsWith<IllegalArgumentException> { safety.safetyAlert(id) }
                assertFailsWith<IllegalArgumentException> { safety.safetyCase(id) }
            }
        }
        assertEquals(0, requests)
    }

    @Test
    fun customClientsMayOmitSafetyAndProvenanceCapabilities() = runTest {
        val safety = object : Safety {}
        val provenance = object : ContentProvenance {}
        assertFailsWith<UnsupportedOperationException> { safety.safetyAlert("alert_1") }
        assertFailsWith<UnsupportedOperationException> { safety.safetyCase("C-1") }
        assertFailsWith<UnsupportedOperationException> { provenance.checkContentProvenance(FileSource("fixture.png", Buffer()), "image/png") }
    }

    @Test
    fun oneShotProvenanceUploadsAreNotRetriedAfterConsumedOrUnconsumedFailures() = runTest {
        for (consume in listOf(false, true)) {
            var attempts = 0
            val failure = IOException("upload failed")
            val source = TrackingSource()
            withClients(MockEngine { request ->
                attempts++
                if (consume) request.body.toByteArray()
                throw failure
            }, maxRetries = 2) { _, provenance ->
                val error = assertFailsWith<OpenAIException> { provenance.checkContentProvenance(FileSource("fixture.png", source), "image/png") }
                assertTrue(generateSequence<Throwable>(error) { it.cause }.any { it === failure })
            }
            assertEquals(1, attempts)
            assertEquals(1, source.closes)
        }
        var reads = 0
        withClients(MockEngine { if (++reads == 1) throw IOException("retry read") else respond(alertJson, headers = jsonHeaders) }, maxRetries = 2) { safety, _ ->
            assertEquals("alert_1", safety.safetyAlert("alert_1").id)
        }
        assertEquals(2, reads)
    }

    @Test
    fun consumedUnauthorizedProvenanceUploadsAreNotReplayedByAuthentication() = runTest {
        var attempts = 0
        val source = TrackingSource()
        withClients(MockEngine { request ->
            attempts++
            assertEquals("Bearer test", request.headers[HttpHeaders.Authorization])
            request.body.toByteArray()
            respond("""{"error":{"message":"Invalid API key","type":"invalid_request_error","code":"invalid_api_key"}}""",
                HttpStatusCode.Unauthorized, headersOf(
                    HttpHeaders.ContentType to listOf("application/json"),
                    HttpHeaders.WWWAuthenticate to listOf("Bearer realm=\"openai\""),
                ))
        }, maxRetries = 2) { _, provenance ->
            val error = assertFailsWith<AuthenticationException> { provenance.checkContentProvenance(FileSource("fixture.png", source), "image/png") }
            assertEquals("invalid_api_key", error.error.detail?.code)
        }
        assertEquals(1, attempts)
        assertEquals(1, source.closes)
    }

    @Test
    fun invalidMediaTypesAndEarlyHttpErrorsCloseUnconsumedSources() = runTest {
        var requests = 0
        val invalid = TrackingSource()
        withClients(MockEngine { requests++; error("Unexpected request") }) { _, provenance ->
            assertFailsWith<OpenAIException> { provenance.checkContentProvenance(FileSource("fixture.png", invalid), "invalid") }
        }
        assertEquals(0, requests)
        assertEquals(1, invalid.closes)
        for (status in listOf(HttpStatusCode.BadRequest, HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden)) {
            val source = TrackingSource()
            withClients(MockEngine { respond("""{"error":{"message":"upload refused","type":"invalid_request_error"}}""", status, jsonHeaders) }) { _, provenance ->
                assertFailsWith<OpenAIException> { provenance.checkContentProvenance(FileSource("fixture.png", source), "image/png") }
            }
            assertEquals(1, source.closes)
        }
    }

    @Test
    fun cleanupDoesNotReplaceThePrimaryUploadFailure() = runTest {
        val failure = IOException("engine failed before consuming upload")
        val closeFailure = IllegalStateException("close failed")
        val source = TrackingSource(closeFailure)
        withClients(MockEngine { throw failure }) { _, provenance ->
            val error = assertFailsWith<OpenAIException> { provenance.checkContentProvenance(FileSource("fixture.png", source), "image/png") }
            assertTrue(generateSequence<Throwable>(error) { it.cause }.any { it === failure })
            assertTrue(error.suppressed.any { it === closeFailure })
        }
        assertEquals(1, source.closes)
    }

    @Test
    fun cancellingAnUploadClosesItsUnconsumedSource() = runTest {
        val started = CompletableDeferred<Unit>()
        val source = TrackingSource()
        withClients(MockEngine { started.complete(Unit); awaitCancellation() }) { _, provenance ->
            val job = launch { provenance.checkContentProvenance(FileSource("fixture.png", source), "image/png") }
            started.await()
            job.cancelAndJoin()
            assertTrue(job.isCancelled)
        }
        assertEquals(1, source.closes)
    }

    private class TrackingSource(
        private val closeFailure: Throwable? = null,
        private val delegate: Buffer = Buffer().apply { write(byteArrayOf(0, 1, -1)) },
    ) : RawSource by delegate {
        var closes = 0
        override fun close() { closes++; closeFailure?.let { throw it }; delegate.close() }
    }

    private suspend fun withClients(engine: MockEngine, maxRetries: Int = 0, block: suspend (Safety, ContentProvenance) -> Unit) {
        val transport = HttpTransport(createHttpClient(OpenAIConfig("test", engine = engine, retry = RetryStrategy(maxRetries = maxRetries, maxDelay = 1.milliseconds), logging = LoggingConfig(LogLevel.None))))
        try { block(SafetyApi(transport), ContentProvenanceApi(transport)) } finally { transport.close() }
    }

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val alertJson = """{"id":"alert_1","created_at":1787659200,"error_type":"potentially_unintended_data_access","model":"gpt-6-astra","request_id":"req_1","response_id":"resp_1","request_paused":true,"reason":null,"detailed_explanation":"temporary explanation"}"""
    private val caseJson = """{"id":"C-1","created_at":1787659200,"entity_identifier":"entity-1","notice":{"type":"deactivation"},"reason":null}"""
    private val provenanceJson = """{"created_at":1787659200,"object":"content_provenance_check","results":[{"type":"c2pa","outcome":"detected","validation_state":"trusted","issuer":"OpenAI OpCo, LLC","model":"gpt-image","generated_at":"2026-07-27T18:34:12Z"},{"type":"synthid","outcome":"not_detected","model":null,"generated_at":null}]}"""
}
