package com.aallam.openai.sample.jvm

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.audio.TranslationRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.ktor.util.*
import okio.FileSystem
import okio.Path.Companion.toPath

@OptIn(BetaOpenAI::class)
suspend fun whisper(openAI: OpenAI) {
    println("\n>️ Create transcription...")
    val transcriptionRequest = TranscriptionRequest(
        audio = FileSource(path = "micro-machines.wav".toPath(), fileSystem = FileSystem.RESOURCES),
        model = ModelId("whisper-1"),
    )
    val transcription = openAI.transcription(transcriptionRequest)
    println(transcription)

    println("\n>️ Create translation...")
    val translationRequest = TranslationRequest(
        audio = FileSource(path = "multilingual.wav".toPath(), fileSystem = FileSystem.RESOURCES),
        model = ModelId("whisper-1"),
    )
    val translation = openAI.translation(translationRequest)
    println(translation)
}

@OptIn(BetaOpenAI::class)
suspend fun whisperWithHeaders(openAI: OpenAI) {
    println("\n>️ Create transcription with headers...")
    val transcriptionRequest = TranscriptionRequest(
        audio = FileSource(path = "micro-machines.wav".toPath(), fileSystem = FileSystem.RESOURCES),
        model = ModelId("whisper-1"),
    )
    val transcription = openAI.transcriptionHeaders(transcriptionRequest)
    println("Transcription result: ${transcription.first.text}. headers: ${transcription.second.toMap()}")

    println("\n>️ Create translation with headers...")
    val translationRequest = TranslationRequest(
        audio = FileSource(path = "multilingual.wav".toPath(), fileSystem = FileSystem.RESOURCES),
        model = ModelId("whisper-1"),
    )
    val translation = openAI.translationHeaders(translationRequest)
    println(translation)
}
