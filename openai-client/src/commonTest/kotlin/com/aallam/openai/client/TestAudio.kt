package com.aallam.openai.client

import com.aallam.openai.api.audio.*
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.TestFileSystem
import com.aallam.openai.client.internal.testFilePath
import okio.FileSystem
import okio.Path.Companion.toPath
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

        val transcription = openAI.transcription(request)
        assertTrue { transcription.text.isNotEmpty() }
        assertTrue {
            transcription.text.startsWith(
                "This is the Micromachine Man presenting the most midget miniature motorcade of micromachines",
                ignoreCase = true,
            )
        }
    }

    @Test
    fun transcriptionText() = test {
        val request = transcriptionRequest {
            audio = FileSource(path = testFilePath("audio/micro-machines.wav"), fileSystem = TestFileSystem)
            model = ModelId("whisper-1")
            responseFormat = AudioResponseFormat.Text
        }
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
        val transcription = openAI.transcription(request)
        assertTrue { transcription.text.isNotEmpty() }
        assertEquals(transcription.language, "english")
        assertEquals(transcription.duration!!, 29.88, absoluteTolerance = 0.1)
        assertTrue { transcription.segments?.isNotEmpty() ?: false }
    }

    @Test
    fun translation() = test {
        val request = translationRequest {
            audio = FileSource(path = testFilePath("audio/multilingual.wav"), fileSystem = TestFileSystem)
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

    @Test
    fun translationText() = test {
        val request = translationRequest {
            audio = FileSource(path = testFilePath("audio/multilingual.wav"), fileSystem = TestFileSystem)
            model = ModelId("whisper-1")
            responseFormat = "text"
        }
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
        val translation = openAI.translation(request)
        assertTrue { translation.text.isNotEmpty() }
        assertEquals(translation.language, "english")
        assertEquals(translation.duration!!, 42.06, absoluteTolerance = 0.1)
        assertTrue { translation.segments?.isNotEmpty() ?: false }
    }

    @Test
    fun speech() = test {
        val request = speechRequest {
            model = ModelId("tts-1")
            input = "The quick brown fox jumped over the lazy dog."
            voice = Voice.Alloy
        }
        val audio = openAI.speech(request)
        assertTrue { audio.isNotEmpty() }
    }
}
