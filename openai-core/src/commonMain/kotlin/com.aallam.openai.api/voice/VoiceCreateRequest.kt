@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.aallam.openai.api.voice

import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.file.FileSource
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Creates a voice from either a consented recording or a text description. */
public sealed interface VoiceCreateRequest {
    /** Label for the saved voice. */
    public val name: String

    /** Creates a voice from an audio sample and an existing consent from the same speaker. */
    public data class Sample(
        override val name: String,
        /** Reference audio; uploaded without changing its bytes. */
        public val audioSample: FileSource,
        /** Existing consent recording identifier from the same project. */
        public val consent: String,
        /** Audio MIME type; inferred from the filename when omitted. Codec parameters are removed. */
        public val mediaType: String? = null,
    ) : VoiceCreateRequest

    /** Creates a prompt-generated voice for Live; no recordings or consent resource are required. */
    @Serializable
    public data class Prompt(
        override val name: String,
        /** Nonblank description of the desired voice. */
        public val prompt: String,
        /** Voice creation model, or `auto`; omission uses the server default. */
        public val model: String? = null,
        /** Optional text spoken during creation; it must not be blank. */
        @SerialName("script_hint") public val scriptHint: String? = null,
    ) : VoiceCreateRequest {
        @EncodeDefault private val type: String = "prompt"
    }
}

/** Creates a sample-based voice request. */
public fun sampleVoiceRequest(block: SampleVoiceRequestBuilder.() -> Unit): VoiceCreateRequest.Sample =
    SampleVoiceRequestBuilder().apply(block).build()

/** Builder for a consented sample-based voice request. */
@OpenAIDsl
public class SampleVoiceRequestBuilder {
    /** Label for the saved voice. */
    public var name: String? = null
    /** Reference audio recording. */
    public var audioSample: FileSource? = null
    /** Existing consent recording identifier. */
    public var consent: String? = null
    /** Audio MIME type; inferred from the filename when omitted. */
    public var mediaType: String? = null

    /** Builds a request, requiring a name, sample and consent identifier. */
    public fun build(): VoiceCreateRequest.Sample = VoiceCreateRequest.Sample(
        name = requireNotNull(name) { "name is required" },
        audioSample = requireNotNull(audioSample) { "audioSample is required" },
        consent = requireNotNull(consent) { "consent is required" },
        mediaType = mediaType,
    )
}

/** Creates a prompt-generated voice request for Live. */
public fun promptVoiceRequest(block: PromptVoiceRequestBuilder.() -> Unit): VoiceCreateRequest.Prompt =
    PromptVoiceRequestBuilder().apply(block).build()

/** Builder for a prompt-generated voice request. */
@OpenAIDsl
public class PromptVoiceRequestBuilder {
    /** Label for the saved voice. */
    public var name: String? = null
    /** Description of the desired voice. */
    public var prompt: String? = null
    /** Voice creation model, or `auto`; omission uses the server default. */
    public var model: String? = null
    /** Optional text spoken during creation. */
    public var scriptHint: String? = null

    /** Builds a request, requiring a name and prompt. */
    public fun build(): VoiceCreateRequest.Prompt = VoiceCreateRequest.Prompt(
        name = requireNotNull(name) { "name is required" },
        prompt = requireNotNull(prompt) { "prompt is required" },
        model = model,
        scriptHint = scriptHint,
    )
}
