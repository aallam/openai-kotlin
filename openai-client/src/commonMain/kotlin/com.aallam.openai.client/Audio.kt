package com.aallam.openai.client

import com.aallam.openai.api.audio.*
import com.aallam.openai.api.core.RequestOptions
import kotlinx.coroutines.flow.Flow

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

    /**
     * Transcribes audio with modern controls and returns usage, confidence and speaker data.
     * @throws UnsupportedOperationException when a custom client has not implemented this operation.
     */
    public suspend fun transcribe(
        request: TranscriptionRequest,
        options: TranscriptionOptions,
        requestOptions: RequestOptions? = null,
    ): AudioTranscription = throw UnsupportedOperationException("Modern transcription is not implemented")

    /**
     * Streams transcription text, completion and speaker-segment events. Collection starts the request.
     * Cancelling collection releases the response. Model support for streaming varies.
     * @throws UnsupportedOperationException when a custom client has not implemented this operation.
     */
    public fun transcriptions(
        request: TranscriptionRequest,
        options: TranscriptionOptions = TranscriptionOptions(),
        requestOptions: RequestOptions? = null,
    ): Flow<TranscriptionEvent> = throw UnsupportedOperationException("Streaming transcription is not implemented")

    /**
     * Generates complete audio with voice instructions or a custom voice reference.
     * @throws UnsupportedOperationException when a custom client has not implemented this operation.
     */
    public suspend fun createSpeech(
        request: SpeechRequest,
        options: SpeechOptions,
        requestOptions: RequestOptions? = null,
    ): ByteArray = throw UnsupportedOperationException("Modern speech is not implemented")

    /**
     * Streams binary speech audio chunks. Collection starts the request; cancellation closes it.
     * @throws UnsupportedOperationException when a custom client has not implemented this operation.
     */
    public fun speechAudio(
        request: SpeechRequest,
        options: SpeechOptions = SpeechOptions(),
        requestOptions: RequestOptions? = null,
    ): Flow<ByteArray> = throw UnsupportedOperationException("Streaming speech is not implemented")

    /**
     * Streams base64 audio deltas and completion usage using server-sent events.
     * @throws UnsupportedOperationException when a custom client has not implemented this operation.
     */
    public fun speechEvents(
        request: SpeechRequest,
        options: SpeechOptions = SpeechOptions(),
        requestOptions: RequestOptions? = null,
    ): Flow<SpeechEvent> = throw UnsupportedOperationException("Speech events are not implemented")
}
