package com.aallam.openai.client

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.*
import io.ktor.http.*

/**
 * Learn how to turn audio into text.
 */
public interface Audio {

    /**
     * Transcribes audio into the input language.
     */
    @BetaOpenAI
    public suspend fun transcription(request: TranscriptionRequest): Transcription

    @BetaOpenAI
    public suspend fun transcriptionHeaders(request: TranscriptionRequest): Pair<Transcription, Headers>

    /**
     * Translates audio into English.
     */
    @BetaOpenAI
    public suspend fun translation(request: TranslationRequest): Translation

    @BetaOpenAI
    public suspend fun translationHeaders(request: TranslationRequest): Pair<Translation, Headers>
}
