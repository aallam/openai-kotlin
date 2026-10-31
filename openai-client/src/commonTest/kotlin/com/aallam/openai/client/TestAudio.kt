package com.aallam.openai.client

import com.aallam.openai.api.audio.*
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.TestFileSystem
import com.aallam.openai.client.internal.assertMicroMachinesIntroduction
import com.aallam.openai.client.internal.reportAudioUsage
import com.aallam.openai.client.internal.reportSpeechUsage
import com.aallam.openai.client.internal.testFilePath
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestAudio : TestOpenAI() {

    @Test
    fun transcription() = test {
        val request = transcriptionRequest {
            audio = FileSource(path = testFilePath("audio/micro-machines.wav"), fileSystem = TestFileSystem)
            model = ModelId("whisper-1")
        }

        reportAudioUsage("audio.transcriptions", request.model, seconds = 3.1)
        val transcription = openAI.transcription(request)
        assertTrue { transcription.text.isNotEmpty() }
        assertMicroMachinesIntroduction(transcription.text)
    }

    @Test
    fun transcriptionText() = test {
        val request = transcriptionRequest {
            audio = FileSource(path = testFilePath("audio/micro-machines.wav"), fileSystem = TestFileSystem)
            model = ModelId("whisper-1")
            responseFormat = AudioResponseFormat.Text
        }
        reportAudioUsage("audio.transcriptions", request.model, seconds = 3.1)
        val transcription = openAI.transcription(request)
        assertTrue { transcription.text.isNotEmpty() }
    }

    @Test
    fun transcriptionJsonVerbose() = test {
        val request = transcriptionRequest {
            audio = FileSource(path = testFilePath("audio/micro-machines.wav"), fileSystem = TestFileSystem)
            model = ModelId("whisper-1")
            responseFormat = AudioResponseFormat.VerboseJson
        }
        reportAudioUsage("audio.transcriptions", request.model, seconds = 3.1)
        val transcription = openAI.transcription(request)
        assertTrue { transcription.text.isNotEmpty() }
        assertEquals(transcription.language, "english")
        assertEquals(transcription.duration!!, 3.1, absoluteTolerance = 0.1)
        assertTrue { transcription.segments?.isNotEmpty() ?: false }
    }

    @Test
    fun transcriptionWithWordTimestamps() = test {
        val request = transcriptionRequest {
            audio = FileSource(path = testFilePath("audio/micro-machines.wav"), fileSystem = TestFileSystem)
            model = ModelId("whisper-1")
            responseFormat = AudioResponseFormat.VerboseJson
            timestampGranularities = listOf(TimestampGranularity.Word)
        }
        reportAudioUsage("audio.transcriptions", request.model, seconds = 3.1)
        val transcription = openAI.transcription(request)
        assertTrue { transcription.text.isNotEmpty() }
        assertEquals(transcription.language, "english")
        assertEquals(transcription.duration!!, 3.1, absoluteTolerance = 0.1)
        assertEquals(transcription.segments, null)
        assertTrue { transcription.words?.isNotEmpty() ?: false }
    }

    @Test
    fun translation() = test {
        val request = translationRequest {
            audio = FileSource(path = testFilePath("audio/multilingual.wav"), fileSystem = TestFileSystem)
            model = ModelId("whisper-1")
        }
        reportAudioUsage("audio.translations", request.model, seconds = 9.2)
        val translation = openAI.translation(request)
        assertTrue { translation.text.isNotEmpty() }
        assertTrue {
            translation.text.startsWith(
                "Whisper is an automatic recognition system of speech",
                ignoreCase = true,
            )
        }
    }

    @Test
    fun translationText() = test {
        val request = translationRequest {
            audio = FileSource(path = testFilePath("audio/multilingual.wav"), fileSystem = TestFileSystem)
            model = ModelId("whisper-1")
            responseFormat = "text"
        }
        reportAudioUsage("audio.translations", request.model, seconds = 9.2)
        val translation = openAI.translation(request)
        assertTrue { translation.text.isNotEmpty() }
    }

    @Test
    fun translationJsonVerbose() = test {
        val request = translationRequest {
            audio = FileSource(path = testFilePath("audio/multilingual.wav"), fileSystem = TestFileSystem)
            model = ModelId("whisper-1")
            responseFormat = "verbose_json"
        }
        reportAudioUsage("audio.translations", request.model, seconds = 9.2)
        val translation = openAI.translation(request)
        assertTrue { translation.text.isNotEmpty() }
        assertEquals(translation.language, "english")
        assertEquals(translation.duration!!, 9.2, absoluteTolerance = 0.1)
        assertTrue { translation.segments?.isNotEmpty() ?: false }
    }

    @Test
    fun speech() = test {
        val request = speechRequest {
            model = ModelId("tts-1")
            input = "Hello."
            voice = Voice.Alloy
        }
        reportSpeechUsage(request.model, characters = request.input.length)
        val audio = openAI.speech(request)
        assertTrue { audio.isNotEmpty() }
    }
}
