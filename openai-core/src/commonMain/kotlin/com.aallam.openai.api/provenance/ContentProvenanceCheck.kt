package com.aallam.openai.api.provenance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Supported provenance signals found in an image or audio upload. Absence does not establish human origin. */
@Serializable
public data class ContentProvenanceCheck(
    /** Check creation time in Unix seconds. */
    @SerialName("created_at") public val createdAt: Long,
    /** Signal-specific detection results. */
    public val results: List<ProvenanceResult>,
    /** Resource discriminator. */
    @SerialName("object") public val objectType: String = "content_provenance_check",
)

/** A C2PA manifest or SynthID watermark detection result. */
@Serializable
public sealed interface ProvenanceResult {
    /** detected or not_detected. Missing signals do not rule out AI generation. */
    public val outcome: String
    /** Model recorded in the signal, when available. */
    public val model: String?
    /** Generation time recorded in the signal, as UTC RFC 3339 text, when available. */
    public val generatedAt: String?

    /** C2PA manifest detection and validation. */
    @Serializable @SerialName("c2pa")
    public data class C2pa(
        override val outcome: String,
        /** Manifest status: trusted, valid, invalid or not_present. */
        @SerialName("validation_state") public val validationState: String,
        /** Manifest issuer, when available. */
        public val issuer: String? = null,
        override val model: String? = null,
        @SerialName("generated_at") override val generatedAt: String? = null,
    ) : ProvenanceResult

    /** SynthID watermark detection for image or audio. */
    @Serializable @SerialName("synthid")
    public data class SynthId(
        override val outcome: String,
        override val model: String? = null,
        @SerialName("generated_at") override val generatedAt: String? = null,
    ) : ProvenanceResult
}
