package com.aallam.openai.client

import com.aallam.openai.api.audio.*
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.exception.OpenAIHttpException
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.createHttpClient
import com.aallam.openai.client.internal.api.AudioApi
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.writeStringUtf8
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.test.runTest
import kotlinx.io.Buffer
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineAudio {
    @Test
    fun legacyInferredAudioReferencesRemainUnambiguous() = runTest {
        val client = client(MockEngine { request ->
            if (request.url.encodedPath.endsWith("/transcriptions")) respond("legacy text", headers = headersOf(HttpHeaders.ContentType, "text/plain"))
            else respond(byteArrayOf(1, 2), headers = headersOf(HttpHeaders.ContentType, "audio/mpeg"))
        })
        try {
            val transcription = client::transcription
            val speech = client::speech
            assertEquals("legacy text", transcription(request(AudioResponseFormat.Text), null).text)
            assertContentEquals(byteArrayOf(1, 2), speech(speechRequest(), null))
        } finally { client.close() }
    }

    @Test
    fun modernTranscriptionSendsArraysAndVadControls() = runTest {
        val client = client(MockEngine { request ->
            val body = request.body.toByteArray().decodeToString()
            for (field in listOf("chunking_strategy[type]", "chunking_strategy[threshold]", "known_speaker_names[]", "known_speaker_references[]", "languages[]", "include[]", "keywords[]")) {
                assertContains(body, "name=\"$field\"")
            }
            assertContains(body, "server_vad")
            assertContains(body, "Alice")
            respond("""{"text":"hello","languages":[{"code":"en"}],"usage":{"type":"tokens","total_tokens":2}}""", headers = jsonHeaders)
        })
        try {
            val result = client.transcribe(request(), TranscriptionOptions(
                chunkingStrategy = TranscriptionChunkingStrategy.ServerVad(threshold = 0.7),
                include = listOf("logprobs"), keywords = listOf("Kotlin"), languages = listOf("en", "fr"),
                knownSpeakerNames = listOf("Alice"), knownSpeakerReferences = listOf("data:audio/wav;base64,AA=="),
            ))
            assertEquals("hello", result.text)
            assertEquals(2, result.usage?.totalTokens)
        } finally { client.close() }
    }

    @Test
    fun textOutputRemainsTextWithModernOptions() = runTest {
        val client = client(MockEngine { respond("hello", headers = headersOf(HttpHeaders.ContentType, "text/plain")) })
        try {
            assertEquals("hello", client.transcribe(request(AudioResponseFormat.Text), TranscriptionOptions()).text)
        } finally { client.close() }
    }

    @Test
    fun transcriptionStreamHandlesMultilineRecordsAndCompletion() = runTest {
        val client = client(MockEngine { request ->
            assertEquals("true", multipartFields(request)["stream"])
            respond(":keepalive\n\nevent: transcript.text.delta\ndata: {\"type\":\"transcript.text.delta\",\ndata: \"delta\":\"hello\",\"segment_id\":\"seg_0\"}\n\ndata: {\"type\":\"transcript.text.done\",\"text\":\"hello\"}\n\n", headers = sseHeaders)
        })
        try {
            val events = client.transcriptions(request()).toList()
            assertEquals(listOf("transcript.text.delta", "transcript.text.done"), events.map { it.type })
            assertEquals("hello", events.first().delta)
            assertEquals("seg_0", events.first().segmentId)
        } finally { client.close() }
    }

    @Test
    fun streamErrorsAreNotReportedAsSuccess() = runTest {
        val client = client(MockEngine { respond("data: {\"type\":\"error\",\"message\":\"failed\"}\n\n", headers = sseHeaders) })
        try {
            assertFailsWith<OpenAIHttpException> { client.transcriptions(request()).toList() }
        } finally { client.close() }
    }

    @Test
    fun speechBytesAndCustomVoiceUseModernWireShape() = runTest {
        val client = client(MockEngine { request ->
            val body = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
            assertEquals("voice_123", body["voice"]?.jsonObject?.get("id")?.jsonPrimitive?.content)
            assertEquals("Calmly", body["instructions"]?.jsonPrimitive?.content)
            assertEquals("audio", body["stream_format"]?.jsonPrimitive?.content)
            respond(byteArrayOf(1, 2, 3), headers = headersOf(HttpHeaders.ContentType, "audio/mpeg"))
        })
        try {
            val chunks = client.speechAudio(speechRequest(), SpeechOptions(instructions = "Calmly", customVoice = CustomVoice("voice_123"))).toList()
            assertContentEquals(byteArrayOf(1, 2, 3), chunks.reduce { a, b -> a + b })
        } finally { client.close() }
    }

    @Test
    fun speechEventsPreserveAudioAndUsage() = runTest {
        val client = client(MockEngine { request ->
            assertEquals("sse", Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject["stream_format"]?.jsonPrimitive?.content)
            respond("data: {\"type\":\"speech.audio.delta\",\"audio\":\"AQID\"}\n\ndata: {\"type\":\"speech.audio.done\",\"usage\":{\"total_tokens\":3}}\n\n", headers = sseHeaders)
        })
        try {
            val events = client.speechEvents(speechRequest()).toList()
            assertEquals("AQID", events.first().audio)
            assertEquals(3, events.last().usage?.totalTokens)
        } finally { client.close() }
    }

    @Test
    fun httpErrorsRetainApiExceptionType() = runTest {
        val client = client(MockEngine { respond("""{"error":{"message":"invalid audio"}}""", status = HttpStatusCode.BadRequest, headers = jsonHeaders) })
        try {
            assertFailsWith<InvalidRequestException> { client.transcriptions(request()).toList() }
        } finally { client.close() }
    }

    @Test
    fun stoppingTranscriptionCollectionCancelsTheResponseChannel() = runTest {
        val channel = ByteChannel(autoFlush = true)
        backgroundScope.launch {
            channel.writeStringUtf8("data: {\"type\":\"transcript.text.delta\",\"delta\":\"hello\"}\n\n")
            awaitCancellation()
        }
        val client = client(MockEngine { respond(channel, headers = sseHeaders) })
        try {
            assertEquals("hello", client.transcriptions(request()).take(1).toList().single().delta)
            assertTrue(channel.isClosedForRead)
        } finally { client.close() }
    }

    @Test
    fun stoppingBinaryCollectionCancelsTheResponseChannel() = runTest {
        val channel = ByteChannel(autoFlush = true)
        backgroundScope.launch {
            channel.writeFully(byteArrayOf(1, 2, 3))
            awaitCancellation()
        }
        val client = client(MockEngine { respond(channel, headers = headersOf(HttpHeaders.ContentType, "audio/mpeg")) })
        try {
            assertContentEquals(byteArrayOf(1, 2, 3), client.speechAudio(speechRequest()).take(1).toList().single())
            assertTrue(channel.isClosedForRead)
        } finally { client.close() }
    }

    @Test
    fun transcriptionStreamCanSendFromTransportCallbackDispatcher() = runTest {
        withDispatchedAudio(MockEngine {
            respond("data: {\"type\":\"transcript.text.delta\",\"delta\":\"hello\"}\n\n", headers = sseHeaders)
        }) { audio ->
            assertEquals("hello", audio.transcriptions(request()).toList().single().delta)
        }
    }

    @Test
    fun speechEventsCanSendFromTransportCallbackDispatcher() = runTest {
        withDispatchedAudio(MockEngine {
            respond("data: {\"type\":\"speech.audio.delta\",\"audio\":\"AQID\"}\n\n", headers = sseHeaders)
        }) { audio ->
            assertEquals("AQID", audio.speechEvents(speechRequest()).toList().single().audio)
        }
    }

    @Test
    fun speechBytesCanSendFromTransportCallbackDispatcher() = runTest {
        withDispatchedAudio(MockEngine {
            respond(byteArrayOf(1, 2, 3), headers = headersOf(HttpHeaders.ContentType, "audio/mpeg"))
        }) { audio ->
            val chunks = audio.speechAudio(speechRequest()).toList()
            assertContentEquals(byteArrayOf(1, 2, 3), chunks.reduce { a, b -> a + b })
        }
    }

    private suspend fun withDispatchedAudio(engine: MockEngine, block: suspend (Audio) -> Unit) {
        val transport = HttpTransport(createHttpClient(OpenAIConfig(token = "test", engine = engine, logging = LoggingConfig(logLevel = LogLevel.None))))
        val requester = object : HttpRequester by transport {
            override suspend fun <T : Any> perform(builder: HttpRequestBuilder, block: suspend (HttpResponse) -> T) {
                transport.perform(builder) { response -> withContext(Dispatchers.IO) { block(response) } }
            }
        }
        try {
            block(AudioApi(requester))
        } finally {
            transport.close()
        }
    }

    private suspend fun multipartFields(request: io.ktor.client.request.HttpRequestData): Map<String, String> {
        val boundary = requireNotNull(request.body.contentType?.parameter("boundary"))
        return request.body.toByteArray().decodeToString().split("--$boundary").mapNotNull { part ->
            val sections = part.split("\r\n\r\n", limit = 2)
            if (sections.size != 2) return@mapNotNull null
            val disposition = sections[0].lineSequence().firstOrNull {
                it.substringBefore(':').equals(HttpHeaders.ContentDisposition, ignoreCase = true)
            } ?: return@mapNotNull null
            val name = ContentDisposition.parse(disposition.substringAfter(':').trim()).parameters
                .single { it.name.equals("name", ignoreCase = true) }.value
            name to sections[1].removeSuffix("\r\n")
        }.toMap()
    }

    private fun request(format: AudioResponseFormat? = null) = TranscriptionRequest(
        audio = FileSource("audio.wav", Buffer().apply { write(byteArrayOf(1, 2, 3)) }),
        model = ModelId("gpt-transcribe"), responseFormat = format,
    )
    private fun speechRequest() = SpeechRequest(ModelId("gpt-4o-mini-tts"), "hello", Voice.Alloy)
    private fun client(engine: MockEngine): OpenAI = OpenAI(OpenAIConfig(token = "test", engine = engine, logging = LoggingConfig(logLevel = LogLevel.None), retry = RetryStrategy(maxRetries = 0)))
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val sseHeaders = headersOf(HttpHeaders.ContentType, "text/event-stream")
}
