package com.aallam.openai.api.audio

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.model.ModelId

/**
 * Request to translate an audio into english.
 */
public class TranslationRequest(
    /**
     * The audio file to translate, in one of these formats: mp3, mp4, mpeg, mpga, m4a, wav, or webm.
     */
    public val audio: FileSource,
    /**
     * ID of the model to use. Only `whisper-1` is currently available.
     */
    public val model: ModelId,

    /**
     * An optional text to guide the model's style or continue a previous audio segment.
     * The [prompt](https://platform.openai.com/docs/guides/speech-to-text/prompting) should be in English.
     */
    public val prompt: String? = null,

    /**
     * The format of the transcript output, in one of these options: json, text, srt, verbose_json, or vtt.
     *
     * Default: json
     */
    public val responseFormat: String? = null,

    /**
     * The sampling temperature, between 0 and 1. Higher values like 0.8 will make the output more random, while lower
     * values like 0.2 will make it more focused and deterministic. If set to 0, the model will use
     * [log probability](https://en.wikipedia.org/wiki/Log_probability) to automatically increase the temperature until
     * certain thresholds are hit.
     *
     * Default: 0
     */
    public val temperature: Double? = null
)


/**
 * Creates a translation request.
 */
public fun translationRequest(block: TranslationRequestBuilder.() -> Unit): TranslationRequest =
    TranslationRequestBuilder().apply(block).build()

@OpenAIDsl
public class TranslationRequestBuilder {

    /**
     * The audio file to transcribe, in one of these formats: mp3, mp4, mpeg, mpga, m4a, wav, or webm.
     */
    public var audio: FileSource? = null

    /**
     * ID of the model to use.
     * Only `whisper-1` is currently available.
     */
    public var model: ModelId? = null

    /**
     * An optional text to guide the model's style or continue a previous audio segment.
     * The [prompt](https://platform.openai.com/docs/guides/speech-to-text/prompting) should match the audio language.
     */
    public var prompt: String? = null

    /**
     * The format of the transcript output, in one of these options: json, text, srt, verbose_json, or vtt.
     */
    public var responseFormat: String? = null

    /**
     * The sampling temperature, between 0 and 1. Higher values like 0.8 will make the output more random, while lower
     * values like 0.2 will make it more focused and deterministic. If set to 0, the model will use
     * [log probability](https://en.wikipedia.org/wiki/Log_probability) to automatically increase the temperature until
     * certain thresholds are hit.
     */
    public var temperature: Double? = null

    /**
     * Builder of [TranslationRequest] instances.
     */
    public fun build(): TranslationRequest = TranslationRequest(
        audio = requireNotNull(audio) { "audio is required" },
        model = requireNotNull(model) { "model is required" },
        prompt = prompt,
        responseFormat = responseFormat,
        temperature = temperature,
    )
}
