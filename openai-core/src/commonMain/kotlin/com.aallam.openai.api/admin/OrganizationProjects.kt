@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.aallam.openai.api.admin

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/** An organization project and its lifecycle settings. */
@Serializable
public data class OrganizationProject(
    /** Id. */
    public val id: String,
    /** Created at. */
    @SerialName("created_at") public val createdAt: Long,
    /** Object. */
    @SerialName("object") public val objectType: String,
    /** Archived at. */
    @SerialName("archived_at") public val archivedAt: Long? = null,
    /** External key id. */
    @SerialName("external_key_id") public val externalKeyId: String? = null,
    /** Name. */
    public val name: String? = null,
    /** Residency. */
    public val residency: String? = null,
    /** Status. */
    public val status: String? = null,
)

/** Creates an organization project. */
@Serializable
public data class OrganizationProjectRequest(
    /** Name. */
    public val name: String,
    /** External key id. */
    @SerialName("external_key_id") public val externalKeyId: String? = null,
    /** Geography. */
    public val geography: String? = null,
    /** Residency. */
    public val residency: String? = null,
)

/** Updates supplied project fields; Clear sends an explicit JSON null. */
@Serializable
public data class OrganizationProjectUpdateRequest(
    /** External key id. */
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("external_key_id") public val externalKeyId: AdministrationValue<String> = AdministrationValue.Unspecified,
    /** Geography. */
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    public val geography: AdministrationValue<String> = AdministrationValue.Unspecified,
    /** Name. */
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    public val name: AdministrationValue<String> = AdministrationValue.Unspecified,
)

/** A user membership in a project. */
@Serializable
public data class OrganizationProjectUser(
    /** Id. */
    public val id: String,
    /** Added at. */
    @SerialName("added_at") public val addedAt: Long,
    /** Object. */
    @SerialName("object") public val objectType: String,
    /** Role. */
    public val role: String,
    /** Email. */
    public val email: String? = null,
    /** Name. */
    public val name: String? = null,
)

/** Adds a project user by user ID or email with a role. */
@Serializable
public data class OrganizationProjectUserRequest(
    /** Role. */
    public val role: String,
    /** Email. */
    public val email: String? = null,
    /** User id. */
    @SerialName("user_id") public val userId: String? = null,
)

/** Updates a project user role, preserving omission versus explicit null. */
@Serializable
public data class OrganizationProjectUserUpdateRequest(
    /** Role. */
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    public val role: AdministrationValue<String> = AdministrationValue.Unspecified,
)

/** A group membership in a project. */
@Serializable
public data class OrganizationProjectGroup(
    /** Created at. */
    @SerialName("created_at") public val createdAt: Long,
    /** Group id. */
    @SerialName("group_id") public val groupId: String,
    /** Group name. */
    @SerialName("group_name") public val groupName: String,
    /** Group type. */
    @SerialName("group_type") public val groupType: String,
    /** Object. */
    @SerialName("object") public val objectType: String,
    /** Project id. */
    @SerialName("project_id") public val projectId: String,
)

/** Grants a group project access with the selected role. */
@Serializable
public data class OrganizationProjectGroupRequest(
    /** Group id. */
    @SerialName("group_id") public val groupId: String,
    /** Role. */
    public val role: String,
)

/** A project service account; creation may include a one-time API key. */
@Serializable
public data class OrganizationServiceAccount(
    /** Id. */
    public val id: String,
    /** Created at. */
    @SerialName("created_at") public val createdAt: Long,
    /** Name. */
    public val name: String,
    /** Object. */
    @SerialName("object") public val objectType: String,
    /** Role. */
    public val role: String,
    /** Api key. */
    @SerialName("api_key") public val apiKey: OrganizationServiceAccountAPIKey? = null,
)

/** Creates a service account and optionally its initial API key. */
@Serializable
public data class OrganizationServiceAccountRequest(
    /** Name. */
    public val name: String,
    /** Create service account only. */
    @SerialName("create_service_account_only") public val createServiceAccountOnly: Boolean? = null,
    /** Expires in seconds. */
    @SerialName("expires_in_seconds") public val expiresInSeconds: Long? = null,
)

/** Renames a service account or updates its project role. */
@Serializable
public data class OrganizationServiceAccountUpdateRequest(
    /** Name. */
    public val name: String? = null,
    /** Role. */
    public val role: String? = null,
)

/** A newly created service-account key. Its value is returned only at creation. */
@Serializable
public data class OrganizationServiceAccountAPIKey(
    /** Id. */
    public val id: String,
    /** Created at. */
    @SerialName("created_at") public val createdAt: Long,
    /** Name. */
    public val name: String,
    /** Object. */
    @SerialName("object") public val objectType: String,
    /** Value. */
    public val value: String,
    /** Expires at. */
    @SerialName("expires_at") public val expiresAt: Long? = null,
) {
    override fun toString(): String = "OrganizationServiceAccountAPIKey(id=$id, value=[REDACTED])"
}

/** Creates a named service-account API key with optional scopes and expiration. */
@Serializable
public data class OrganizationServiceAccountAPIKeyRequest(
    /** Expires in seconds. */
    @SerialName("expires_in_seconds") public val expiresInSeconds: Long? = null,
    /** Name. */
    public val name: String? = null,
    /** Scopes. */
    public val scopes: List<String>? = null,
)
