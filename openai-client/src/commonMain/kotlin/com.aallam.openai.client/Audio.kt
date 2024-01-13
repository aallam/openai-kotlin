package com.aallam.openai.client

import com.aallam.openai.api.audio.*
import com.aallam.openai.api.core.RequestOptions

/**
 * Learn how to turn audio into text.
 */
public interface Audio {

    /**
     * Transcribes audio into the input language.
     *
     * @param request transcription request.
     * @param requestOptions request options.
     */
    public suspend fun transcription(
        request: TranscriptionRequest,
        requestOptions: RequestOptions? = null
    ): Transcription

    /**
     * Translates audio into English.
     *
     * @param request translation request.
     * @param requestOptions request options.
     */
    public suspend fun translation(request: TranslationRequest, requestOptions: RequestOptions? = null): Translation

    /**
     * Generates audio from the input text.
     *
     * @param request speech request.
     * @param requestOptions request options.
     */
    public suspend fun speech(request: SpeechRequest, requestOptions: RequestOptions? = null): ByteArray
}
