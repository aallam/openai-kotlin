@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.aallam.openai.api.admin

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/** An organization group, which may be managed by SCIM. */
@Serializable
public data class OrganizationGroup(
    /** Id. */
    public val id: String,
    /** Created at. */
    @SerialName("created_at") public val createdAt: Long,
    /** Is scim managed. */
    @SerialName("is_scim_managed") public val isScimManaged: Boolean,
    /** Name. */
    public val name: String,
    /** Group category; omitted from update responses. */
    @SerialName("group_type") public val groupType: String? = null,
)

/** Creates or renames an organization group. */
@Serializable
public data class OrganizationGroupRequest(
    /** Name. */
    public val name: String,
)

/** A user listed in or retrieved from a group. */
@Serializable
public data class OrganizationGroupUser(
    /** Id. */
    public val id: String,
    /** Email. */
    public val email: String?,
    /** Name. */
    public val name: String,
    /** Is service account. */
    @SerialName("is_service_account") public val isServiceAccount: Boolean? = null,
    /** Picture. */
    public val picture: String? = null,
    /** User type. */
    @SerialName("user_type") public val userType: String? = null,
)

/** Result of adding a user to a group. */
@Serializable
public data class OrganizationGroupMembership(
    /** Group id. */
    @SerialName("group_id") public val groupId: String,
    /** Object. */
    @SerialName("object") public val objectType: String,
    /** User id. */
    @SerialName("user_id") public val userId: String,
)

/** Adds a user to a group. */
@Serializable
public data class OrganizationGroupMemberRequest(
    /** User id. */
    @SerialName("user_id") public val userId: String,
)

/** A role and its granted permissions. */
@Serializable
public data class OrganizationRole(
    /** Id. */
    public val id: String,
    /** Description. */
    public val description: String?,
    /** Name. */
    public val name: String,
    /** Object. */
    @SerialName("object") public val objectType: String,
    /** Permissions. */
    public val permissions: List<String>,
    /** Predefined role. */
    @SerialName("predefined_role") public val predefinedRole: Boolean,
    /** Resource type. */
    @SerialName("resource_type") public val resourceType: String,
)

/** Creates a role bound to an organization or project. */
@Serializable
public data class OrganizationRoleRequest(
    /** Permissions. */
    public val permissions: List<String>,
    /** Role name. */
    @SerialName("role_name") public val roleName: String,
    /** Description. */
    public val description: String? = null,
)

/** Updates only supplied role fields; Clear sends an explicit JSON null. */
@Serializable
public data class OrganizationRoleUpdateRequest(
    /** Description. */
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    public val description: AdministrationValue<String> = AdministrationValue.Unspecified,
    /** Permissions. */
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    public val permissions: AdministrationValue<List<String>> = AdministrationValue.Unspecified,
    /** Role name. */
    @EncodeDefault(EncodeDefault.Mode.NEVER)
    @SerialName("role_name") public val roleName: AdministrationValue<String> = AdministrationValue.Unspecified,
)

/** Assigns a role to a user or group. */
@Serializable
public data class OrganizationRoleAssignmentRequest(
    /** Role id. */
    @SerialName("role_id") public val roleId: String,
)

/** Principal responsible for an effective role assignment. */
@Serializable
public data class OrganizationRoleAssignmentSource(
    /** Principal id. */
    @SerialName("principal_id") public val principalId: String,
    /** Principal type. */
    @SerialName("principal_type") public val principalType: String,
)

/** An effective role with assignment-source and audit metadata. */
@Serializable
public data class OrganizationAssignedRole(
    /** Id. */
    public val id: String,
    /** Assignment sources. */
    @SerialName("assignment_sources") public val assignmentSources: List<OrganizationRoleAssignmentSource>?,
    /** Created at. */
    @SerialName("created_at") public val createdAt: Long?,
    /** Created by. */
    @SerialName("created_by") public val createdBy: String?,
    /** Created by user obj. */
    @SerialName("created_by_user_obj") public val createdByUserObj: JsonObject?,
    /** Description. */
    public val description: String?,
    /** Metadata. */
    public val metadata: JsonObject?,
    /** Name. */
    public val name: String,
    /** Permissions. */
    public val permissions: List<String>,
    /** Predefined role. */
    @SerialName("predefined_role") public val predefinedRole: Boolean,
    /** Resource type. */
    @SerialName("resource_type") public val resourceType: String,
    /** Updated at. */
    @SerialName("updated_at") public val updatedAt: Long?,
)

/** Group summary returned with a role assignment. */
@Serializable
public data class OrganizationRoleGroup(
    /** Id. */
    public val id: String,
    /** Created at. */
    @SerialName("created_at") public val createdAt: Long,
    /** Name. */
    public val name: String,
    /** Object. */
    @SerialName("object") public val objectType: String,
    /** Scim managed. */
    @SerialName("scim_managed") public val scimManaged: Boolean,
)

/** Result of assigning a role to a group. */
@Serializable
public data class OrganizationGroupRoleAssignment(
    /** Group. */
    public val group: OrganizationRoleGroup,
    /** Object. */
    @SerialName("object") public val objectType: String,
    /** Role. */
    public val role: OrganizationRole,
)

/** Result of assigning a role to a user. */
@Serializable
public data class OrganizationUserRoleAssignment(
    /** Object. */
    @SerialName("object") public val objectType: String,
    /** Role. */
    public val role: OrganizationRole,
    /** User. */
    public val user: OrganizationUser,
)
