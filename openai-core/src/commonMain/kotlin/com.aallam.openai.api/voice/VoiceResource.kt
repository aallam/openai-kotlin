package com.aallam.openai.api.voice

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A saved custom voice. Prompt-generated voices are supported only by Live. */
@Serializable
public data class VoiceResource(
    /** Identifier used when selecting this custom voice. */
    public val id: String,
    /** Creation time in Unix seconds. */
    @SerialName("created_at") public val createdAt: Long,
    /** Human-readable voice label. */
    public val name: String,
    /** Creation method: `audio_sample` or `prompt`. */
    public val type: String,
    /** Original description for a prompt-generated voice, when retained. */
    public val prompt: String? = null,
    /** API object discriminator. */
    @SerialName("object") public val objectType: String = "audio.voice",
)

/** Recording authorizing creation of voices from the same speaker's audio samples. */
@Serializable
public data class VoiceConsent(
    /** Consent recording identifier. */
    public val id: String,
    /** Creation time in Unix seconds. */
    @SerialName("created_at") public val createdAt: Long,
    /** BCP 47 language tag of the recorded consent phrase. */
    public val language: String,
    /** Human-readable consent label. */
    public val name: String,
    /** API object discriminator. */
    @SerialName("object") public val objectType: String = "audio.voice_consent",
)

/** A cursor page of consent recordings, retaining the response envelope. */
@Serializable
public data class VoiceConsentPage(
    /** Recordings in this page. */
    public val data: List<VoiceConsent>,
    /** Whether more recordings can be requested. */
    @SerialName("has_more") public val hasMore: Boolean,
    /** First recording identifier, when supplied. */
    @SerialName("first_id") public val firstId: String? = null,
    /** Last recording identifier; pass this as the next request's `after` cursor. */
    @SerialName("last_id") public val lastId: String? = null,
    /** API object discriminator. */
    @SerialName("object") public val objectType: String = "list",
) : List<VoiceConsent> by data
