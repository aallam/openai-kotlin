package com.aallam.openai.api.admin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/** Administrative key metadata; retrieval does not reveal its secret. */
@Serializable
public data class OrganizationAdminAPIKey(
    /** Id. */
    public val id: String,
    /** Created at. */
    @SerialName("created_at") public val createdAt: Long,
    /** Expires at. */
    @SerialName("expires_at") public val expiresAt: Long?,
    /** Object. */
    @SerialName("object") public val objectType: String,
    /** Owner. */
    public val owner: OrganizationAdminKeyOwner,
    /** Redacted value. */
    @SerialName("redacted_value") public val redactedValue: String,
    /** Last used at. */
    @SerialName("last_used_at") public val lastUsedAt: Long? = null,
    /** Name. */
    public val name: String? = null,
)

/** Administrative key ownership information. */
@Serializable
public data class OrganizationAdminKeyOwner(
    /** Id. */
    public val id: String? = null,
    /** Created at. */
    @SerialName("created_at") public val createdAt: Long? = null,
    /** Name. */
    public val name: String? = null,
    /** Object. */
    @SerialName("object") public val objectType: String? = null,
    /** Role. */
    public val role: String? = null,
    /** Type. */
    public val type: String? = null,
)

/** Creates an administrative API key. */
@Serializable
public data class OrganizationAdminAPIKeyRequest(
    /** Name. */
    public val name: String,
    /** Expires in seconds. */
    @SerialName("expires_in_seconds") public val expiresInSeconds: Long? = null,
)

/** An administrative key secret returned only at creation, with available key metadata. */
@Serializable
public data class OrganizationAdminAPIKeyCreation(
    /** Value. */
    public val value: String,
    /** Id. */
    public val id: String? = null,
    /** Created at. */
    @SerialName("created_at") public val createdAt: Long? = null,
    /** Expires at. */
    @SerialName("expires_at") public val expiresAt: Long? = null,
    /** Object. */
    @SerialName("object") public val objectType: String? = null,
    /** Owner. */
    public val owner: OrganizationAdminKeyOwner? = null,
    /** Redacted value. */
    @SerialName("redacted_value") public val redactedValue: String? = null,
    /** Last used at. */
    @SerialName("last_used_at") public val lastUsedAt: Long? = null,
    /** Name. */
    public val name: String? = null,
) {
    override fun toString(): String = "OrganizationAdminAPIKeyCreation(id=$id, value=[REDACTED])"
}

/** Project key metadata, ownership and effective project access. */
@Serializable
public data class OrganizationProjectAPIKey(
    /** Id. */
    public val id: String,
    /** Created at. */
    @SerialName("created_at") public val createdAt: Long,
    /** Last used at. */
    @SerialName("last_used_at") public val lastUsedAt: Long?,
    /** Name. */
    public val name: String,
    /** Object. */
    @SerialName("object") public val objectType: String,
    /** Owner. */
    public val owner: OrganizationProjectKeyOwner,
    /** Owner project access. */
    @SerialName("owner_project_access") public val ownerProjectAccess: String,
    /** Redacted value. */
    @SerialName("redacted_value") public val redactedValue: String,
    /** Expires at. */
    @SerialName("expires_at") public val expiresAt: Long? = null,
)

/** The user or service account owning a project key. */
@Serializable
public data class OrganizationProjectKeyOwner(
    /** Type. */
    public val type: String? = null,
    /** Service account. */
    @SerialName("service_account") public val serviceAccount: OrganizationProjectKeyServiceAccount? = null,
    /** User. */
    public val user: OrganizationProjectKeyUser? = null,
)

/** Service-account owner details returned with a project key. */
@Serializable
public data class OrganizationProjectKeyServiceAccount(
    /** Id. */
    public val id: String,
    /** Created at. */
    @SerialName("created_at") public val createdAt: Long,
    /** Name. */
    public val name: String,
    /** Role. */
    public val role: String,
)

/** User owner details returned with a project key. */
@Serializable
public data class OrganizationProjectKeyUser(
    /** Id. */
    public val id: String,
    /** Created at. */
    @SerialName("created_at") public val createdAt: Long,
    /** Email. */
    public val email: String,
    /** Name. */
    public val name: String,
    /** Role. */
    public val role: String,
)

/** Updates a project key. Empty scopes restores all permissions; omitted fields stay unchanged. */
@Serializable
public data class OrganizationProjectAPIKeyUpdateRequest(
    /** Expires in seconds. */
    @SerialName("expires_in_seconds") public val expiresInSeconds: Long? = null,
    /** Scopes. */
    public val scopes: List<String>? = null,
) {
    init {
        require(expiresInSeconds != null || scopes != null) { "Supply scopes or expiresInSeconds" }
        require(expiresInSeconds == null || expiresInSeconds in 1..31536000) { "Invalid key expiration" }
    }
}
