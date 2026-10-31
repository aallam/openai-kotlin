@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.aallam.openai.api.admin

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/** A user membership in an organization. */
@Serializable
public data class OrganizationUser(
    /** Id. */
    public val id: String,
    /** Unix timestamp in seconds when membership was added. */
    @SerialName("added_at") public val addedAt: Long,
    /** Object. */
    @SerialName("object") public val objectType: String,
    /** Api key last used at. */
    @SerialName("api_key_last_used_at") public val apiKeyLastUsedAt: Long? = null,
    /** Created. */
    public val created: Long? = null,
    /** Developer persona. */
    @SerialName("developer_persona") public val developerPersona: String? = null,
    /** Email. */
    public val email: String? = null,
    /** Is default. */
    @SerialName("is_default") public val isDefault: Boolean? = null,
    /** Is scale tier authorized purchaser. */
    @SerialName("is_scale_tier_authorized_purchaser") public val isScaleTierAuthorizedPurchaser: Boolean? = null,
    /** Is scim managed. */
    @SerialName("is_scim_managed") public val isScimManaged: Boolean? = null,
    /** Is service account. */
    @SerialName("is_service_account") public val isServiceAccount: Boolean? = null,
    /** Name. */
    public val name: String? = null,
    /** Projects. */
    public val projects: OrganizationUserProjects? = null,
    /** Role. */
    public val role: String? = null,
    /** Technical level. */
    @SerialName("technical_level") public val technicalLevel: String? = null,
    /** User. */
    public val user: OrganizationUserDetails? = null,
)

/** Projects returned with an organization user. */
@Serializable
public data class OrganizationUserProjects(
    /** Data. */
    public val data: List<OrganizationUserProject>,
    /** Object. */
    @SerialName("object") public val objectType: String,
)

/** A project associated with an organization user. */
@Serializable
public data class OrganizationUserProject(
    /** Id. */
    public val id: String? = null,
    /** Name. */
    public val name: String? = null,
    /** Role. */
    public val role: String? = null,
)

/** Nested user account information. */
@Serializable
public data class OrganizationUserDetails(
    /** Id. */
    public val id: String,
    /** Object. */
    @SerialName("object") public val objectType: String,
    /** Banned. */
    public val banned: Boolean? = null,
    /** Banned at. */
    @SerialName("banned_at") public val bannedAt: Long? = null,
    /** Email. */
    public val email: String? = null,
    /** Enabled. */
    public val enabled: Boolean? = null,
    /** Name. */
    public val name: String? = null,
    /** Picture. */
    public val picture: String? = null,
)

/** Updates only supplied organization-user fields; Clear sends an explicit JSON null. */
@Serializable
public data class OrganizationUserUpdateRequest(
    /** Developer persona. */
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("developer_persona") public val developerPersona: AdministrationValue<String> = AdministrationValue.Unspecified,
    /** Role. */
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    public val role: AdministrationValue<String> = AdministrationValue.Unspecified,
    /** Role id. */
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("role_id") public val roleId: AdministrationValue<String> = AdministrationValue.Unspecified,
    /** Technical level. */
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("technical_level") public val technicalLevel: AdministrationValue<String> = AdministrationValue.Unspecified,
)

/** An invitation to join an organization. */
@Serializable
public data class OrganizationInvite(
    /** Id. */
    public val id: String,
    /** Created at. */
    @SerialName("created_at") public val createdAt: Long,
    /** Email. */
    public val email: String,
    /** Object. */
    @SerialName("object") public val objectType: String,
    /** Projects. */
    public val projects: List<OrganizationInviteProject>,
    /** Role. */
    public val role: String,
    /** Status. */
    public val status: String,
    /** Accepted at. */
    @SerialName("accepted_at") public val acceptedAt: Long? = null,
    /** Expires at. */
    @SerialName("expires_at") public val expiresAt: Long? = null,
)

/** Initial project access attached to an invitation. */
@Serializable
public data class OrganizationInviteProject(
    /** Id. */
    public val id: String,
    /** Role. */
    public val role: String,
)

/** Invites an email address with organization and optional project roles. */
@Serializable
public data class OrganizationInviteRequest(
    /** Email. */
    public val email: String,
    /** Role. */
    public val role: String,
    /** Projects. */
    public val projects: List<OrganizationInviteProject>? = null,
)
