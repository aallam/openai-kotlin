package com.aallam.openai.client

import com.aallam.openai.api.audio.transcriptionRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.asSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class TestAudio : TestOpenAI() {

    private val httpClient = HttpClient()

    @Test
    fun transcription() = runTest {
        val speedTalkingUrl = "https://github.com/aallam/sample-data/raw/main/openai/audio/micro-machines.wav"
        val audioBytes: ByteArray = httpClient.get(speedTalkingUrl).body()
        val request = transcriptionRequest {
            audio = FileSource(name = "micro-machines.wav", source = audioBytes.asSource())
            model = ModelId("whisper-1")
        }
        val transcription = openAI.transcription(request)
        assertTrue { transcription.text.isNotEmpty() }
        assertTrue {
            transcription.text.startsWith(
                "This is the Micro Machine Man presenting the most midget miniature motorcade of micro machines",
                ignoreCase = true,
            )
        }
    }

    @Test
    fun translation() = runTest {
        val multilingualUrl = "https://github.com/aallam/sample-data/raw/main/openai/audio/multilingual.wav"
        val audioBytes: ByteArray = httpClient.get(multilingualUrl).body()
        val request = transcriptionRequest {
            audio = FileSource(name = "multilingual.wav", source = audioBytes.asSource())
            model = ModelId("whisper-1")
        }
        val transcription = openAI.transcription(request)
        assertTrue { transcription.text.isNotEmpty() }
        assertTrue {
            transcription.text.startsWith(
                "Whisper is an automatic recognition system of speech",
                ignoreCase = true,
            )
        }
    }
}