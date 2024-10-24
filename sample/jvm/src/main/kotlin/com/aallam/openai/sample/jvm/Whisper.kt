package com.aallam.openai.sample.jvm

import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.audio.TranslationRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI

suspend fun whisper(openAI: OpenAI) {
    println("\n>️ Create transcription...")
    val transcriptionRequest = TranscriptionRequest(
        audio = FileSource(path = Resources.path("micro-machines.wav")),
        model = ModelId("whisper-1"),
    )
    val transcription = openAI.transcription(transcriptionRequest)
    println(transcription)

    println("\n>️ Create translation...")
    val translationRequest = TranslationRequest(
        audio = FileSource(path = Resources.path("multilingual.wav")),
        model = ModelId("whisper-1"),
    )
    val translation = openAI.translation(translationRequest)
    println(translation)
}
