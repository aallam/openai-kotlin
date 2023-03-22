package com.aallam.openai.client

import com.aallam.openai.api.audio.transcriptionRequest
import com.aallam.openai.api.audio.translationRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.asSource
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
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
    fun transcriptionText() = runTest {
        val speedTalkingUrl = "https://github.com/aallam/sample-data/raw/main/openai/audio/micro-machines.wav"
        val audioBytes: ByteArray = httpClient.get(speedTalkingUrl).body()
        val request = transcriptionRequest {
            audio = FileSource(name = "micro-machines.wav", source = audioBytes.asSource())
            model = ModelId("whisper-1")
        }
        val transcription = openAI.transcriptionText(request)
        assertTrue { transcription.text.isNotEmpty() }
    }

    @Test
    fun transcriptionJson() = runTest {
        val speedTalkingUrl = "https://github.com/aallam/sample-data/raw/main/openai/audio/micro-machines.wav"
        val audioBytes: ByteArray = httpClient.get(speedTalkingUrl).body()
        val request = transcriptionRequest {
            audio = FileSource(name = "micro-machines.wav", source = audioBytes.asSource())
            model = ModelId("whisper-1")
        }
        val transcription = openAI.transcriptionJson(request)
        assertTrue { transcription.text.isNotEmpty() }
        assertNull(transcription.language)
        assertNull(transcription.duration)
        assertNull(transcription.segments)
    }

    @Test
    fun transcriptionJsonVerbose() = runTest {
        val speedTalkingUrl = "https://github.com/aallam/sample-data/raw/main/openai/audio/micro-machines.wav"
        val audioBytes: ByteArray = httpClient.get(speedTalkingUrl).body()
        val request = transcriptionRequest {
            audio = FileSource(name = "micro-machines.wav", source = audioBytes.asSource())
            model = ModelId("whisper-1")
        }
        val transcription = openAI.transcriptionJsonVerbose(request)
        assertTrue { transcription.text.isNotEmpty() }
        assertEquals(transcription.language, "english")
        assertEquals(transcription.duration, 29.88)
        assertTrue { transcription.segments?.isNotEmpty() ?: false }
    }

    @Test
    fun transcriptionVTT() = runTest {
        val speedTalkingUrl = "https://github.com/aallam/sample-data/raw/main/openai/audio/micro-machines.wav"
        val audioBytes: ByteArray = httpClient.get(speedTalkingUrl).body()
        val request = transcriptionRequest {
            audio = FileSource(name = "micro-machines.wav", source = audioBytes.asSource())
            model = ModelId("whisper-1")
        }
        val transcription = openAI.transcriptionVTT(request)
        assertTrue { transcription.vtt.startsWith("WEBVTT") }
    }

    @Test
    fun transcriptionSRT() = runTest {
        val speedTalkingUrl = "https://github.com/aallam/sample-data/raw/main/openai/audio/micro-machines.wav"
        val audioBytes: ByteArray = httpClient.get(speedTalkingUrl).body()
        val request = transcriptionRequest {
            audio = FileSource(name = "micro-machines.wav", source = audioBytes.asSource())
            model = ModelId("whisper-1")
        }
        val transcription = openAI.transcriptionSRT(request)
        assertTrue { transcription.srt.startsWith("1") }
    }

   @Test
   fun translation() = runTest {
       val multilingualUrl = "https://github.com/aallam/sample-data/raw/main/openai/audio/multilingual.wav"
       val audioBytes: ByteArray = httpClient.get(multilingualUrl).body()
       val request = translationRequest {
           audio = FileSource(name = "multilingual.wav", source = audioBytes.asSource())
           model = ModelId("whisper-1")
       }
       val translation = openAI.translation(request)
       assertTrue { translation.text.isNotEmpty() }
       assertTrue {
           translation.text.startsWith(
               "Whisper is an automatic recognition system of speech",
               ignoreCase = true,
           )
       }
   }
}
