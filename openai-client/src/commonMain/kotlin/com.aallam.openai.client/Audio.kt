package com.aallam.openai.client

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.*

/**
 * Learn how to turn audio into text.
 */
public interface Audio {

    /**
     * Transcribes audio into the input language.
     */
    @BetaOpenAI
    public suspend fun transcription(request: TranscriptionRequest): Transcription

    /**
     * Transcribes audio into the input language.
     */
    @BetaOpenAI
    public suspend fun transcriptionText(request: TranscriptionRequest): TranscriptionText

    /**
     * Transcribes audio into the input language.
     */
    @BetaOpenAI
    public suspend fun transcriptionJson(request: TranscriptionRequest): TranscriptionJson

    /**
     * Transcribes audio into the input language.
     */
    @BetaOpenAI
    public suspend fun transcriptionJsonVerbose(request: TranscriptionRequest): TranscriptionJson

    /**
     * Transcribes audio into the input language.
     */
    @BetaOpenAI
    public suspend fun transcriptionVTT(request: TranscriptionRequest): TranscriptionVTT

    /**
     * Transcribes audio into the input language.
     */
    @BetaOpenAI
    public suspend fun transcriptionSRT(request: TranscriptionRequest): TranscriptionSRT

    /**
     * Translates audio into English.
     */
    @BetaOpenAI
    public suspend fun translation(request: TranslationRequest): Translation
}
