package com.aallam.openai.api.voice

import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.file.FileSource
import kotlinx.serialization.Serializable

/** Uploads a consent recording containing the speaker's required consent phrase. */
public data class VoiceConsentRequest(
    /** Label for this consent recording. */
    public val name: String,
    /** BCP 47 language tag of the consent phrase, such as `en-US`. */
    public val language: String,
    /** Consent audio recording, uploaded without changing its bytes. */
    public val recording: FileSource,
    /** Audio MIME type; inferred from the filename when omitted. Codec parameters are removed. */
    public val mediaType: String? = null,
)

/** Updates the label of an existing consent recording without replacing its audio or language. */
@Serializable
public data class VoiceConsentUpdateRequest(
    /** New recording label. */
    public val name: String,
)

/** Creates a consent recording upload request. */
public fun voiceConsentRequest(block: VoiceConsentRequestBuilder.() -> Unit): VoiceConsentRequest =
    VoiceConsentRequestBuilder().apply(block).build()

/** Builder for a consent recording upload. */
@OpenAIDsl
public class VoiceConsentRequestBuilder {
    /** Recording label. */
    public var name: String? = null
    /** BCP 47 language tag of the consent phrase. */
    public var language: String? = null
    /** Recorded consent phrase. */
    public var recording: FileSource? = null
    /** Audio MIME type; inferred from the filename when omitted. */
    public var mediaType: String? = null

    /** Builds a request, requiring its label, language and recording. */
    public fun build(): VoiceConsentRequest = VoiceConsentRequest(
        name = requireNotNull(name) { "name is required" },
        language = requireNotNull(language) { "language is required" },
        recording = requireNotNull(recording) { "recording is required" },
        mediaType = mediaType,
    )
}

/** Creates a consent label update request. */
public fun voiceConsentUpdateRequest(block: VoiceConsentUpdateRequestBuilder.() -> Unit): VoiceConsentUpdateRequest =
    VoiceConsentUpdateRequestBuilder().apply(block).build()

/** Builder for a consent label update. */
@OpenAIDsl
public class VoiceConsentUpdateRequestBuilder {
    /** New recording label. */
    public var name: String? = null

    /** Builds an update, requiring the new label. */
    public fun build(): VoiceConsentUpdateRequest = VoiceConsentUpdateRequest(requireNotNull(name) { "name is required" })
}
