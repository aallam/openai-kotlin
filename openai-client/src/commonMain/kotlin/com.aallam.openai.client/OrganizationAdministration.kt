package com.aallam.openai.client

import com.aallam.openai.api.admin.*
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder

/**
 * Organization identity and project management using an Admin API key.
 *
 * Retrieval returns null only for HTTP 404. Other API errors propagate. Default methods
 * keep custom clients source compatible; the SDK administration client implements all operations.
 */
public interface OrganizationAdministration {
    /** Create admin API key. */
    public suspend fun createOrganizationAdminAPIKey(
        request: OrganizationAdminAPIKeyRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationAdminAPIKeyCreation = unsupported()

    /** Delete admin API key. */
    public suspend fun deleteOrganizationAdminAPIKey(
        keyId: String,
        requestOptions: RequestOptions? = null,
    ): AdministrationDeletion = unsupported()

    /** List all organization and project API keys.. */
    public suspend fun organizationAdminAPIKeys(
        after: String? = null,
        limit: Int? = null,
        order: SortOrder? = null,
        requestOptions: RequestOptions? = null,
    ): AdministrationPage<OrganizationAdminAPIKey> = unsupported()

    /** Retrieve admin API key. */
    public suspend fun organizationAdminAPIKey(
        keyId: String,
        requestOptions: RequestOptions? = null,
    ): OrganizationAdminAPIKey? = unsupported()

    /** Create group. */
    public suspend fun createOrganizationGroup(
        request: OrganizationGroupRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationGroup = unsupported()

    /** Delete group. */
    public suspend fun deleteOrganizationGroup(
        groupId: String,
        requestOptions: RequestOptions? = null,
    ): AdministrationDeletion = unsupported()

    /** List groups. */
    public suspend fun organizationGroups(
        after: String? = null,
        limit: Int? = null,
        order: SortOrder? = null,
        requestOptions: RequestOptions? = null,
    ): AdministrationPage<OrganizationGroup> = unsupported()

    /** Retrieve group. */
    public suspend fun organizationGroup(
        groupId: String,
        requestOptions: RequestOptions? = null,
    ): OrganizationGroup? = unsupported()

    /** Assign organization role to group. */
    public suspend fun assignOrganizationGroupRole(
        groupId: String,
        request: OrganizationRoleAssignmentRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationGroupRoleAssignment = unsupported()

    /** Unassign organization role from group. */
    public suspend fun removeOrganizationGroupRole(
        groupId: String,
        roleId: String,
        requestOptions: RequestOptions? = null,
    ): AdministrationDeletion = unsupported()

    /** List group organization role assignments. */
    public suspend fun organizationGroupRoles(
        groupId: String,
        after: String? = null,
        limit: Int? = null,
        order: SortOrder? = null,
        requestOptions: RequestOptions? = null,
    ): AdministrationPage<OrganizationAssignedRole> = unsupported()

    /** Retrieve group organization role. */
    public suspend fun organizationGroupRole(
        groupId: String,
        roleId: String,
        requestOptions: RequestOptions? = null,
    ): OrganizationAssignedRole? = unsupported()

    /** Update group. */
    public suspend fun updateOrganizationGroup(
        groupId: String,
        request: OrganizationGroupRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationGroup = unsupported()

    /** Add group user. */
    public suspend fun addOrganizationGroupUser(
        groupId: String,
        request: OrganizationGroupMemberRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationGroupMembership = unsupported()

    /** Remove group user. */
    public suspend fun removeOrganizationGroupUser(
        groupId: String,
        userId: String,
        requestOptions: RequestOptions? = null,
    ): AdministrationDeletion = unsupported()

    /** List group users. */
    public suspend fun organizationGroupUsers(
        groupId: String,
        after: String? = null,
        limit: Int? = null,
        order: SortOrder? = null,
        requestOptions: RequestOptions? = null,
    ): AdministrationPage<OrganizationGroupUser> = unsupported()

    /** Retrieve group user. */
    public suspend fun organizationGroupUser(
        groupId: String,
        userId: String,
        requestOptions: RequestOptions? = null,
    ): OrganizationGroupUser? = unsupported()

    /** Create invite. */
    public suspend fun createOrganizationInvite(
        request: OrganizationInviteRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationInvite = unsupported()

    /** Delete invite. */
    public suspend fun deleteOrganizationInvite(
        inviteId: String,
        requestOptions: RequestOptions? = null,
    ): AdministrationDeletion = unsupported()

    /** List invites. */
    public suspend fun organizationInvites(
        after: String? = null,
        limit: Int? = null,
        requestOptions: RequestOptions? = null,
    ): AdministrationPage<OrganizationInvite> = unsupported()

    /** Retrieve invite. */
    public suspend fun organizationInvite(
        inviteId: String,
        requestOptions: RequestOptions? = null,
    ): OrganizationInvite? = unsupported()

    /** Delete project API key. */
    public suspend fun deleteProjectAPIKey(
        projectId: String,
        apiKeyId: String,
        requestOptions: RequestOptions? = null,
    ): AdministrationDeletion = unsupported()

    /** List project API keys. */
    public suspend fun projectAPIKeys(
        projectId: String,
        after: String? = null,
        limit: Int? = null,
        ownerProjectAccess: String? = null,
        requestOptions: RequestOptions? = null,
    ): AdministrationPage<OrganizationProjectAPIKey> = unsupported()

    /** Retrieve project API key. */
    public suspend fun projectAPIKey(
        projectId: String,
        apiKeyId: String,
        requestOptions: RequestOptions? = null,
    ): OrganizationProjectAPIKey? = unsupported()

    /** Update project API key. */
    public suspend fun updateProjectAPIKey(
        projectId: String,
        apiKeyId: String,
        request: OrganizationProjectAPIKeyUpdateRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationProjectAPIKey = unsupported()

    /** Archive project. */
    public suspend fun archiveOrganizationProject(
        projectId: String,
        requestOptions: RequestOptions? = null,
    ): OrganizationProject = unsupported()

    /** Create project. */
    public suspend fun createOrganizationProject(
        request: OrganizationProjectRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationProject = unsupported()

    /** Add project group. */
    public suspend fun createProjectGroup(
        projectId: String,
        request: OrganizationProjectGroupRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationProjectGroup = unsupported()

    /** Remove project group. */
    public suspend fun deleteProjectGroup(
        projectId: String,
        groupId: String,
        requestOptions: RequestOptions? = null,
    ): AdministrationDeletion = unsupported()

    /** List project groups. */
    public suspend fun projectGroups(
        projectId: String,
        after: String? = null,
        limit: Int? = null,
        order: SortOrder? = null,
        requestOptions: RequestOptions? = null,
    ): AdministrationPage<OrganizationProjectGroup> = unsupported()

    /** Retrieve project group. */
    public suspend fun projectGroup(
        projectId: String,
        groupId: String,
        groupType: String? = null,
        requestOptions: RequestOptions? = null,
    ): OrganizationProjectGroup? = unsupported()

    /** Assign project role to group. */
    public suspend fun assignProjectGroupRole(
        projectId: String,
        groupId: String,
        request: OrganizationRoleAssignmentRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationGroupRoleAssignment = unsupported()

    /** Unassign project role from group. */
    public suspend fun removeProjectGroupRole(
        projectId: String,
        groupId: String,
        roleId: String,
        requestOptions: RequestOptions? = null,
    ): AdministrationDeletion = unsupported()

    /** List project group role assignments. */
    public suspend fun projectGroupRoles(
        projectId: String,
        groupId: String,
        after: String? = null,
        limit: Int? = null,
        order: SortOrder? = null,
        requestOptions: RequestOptions? = null,
    ): AdministrationPage<OrganizationAssignedRole> = unsupported()

    /** Retrieve project group role. */
    public suspend fun projectGroupRole(
        projectId: String,
        groupId: String,
        roleId: String,
        requestOptions: RequestOptions? = null,
    ): OrganizationAssignedRole? = unsupported()

    /** List projects. */
    public suspend fun organizationProjects(
        after: String? = null,
        includeArchived: Boolean? = null,
        limit: Int? = null,
        requestOptions: RequestOptions? = null,
    ): AdministrationPage<OrganizationProject> = unsupported()

    /** Retrieve project. */
    public suspend fun organizationProject(
        projectId: String,
        requestOptions: RequestOptions? = null,
    ): OrganizationProject? = unsupported()

    /** Create project role. */
    public suspend fun createProjectRole(
        projectId: String,
        request: OrganizationRoleRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationRole = unsupported()

    /** Delete project role. */
    public suspend fun deleteProjectRole(
        projectId: String,
        roleId: String,
        requestOptions: RequestOptions? = null,
    ): AdministrationDeletion = unsupported()

    /** List project roles. */
    public suspend fun projectRoles(
        projectId: String,
        after: String? = null,
        limit: Int? = null,
        order: SortOrder? = null,
        requestOptions: RequestOptions? = null,
    ): AdministrationPage<OrganizationRole> = unsupported()

    /** Retrieve project role. */
    public suspend fun projectRole(
        projectId: String,
        roleId: String,
        requestOptions: RequestOptions? = null,
    ): OrganizationRole? = unsupported()

    /** Update project role. */
    public suspend fun updateProjectRole(
        projectId: String,
        roleId: String,
        request: OrganizationRoleUpdateRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationRole = unsupported()

    /** Create an API key for a service account. */
    public suspend fun createProjectServiceAccountAPIKey(
        projectId: String,
        serviceAccountId: String,
        request: OrganizationServiceAccountAPIKeyRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationServiceAccountAPIKey = unsupported()

    /** Create project service account. */
    public suspend fun createProjectServiceAccount(
        projectId: String,
        request: OrganizationServiceAccountRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationServiceAccount = unsupported()

    /** Delete project service account. */
    public suspend fun deleteProjectServiceAccount(
        projectId: String,
        serviceAccountId: String,
        requestOptions: RequestOptions? = null,
    ): AdministrationDeletion = unsupported()

    /** List project service accounts. */
    public suspend fun projectServiceAccounts(
        projectId: String,
        after: String? = null,
        limit: Int? = null,
        requestOptions: RequestOptions? = null,
    ): AdministrationPage<OrganizationServiceAccount> = unsupported()

    /** Retrieve project service account. */
    public suspend fun projectServiceAccount(
        projectId: String,
        serviceAccountId: String,
        requestOptions: RequestOptions? = null,
    ): OrganizationServiceAccount? = unsupported()

    /** Update project service account. */
    public suspend fun updateProjectServiceAccount(
        projectId: String,
        serviceAccountId: String,
        request: OrganizationServiceAccountUpdateRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationServiceAccount = unsupported()

    /** Modify project. */
    public suspend fun updateOrganizationProject(
        projectId: String,
        request: OrganizationProjectUpdateRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationProject = unsupported()

    /** Create project user. */
    public suspend fun createProjectUser(
        projectId: String,
        request: OrganizationProjectUserRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationProjectUser = unsupported()

    /** Delete project user. */
    public suspend fun deleteProjectUser(
        projectId: String,
        userId: String,
        requestOptions: RequestOptions? = null,
    ): AdministrationDeletion = unsupported()

    /** List project users. */
    public suspend fun projectUsers(
        projectId: String,
        after: String? = null,
        limit: Int? = null,
        requestOptions: RequestOptions? = null,
    ): AdministrationPage<OrganizationProjectUser> = unsupported()

    /** Retrieve project user. */
    public suspend fun projectUser(
        projectId: String,
        userId: String,
        requestOptions: RequestOptions? = null,
    ): OrganizationProjectUser? = unsupported()

    /** Assign project role to user. */
    public suspend fun assignProjectUserRole(
        projectId: String,
        userId: String,
        request: OrganizationRoleAssignmentRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationUserRoleAssignment = unsupported()

    /** Unassign project role from user. */
    public suspend fun removeProjectUserRole(
        projectId: String,
        userId: String,
        roleId: String,
        requestOptions: RequestOptions? = null,
    ): AdministrationDeletion = unsupported()

    /** List project user role assignments. */
    public suspend fun projectUserRoles(
        projectId: String,
        userId: String,
        after: String? = null,
        limit: Int? = null,
        order: SortOrder? = null,
        requestOptions: RequestOptions? = null,
    ): AdministrationPage<OrganizationAssignedRole> = unsupported()

    /** Retrieve project user role. */
    public suspend fun projectUserRole(
        projectId: String,
        userId: String,
        roleId: String,
        requestOptions: RequestOptions? = null,
    ): OrganizationAssignedRole? = unsupported()

    /** Modify project user. */
    public suspend fun updateProjectUser(
        projectId: String,
        userId: String,
        request: OrganizationProjectUserUpdateRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationProjectUser = unsupported()

    /** Create organization role. */
    public suspend fun createOrganizationRole(
        request: OrganizationRoleRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationRole = unsupported()

    /** Delete organization role. */
    public suspend fun deleteOrganizationRole(
        roleId: String,
        requestOptions: RequestOptions? = null,
    ): AdministrationDeletion = unsupported()

    /** List organization roles. */
    public suspend fun organizationRoles(
        after: String? = null,
        limit: Int? = null,
        order: SortOrder? = null,
        requestOptions: RequestOptions? = null,
    ): AdministrationPage<OrganizationRole> = unsupported()

    /** Retrieve organization role. */
    public suspend fun organizationRole(
        roleId: String,
        requestOptions: RequestOptions? = null,
    ): OrganizationRole? = unsupported()

    /** Update organization role. */
    public suspend fun updateOrganizationRole(
        roleId: String,
        request: OrganizationRoleUpdateRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationRole = unsupported()

    /** Delete user. */
    public suspend fun deleteOrganizationUser(
        userId: String,
        requestOptions: RequestOptions? = null,
    ): AdministrationDeletion = unsupported()

    /** List users. */
    public suspend fun organizationUsers(
        after: String? = null,
        emails: List<String>? = null,
        limit: Int? = null,
        requestOptions: RequestOptions? = null,
    ): AdministrationPage<OrganizationUser> = unsupported()

    /** Retrieve user. */
    public suspend fun organizationUser(
        userId: String,
        requestOptions: RequestOptions? = null,
    ): OrganizationUser? = unsupported()

    /** Assign organization role to user. */
    public suspend fun assignOrganizationUserRole(
        userId: String,
        request: OrganizationRoleAssignmentRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationUserRoleAssignment = unsupported()

    /** Unassign organization role from user. */
    public suspend fun removeOrganizationUserRole(
        userId: String,
        roleId: String,
        requestOptions: RequestOptions? = null,
    ): AdministrationDeletion = unsupported()

    /** List user organization role assignments. */
    public suspend fun organizationUserRoles(
        userId: String,
        after: String? = null,
        limit: Int? = null,
        order: SortOrder? = null,
        requestOptions: RequestOptions? = null,
    ): AdministrationPage<OrganizationAssignedRole> = unsupported()

    /** Retrieve user organization role. */
    public suspend fun organizationUserRole(
        userId: String,
        roleId: String,
        requestOptions: RequestOptions? = null,
    ): OrganizationAssignedRole? = unsupported()

    /** Modify user. */
    public suspend fun updateOrganizationUser(
        userId: String,
        request: OrganizationUserUpdateRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationUser = unsupported()

    private fun unsupported(): Nothing = throw UnsupportedOperationException("Organization administration is not supported by this client.")
}
