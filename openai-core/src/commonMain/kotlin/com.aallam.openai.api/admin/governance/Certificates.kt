package com.aallam.openai.api.admin.governance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** An organization certificate, optionally annotated with its activation state at a scope. */
@Serializable
public data class OrganizationCertificate(
    /** Certificate identifier. */
    public val id: String,
    /** PEM content and validity dates, when included by the API. */
    @SerialName("certificate_details") public val certificateDetails: CertificateDetails,
    /** Upload time in Unix seconds. */
    @SerialName("created_at") public val createdAt: Long,
    /** Optional descriptive name. */
    public val name: String? = null,
    /** Certificate, organization-certificate or project-certificate discriminator. */
    @SerialName("object") public val objectType: String = "certificate",
    /** Scope-specific activation state; omitted from an individual certificate retrieval. */
    public val active: Boolean? = null,
)

/** Available certificate content and validity information. */
@Serializable
public data class CertificateDetails(
    /** PEM content, returned when explicitly included. */
    public val content: String? = null,
    /** Expiry time in Unix seconds. */
    @SerialName("expires_at") public val expiresAt: Long? = null,
    /** Start of validity in Unix seconds. */
    @SerialName("valid_at") public val validAt: Long? = null,
)

/** Uploads PEM content without activating the certificate. */
@Serializable
public data class CertificateUploadRequest(
    /** Certificate content in PEM format. */
    public val certificate: String,
    /** Optional descriptive name. */
    public val name: String? = null,
)

/** Updates the certificate name when supplied. */
@Serializable
public data class CertificateUpdateRequest(
    /** Replacement descriptive name; null leaves it omitted. */
    public val name: String? = null,
)

/** Atomically activates or deactivates one to ten certificates at a scope. */
@Serializable
public data class CertificateActivationRequest(
    /** Certificate identifiers to change together. */
    @SerialName("certificate_ids") public val certificateIds: List<String>,
) {
    init { require(certificateIds.size in 1..10) { "Supply one to ten certificate IDs" } }
}

/** Activation/deactivation result, preserving its scope-specific response envelope. */
@Serializable
public data class CertificateActivationResult(
    /** Certificates with their resulting activation states. */
    public val data: List<OrganizationCertificate>,
    /** Scope and activation/deactivation response discriminator. */
    @SerialName("object") public val objectType: String,
)

/** Confirms deletion of a certificate; this endpoint does not return a deleted boolean. */
@Serializable
public data class CertificateDeletion(
    /** Deleted certificate identifier. */
    public val id: String,
    /** Deletion response discriminator. */
    @SerialName("object") public val objectType: String = "certificate.deleted",
)
