package com.aallam.openai.api.audio

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TestModernAudio {
    @Test
    fun chunkingSupportsAutoAndServerVad() {
        val serializer = TranscriptionChunkingStrategy.serializer()
        assertEquals("\"auto\"", Json.encodeToString(serializer, TranscriptionChunkingStrategy.Auto))
        val vad: TranscriptionChunkingStrategy = TranscriptionChunkingStrategy.ServerVad(threshold = 0.7)
        val json = Json.parseToJsonElement("""{"threshold":0.7,"type":"server_vad"}""")
        assertEquals(json, Json.parseToJsonElement(Json.encodeToString(serializer, vad)))
        assertEquals(vad, Json.decodeFromString(serializer, json.toString()))
    }

    @Test
    fun diarizedResponseRetainsStringIdsSpeakersAndUsage() {
        val json = """{"text":"Hello","duration":1.2,"task":"transcribe","segments":[{"id":"seg_1","speaker":"Alice","start":0.0,"end":1.2,"text":"Hello","type":"transcript.text.segment"}],"usage":{"type":"duration","seconds":2.0}}"""
        val response = Json.decodeFromString<AudioTranscription>(json)
        assertEquals("Alice", response.diarizedSegments?.single()?.speaker)
        assertEquals("seg_1", response.diarizedSegments?.single()?.id)
        assertNull(response.segments)
        assertEquals(2.0, response.usage?.seconds)
        val encoded = Json.encodeToString(response)
        assertEquals("transcript.text.segment", Json.parseToJsonElement(encoded).jsonObject["segments"]?.jsonArray?.single()?.jsonObject?.get("type")?.jsonPrimitive?.content)
        assertEquals(response, Json.decodeFromString<AudioTranscription>(encoded))
    }

    @Test
    fun transcriptionRetainsConfidenceLanguageAndTokenBreakdown() {
        val response = Json.decodeFromString<AudioTranscription>("""{"text":"Hi","languages":[{"code":"en"}],"logprobs":[{"token":"Hi","bytes":[72,105],"logprob":-0.1}],"usage":{"type":"tokens","input_tokens":4,"output_tokens":1,"total_tokens":5,"input_token_details":{"audio_tokens":4,"text_tokens":0}}}""")
        assertEquals("en", response.languages?.single()?.code)
        assertEquals(-0.1, response.logprobs?.single()?.logprob)
        assertEquals(4, response.usage?.inputTokenDetails?.audioTokens)
    }

    @Test
    fun speechOptionsUseCustomVoiceObject() {
        val options = SpeechOptions(instructions = "Speak slowly", customVoice = CustomVoice("voice_123"), streamFormat = SpeechStreamFormat.Sse)
        assertEquals(
            Json.parseToJsonElement("""{"instructions":"Speak slowly","stream_format":"sse","voice":{"id":"voice_123"}}"""),
            Json.parseToJsonElement(Json.encodeToString(options)),
        )
    }
}
