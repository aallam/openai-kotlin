package com.aallam.openai.client

import com.aallam.openai.api.audio.*
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.exception.OpenAIHttpException
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
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
            val result = client.transcription(request(), TranscriptionOptions(
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
            assertEquals("hello", client.transcription(request(AudioResponseFormat.Text), TranscriptionOptions()).text)
        } finally { client.close() }
    }

    @Test
    fun transcriptionStreamHandlesMultilineRecordsAndCompletion() = runTest {
        val client = client(MockEngine { request ->
            assertContains(request.body.toByteArray().decodeToString(), "name=stream")
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

    private fun request(format: AudioResponseFormat? = null) = TranscriptionRequest(
        audio = FileSource("audio.wav", Buffer().apply { write(byteArrayOf(1, 2, 3)) }),
        model = ModelId("gpt-transcribe"), responseFormat = format,
    )
    private fun speechRequest() = SpeechRequest(ModelId("gpt-4o-mini-tts"), "hello", Voice.Alloy)
    private fun client(engine: MockEngine): OpenAI = OpenAI(OpenAIConfig(token = "test", engine = engine, logging = LoggingConfig(logLevel = LogLevel.None), retry = RetryStrategy(maxRetries = 0)))
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val sseHeaders = headersOf(HttpHeaders.ContentType, "text/event-stream")
}
