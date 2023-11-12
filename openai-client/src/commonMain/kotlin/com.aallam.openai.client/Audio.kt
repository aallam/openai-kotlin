package com.aallam.openai.client

import com.aallam.openai.api.audio.Transcription
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.audio.Translation
import com.aallam.openai.api.audio.TranslationRequest

/**
 * Learn how to turn audio into text.
 */
public interface Audio {

    /**
     * Transcribes audio into the input language.
     */
    public suspend fun transcription(request: TranscriptionRequest): Transcription

    /**
     * Translates audio into English.
     */
    public suspend fun translation(request: TranslationRequest): Translation
}
