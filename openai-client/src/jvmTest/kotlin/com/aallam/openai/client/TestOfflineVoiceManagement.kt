package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.api.exception.GenericIOException
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.voice.*
import com.aallam.openai.client.internal.api.VoiceManagementApi
import com.aallam.openai.client.internal.createHttpClient
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.engine.mock.*
import io.ktor.client.request.HttpRequestData
import io.ktor.http.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineVoiceManagement {
    @Test
    fun consentAndSampleVoiceLifecyclePreservesAudioBytesMetadataAndCursors() = runTest {
        val recording = byteArrayOf(0, -1, 127, 4)
        val sample = byteArrayOf(-2, 0, 1, 126)
        val calls = mutableListOf<String>()
        val options = RequestOptions(headers = mapOf("X-Test" to "voices"), urlParameters = mapOf("trace" to "yes"))
        val engine = MockEngine { request ->
            val route = "${request.method.value} ${request.url.encodedPath}"
            calls += route
            assertEquals("voices", request.headers["X-Test"])
            assertEquals("yes", request.url.parameters["trace"])
            when (route) {
                "POST /v1/audio/voice_consents" -> {
                    val parts = multipartParts(request)
                    assertEquals(setOf("name", "language", "recording"), parts.keys)
                    assertEquals("Speaker", parts.getValue("name").text())
                    assertEquals("en-US", parts.getValue("language").text())
                    val audio = parts.getValue("recording")
                    assertContentEquals(recording, audio.bytes)
                    assertEquals("audio/wav", audio.headers["content-type"])
                    assertEquals("consent.wav", audio.filename)
                    respond(consentJson, headers = jsonHeaders)
                }
                "GET /v1/audio/voice_consents/cons_1" -> respond(consentJson, headers = jsonHeaders)
                "POST /v1/audio/voice_consents/cons_1" -> {
                    assertEquals(Json.parseToJsonElement("""{"name":"Renamed"}"""), Json.parseToJsonElement(request.body.toByteArray().decodeToString()))
                    respond(consentJson.replace("Speaker", "Renamed"), headers = jsonHeaders)
                }
                "GET /v1/audio/voice_consents" -> {
                    assertEquals("cons_0", request.url.parameters["after"])
                    assertEquals("2", request.url.parameters["limit"])
                    respond("""{"object":"list","data":[$consentJson],"has_more":true,"first_id":"cons_1","last_id":"cons_1"}""", headers = jsonHeaders)
                }
                "POST /v1/audio/voices" -> {
                    val parts = multipartParts(request)
                    assertEquals(setOf("type", "name", "consent", "audio_sample"), parts.keys)
                    assertEquals("audio_sample", parts.getValue("type").text())
                    assertEquals("Narrator", parts.getValue("name").text())
                    assertEquals("cons_1", parts.getValue("consent").text())
                    val audio = parts.getValue("audio_sample")
                    assertContentEquals(sample, audio.bytes)
                    assertEquals("audio/webm", audio.headers["content-type"])
                    assertEquals("sample.webm", audio.filename)
                    respond(sampleVoiceJson, headers = jsonHeaders)
                }
                "DELETE /v1/audio/voices/voice_1" -> respond("""{"id":"voice_1","object":"audio.voice.deleted","deleted":true}""", headers = jsonHeaders)
                "DELETE /v1/audio/voice_consents/cons_1" -> respond("""{"id":"cons_1","object":"audio.voice_consent","deleted":true}""", headers = jsonHeaders)
                else -> error("Unexpected voice route $route")
            }
        }
        withClient(engine) { client ->
            val consent = client.createVoiceConsent(voiceConsentRequest {
                name = "Speaker"
                language = "en-US"
                this.recording = source("consent.wav", recording)
            }, options)
            assertEquals("audio.voice_consent", consent.objectType)
            assertEquals(1700000000L, consent.createdAt)
            assertEquals(consent, client.voiceConsent(consent.id, options))
            assertEquals("Renamed", client.updateVoiceConsent(consent.id, voiceConsentUpdateRequest { name = "Renamed" }, options).name)
            val page = client.voiceConsents(after = "cons_0", limit = 2, requestOptions = options)
            assertEquals("list", page.objectType)
            assertTrue(page.hasMore)
            assertEquals("cons_1", page.firstId)
            assertEquals("cons_1", page.lastId)
            assertEquals(consent, page.single())
            val voice = client.createVoice(sampleVoiceRequest {
                name = "Narrator"
                audioSample = source("sample.webm", sample)
                this.consent = consent.id
                mediaType = "audio/webm;codecs=opus"
            }, options)
            assertEquals("audio_sample", voice.type)
            assertNull(voice.prompt)
            assertTrue(client.deleteVoice(voice.id, options))
            assertTrue(client.deleteVoiceConsent(consent.id, options))
        }
        assertEquals(listOf(
            "POST /v1/audio/voice_consents", "GET /v1/audio/voice_consents/cons_1",
            "POST /v1/audio/voice_consents/cons_1", "GET /v1/audio/voice_consents",
            "POST /v1/audio/voices", "DELETE /v1/audio/voices/voice_1", "DELETE /v1/audio/voice_consents/cons_1",
        ), calls)
    }

    @Test
    fun consumedSampleAndConsentUploadsAreNotRetriedAndCloseTheirSourcesOnce() = runTest {
        for (sample in listOf(false, true)) {
            var requests = 0
            val source = TrackedSource(byteArrayOf(0, 1, -1, 127))
            withClient(MockEngine { request ->
                requests++
                request.body.toByteArray()
                throw java.io.IOException("connection lost after consuming audio")
            }, maxRetries = 2) { client ->
                val error = assertFailsWith<GenericIOException> { upload(client, sample, source) }
                assertEquals("connection lost after consuming audio", error.cause?.message)
            }
            assertEquals(1, requests)
            assertEquals(1, source.closes)
        }
    }

    @Test
    fun consumedAuthenticationFailuresDoNotReplaySampleOrConsentBodies() = runTest {
        for (sample in listOf(false, true)) {
            var requests = 0
            val source = TrackedSource(byteArrayOf(0, 1, -1, 127))
            withClient(MockEngine { request ->
                requests++
                assertEquals("Bearer test", request.headers[HttpHeaders.Authorization])
                request.body.toByteArray()
                respond("""{"error":{"message":"Invalid API key","type":"authentication_error","code":"invalid_api_key"}}""",
                    HttpStatusCode.Unauthorized, headersOf(
                        HttpHeaders.ContentType to listOf("application/json"),
                        HttpHeaders.WWWAuthenticate to listOf("Bearer"),
                    ))
            }, maxRetries = 2) { client ->
                val error = assertFailsWith<AuthenticationException> { upload(client, sample, source) }
                assertEquals("Invalid API key", error.error.detail?.message)
                assertEquals("invalid_api_key", error.error.detail?.code)
            }
            assertEquals(1, requests)
            assertEquals(1, source.closes)
        }
    }

    @Test
    fun sourcesCloseOnValidationEarlyHttpFailureAndCancellationWithoutReplacingTheFailure() = runTest {
        for (sample in listOf(false, true)) {
            for (failure in listOf("validation", "http", "cancellation")) {
                val closeFailure = IllegalStateException("close failed")
                val source = TrackedSource(byteArrayOf(0, 1), closeFailure)
                var requests = 0
                withClient(MockEngine {
                    requests++
                    if (failure == "cancellation") throw CancellationException("cancelled voice upload")
                    assertEquals("http", failure)
                    respond("""{"error":{"message":"Invalid recording","type":"invalid_request_error","param":"recording","code":"invalid_value"}}""", HttpStatusCode.BadRequest, jsonHeaders)
                }, maxRetries = 2) { client ->
                    val error = when (failure) {
                        "validation" -> assertFailsWith<IllegalArgumentException> { upload(client, sample, source, "audio.unknown") }
                        "http" -> assertFailsWith<InvalidRequestException> { upload(client, sample, source) }.also {
                            assertEquals("recording", it.error.detail?.param)
                        }
                        else -> assertFailsWith<CancellationException> { upload(client, sample, source) }.also {
                            assertEquals("cancelled voice upload", it.message)
                        }
                    }
                    assertTrue(error.suppressedExceptions.any { it === closeFailure })
                }
                assertEquals(if (failure == "validation") 0 else 1, requests)
                assertEquals(1, source.closes)
            }
        }
    }

    @Test
    fun promptCreationSendsJsonAndRetainsTheCreationTypeAndStoredPrompt() = runTest {
        withClient(MockEngine { request ->
            assertEquals(HttpMethod.Post, request.method)
            assertEquals("/v1/audio/voices", request.url.encodedPath)
            assertEquals(ContentType.Application.Json, request.body.contentType?.withoutParameters())
            assertEquals(Json.parseToJsonElement("""{
                "type":"prompt","name":"Warm narrator","prompt":"A calm fictional narrator.",
                "model":"auto","script_hint":"Welcome to this example recording."
            }"""), Json.parseToJsonElement(request.body.toByteArray().decodeToString()))
            respond("""{"id":"voice_2","created_at":1700000001,"name":"Warm narrator","type":"prompt","prompt":"A calm fictional narrator.","object":"audio.voice"}""", headers = jsonHeaders)
        }) { client ->
            val result = client.createVoice(promptVoiceRequest {
                name = "Warm narrator"
                prompt = "A calm fictional narrator."
                model = "auto"
                scriptHint = "Welcome to this example recording."
            })
            assertEquals("prompt", result.type)
            assertEquals("A calm fictional narrator.", result.prompt)
            assertEquals("audio.voice", result.objectType)
        }
    }

    @Test
    fun missingResourcesAreNullableOrFalseButPermissionAndUpdateErrorsPropagate() = runTest {
        var status = HttpStatusCode.NotFound
        withClient(MockEngine {
            respond("""{"error":{"message":"Not available","type":"invalid_request_error","param":"consent","code":"not_found"}}""", status, jsonHeaders)
        }) { client ->
            assertNull(client.voiceConsent("missing"))
            assertFalse(client.deleteVoice("missing"))
            assertFalse(client.deleteVoiceConsent("missing"))
            val update = assertFailsWith<InvalidRequestException> { client.updateVoiceConsent("missing", VoiceConsentUpdateRequest("New")) }
            assertEquals("consent", update.error.detail?.param)
            assertEquals("not_found", update.error.detail?.code)
            status = HttpStatusCode.Unauthorized
            assertFailsWith<AuthenticationException> { client.voiceConsent("missing") }
            assertFailsWith<AuthenticationException> { client.deleteVoice("missing") }
            assertFailsWith<AuthenticationException> { client.deleteVoiceConsent("missing") }
        }
    }

    @Test
    fun unsupportedAudioTypesAndMalformedResourceIdsFailBeforeTransport() = runTest {
        withClient(MockEngine { error("No HTTP request should be made") }) { client ->
            assertFailsWith<IllegalArgumentException> { client.createVoiceConsent(VoiceConsentRequest("Speaker", "en", source("audio.unknown"))) }
            assertFailsWith<IllegalArgumentException> { client.createVoice(VoiceCreateRequest.Sample("Speaker", source("audio.wav"), "cons_1", "application/octet-stream")) }
            assertFailsWith<IllegalArgumentException> { client.voiceConsent("cons_1/other") }
            assertFailsWith<IllegalArgumentException> { client.deleteVoice("voice_1?other") }
        }
    }

    @Test
    fun customClientsCanOmitVoiceManagement() = runTest {
        val custom = object : VoiceManagement {}
        assertFailsWith<UnsupportedOperationException> { custom.createVoice(VoiceCreateRequest.Prompt("Voice", "A fictional narrator.")) }
        assertFailsWith<UnsupportedOperationException> { custom.deleteVoice("voice_1") }
        assertFailsWith<UnsupportedOperationException> { custom.createVoiceConsent(VoiceConsentRequest("Speaker", "en", source("consent.wav"))) }
        assertFailsWith<UnsupportedOperationException> { custom.voiceConsent("cons_1") }
        assertFailsWith<UnsupportedOperationException> { custom.updateVoiceConsent("cons_1", VoiceConsentUpdateRequest("New")) }
        assertFailsWith<UnsupportedOperationException> { custom.voiceConsents() }
        assertFailsWith<UnsupportedOperationException> { custom.deleteVoiceConsent("cons_1") }
    }

    private suspend fun withClient(engine: MockEngine, maxRetries: Int = 0, block: suspend (VoiceManagement) -> Unit) {
        val transport = HttpTransport(createHttpClient(OpenAIConfig("test", engine = engine, logging = LoggingConfig(LogLevel.None), retry = RetryStrategy(maxRetries = maxRetries))))
        try { block(VoiceManagementApi(transport)) } finally { transport.close() }
    }

    private suspend fun upload(client: VoiceManagement, sample: Boolean, source: RawSource, name: String = "audio.wav") {
        val file = FileSource(name, source)
        if (sample) client.createVoice(VoiceCreateRequest.Sample("Voice", file, "cons_1"))
        else client.createVoiceConsent(VoiceConsentRequest("Consent", "en-US", file))
    }

    private class TrackedSource(bytes: ByteArray, private val closeFailure: Throwable? = null) : RawSource {
        private val data = Buffer().apply { write(bytes) }
        var closes = 0
        override fun readAtMostTo(sink: Buffer, byteCount: Long): Long = data.readAtMostTo(sink, byteCount)
        override fun close() { closes++; closeFailure?.let { throw it } }
    }

    private fun source(name: String, bytes: ByteArray = byteArrayOf(0, 1)) = FileSource(name, Buffer().apply { write(bytes) })

    private class Part(val headers: Map<String, String>, val bytes: ByteArray, val filename: String?) {
        fun text(): String = bytes.decodeToString()
    }

    private suspend fun multipartParts(request: HttpRequestData): Map<String, Part> {
        assertTrue(request.body.contentType!!.match(ContentType.MultiPart.FormData))
        val boundary = request.body.contentType!!.parameter("boundary")!!
        val raw = request.body.toByteArray().map { (it.toInt() and 255).toChar() }.joinToString("")
        return raw.split("--$boundary").mapNotNull { segment ->
            val sections = segment.split("\r\n\r\n", limit = 2)
            if (sections.size != 2) return@mapNotNull null
            val headers = sections[0].trim().lineSequence().associate { line ->
                line.substringBefore(':').lowercase() to line.substringAfter(':').trim()
            }
            val disposition = ContentDisposition.parse(headers.getValue("content-disposition"))
            val name = disposition.parameters.single { it.name.equals("name", ignoreCase = true) }.value
            val filename = disposition.parameters.firstOrNull { it.name.equals("filename", ignoreCase = true) }?.value
            val bytes = sections[1].removeSuffix("\r\n").map { it.code.toByte() }.toByteArray()
            name to Part(headers, bytes, filename)
        }.toMap()
    }

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val consentJson = """{"id":"cons_1","created_at":1700000000,"name":"Speaker","language":"en-US","object":"audio.voice_consent"}"""
    private val sampleVoiceJson = """{"id":"voice_1","created_at":1700000001,"name":"Narrator","type":"audio_sample","object":"audio.voice"}"""
}
