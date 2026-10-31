package com.aallam.openai.client.internal.api

import com.aallam.openai.api.admin.*
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.client.OrganizationAdministration
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.buildJsonObject

internal class OrganizationAdministrationApi(private val requester: HttpRequester) : OrganizationAdministration {
    override suspend fun createOrganizationAdminAPIKey(
        request: OrganizationAdminAPIKeyRequest,
        requestOptions: RequestOptions?,
    ): OrganizationAdminAPIKeyCreation = post("organization/admin_api_keys", request, requestOptions)

    override suspend fun deleteOrganizationAdminAPIKey(
        keyId: String,
        requestOptions: RequestOptions?,
    ): AdministrationDeletion = delete("organization/admin_api_keys/${segment(keyId)}", requestOptions)

    override suspend fun organizationAdminAPIKeys(
        after: String?,
        limit: Int?,
        order: SortOrder?,
        requestOptions: RequestOptions?,
    ): AdministrationPage<OrganizationAdminAPIKey> = get("organization/admin_api_keys", requestOptions) {
        after?.let { parameters.append("after", it.toString()) }
        limit?.let { parameters.append("limit", it.toString()) }
        order?.let { parameters.append("order", it.order) }
    }

    override suspend fun organizationAdminAPIKey(
        keyId: String,
        requestOptions: RequestOptions?,
    ): OrganizationAdminAPIKey? = getOrNull("organization/admin_api_keys/${segment(keyId)}", requestOptions)

    override suspend fun createOrganizationGroup(
        request: OrganizationGroupRequest,
        requestOptions: RequestOptions?,
    ): OrganizationGroup = post("organization/groups", request, requestOptions)

    override suspend fun deleteOrganizationGroup(
        groupId: String,
        requestOptions: RequestOptions?,
    ): AdministrationDeletion = delete("organization/groups/${segment(groupId)}", requestOptions)

    override suspend fun organizationGroups(
        after: String?,
        limit: Int?,
        order: SortOrder?,
        requestOptions: RequestOptions?,
    ): AdministrationPage<OrganizationGroup> = get("organization/groups", requestOptions) {
        after?.let { parameters.append("after", it.toString()) }
        limit?.let { parameters.append("limit", it.toString()) }
        order?.let { parameters.append("order", it.order) }
    }

    override suspend fun organizationGroup(
        groupId: String,
        requestOptions: RequestOptions?,
    ): OrganizationGroup? = getOrNull("organization/groups/${segment(groupId)}", requestOptions)

    override suspend fun assignOrganizationGroupRole(
        groupId: String,
        request: OrganizationRoleAssignmentRequest,
        requestOptions: RequestOptions?,
    ): OrganizationGroupRoleAssignment = post("organization/groups/${segment(groupId)}/roles", request, requestOptions)

    override suspend fun removeOrganizationGroupRole(
        groupId: String,
        roleId: String,
        requestOptions: RequestOptions?,
    ): AdministrationDeletion = delete("organization/groups/${segment(groupId)}/roles/${segment(roleId)}", requestOptions)

    override suspend fun organizationGroupRoles(
        groupId: String,
        after: String?,
        limit: Int?,
        order: SortOrder?,
        requestOptions: RequestOptions?,
    ): AdministrationPage<OrganizationAssignedRole> = get("organization/groups/${segment(groupId)}/roles", requestOptions) {
        after?.let { parameters.append("after", it.toString()) }
        limit?.let { parameters.append("limit", it.toString()) }
        order?.let { parameters.append("order", it.order) }
    }

    override suspend fun organizationGroupRole(
        groupId: String,
        roleId: String,
        requestOptions: RequestOptions?,
    ): OrganizationAssignedRole? = getOrNull("organization/groups/${segment(groupId)}/roles/${segment(roleId)}", requestOptions)

    override suspend fun updateOrganizationGroup(
        groupId: String,
        request: OrganizationGroupRequest,
        requestOptions: RequestOptions?,
    ): OrganizationGroup = post("organization/groups/${segment(groupId)}", request, requestOptions)

    override suspend fun addOrganizationGroupUser(
        groupId: String,
        request: OrganizationGroupMemberRequest,
        requestOptions: RequestOptions?,
    ): OrganizationGroupMembership = post("organization/groups/${segment(groupId)}/users", request, requestOptions)

    override suspend fun removeOrganizationGroupUser(
        groupId: String,
        userId: String,
        requestOptions: RequestOptions?,
    ): AdministrationDeletion = delete("organization/groups/${segment(groupId)}/users/${segment(userId)}", requestOptions)

    override suspend fun organizationGroupUsers(
        groupId: String,
        after: String?,
        limit: Int?,
        order: SortOrder?,
        requestOptions: RequestOptions?,
    ): AdministrationPage<OrganizationGroupUser> = get("organization/groups/${segment(groupId)}/users", requestOptions) {
        after?.let { parameters.append("after", it.toString()) }
        limit?.let { parameters.append("limit", it.toString()) }
        order?.let { parameters.append("order", it.order) }
    }

    override suspend fun organizationGroupUser(
        groupId: String,
        userId: String,
        requestOptions: RequestOptions?,
    ): OrganizationGroupUser? = getOrNull("organization/groups/${segment(groupId)}/users/${segment(userId)}", requestOptions)

    override suspend fun createOrganizationInvite(
        request: OrganizationInviteRequest,
        requestOptions: RequestOptions?,
    ): OrganizationInvite = post("organization/invites", request, requestOptions)

    override suspend fun deleteOrganizationInvite(
        inviteId: String,
        requestOptions: RequestOptions?,
    ): AdministrationDeletion = delete("organization/invites/${segment(inviteId)}", requestOptions)

    override suspend fun organizationInvites(
        after: String?,
        limit: Int?,
        requestOptions: RequestOptions?,
    ): AdministrationPage<OrganizationInvite> = get("organization/invites", requestOptions) {
        after?.let { parameters.append("after", it.toString()) }
        limit?.let { parameters.append("limit", it.toString()) }
    }

    override suspend fun organizationInvite(
        inviteId: String,
        requestOptions: RequestOptions?,
    ): OrganizationInvite? = getOrNull("organization/invites/${segment(inviteId)}", requestOptions)

    override suspend fun deleteProjectAPIKey(
        projectId: String,
        apiKeyId: String,
        requestOptions: RequestOptions?,
    ): AdministrationDeletion = delete("organization/projects/${segment(projectId)}/api_keys/${segment(apiKeyId)}", requestOptions)

    override suspend fun projectAPIKeys(
        projectId: String,
        after: String?,
        limit: Int?,
        ownerProjectAccess: String?,
        requestOptions: RequestOptions?,
    ): AdministrationPage<OrganizationProjectAPIKey> = get("organization/projects/${segment(projectId)}/api_keys", requestOptions) {
        after?.let { parameters.append("after", it.toString()) }
        limit?.let { parameters.append("limit", it.toString()) }
        ownerProjectAccess?.let { parameters.append("owner_project_access", it.toString()) }
    }

    override suspend fun projectAPIKey(
        projectId: String,
        apiKeyId: String,
        requestOptions: RequestOptions?,
    ): OrganizationProjectAPIKey? = getOrNull("organization/projects/${segment(projectId)}/api_keys/${segment(apiKeyId)}", requestOptions)

    override suspend fun updateProjectAPIKey(
        projectId: String,
        apiKeyId: String,
        request: OrganizationProjectAPIKeyUpdateRequest,
        requestOptions: RequestOptions?,
    ): OrganizationProjectAPIKey = post("organization/projects/${segment(projectId)}/api_keys/${segment(apiKeyId)}", request, requestOptions)

    override suspend fun archiveOrganizationProject(
        projectId: String,
        requestOptions: RequestOptions?,
    ): OrganizationProject = postEmpty("organization/projects/${segment(projectId)}/archive", requestOptions)

    override suspend fun createOrganizationProject(
        request: OrganizationProjectRequest,
        requestOptions: RequestOptions?,
    ): OrganizationProject = post("organization/projects", request, requestOptions)

    override suspend fun createProjectGroup(
        projectId: String,
        request: OrganizationProjectGroupRequest,
        requestOptions: RequestOptions?,
    ): OrganizationProjectGroup = post("organization/projects/${segment(projectId)}/groups", request, requestOptions)

    override suspend fun deleteProjectGroup(
        projectId: String,
        groupId: String,
        requestOptions: RequestOptions?,
    ): AdministrationDeletion = delete("organization/projects/${segment(projectId)}/groups/${segment(groupId)}", requestOptions)

    override suspend fun projectGroups(
        projectId: String,
        after: String?,
        limit: Int?,
        order: SortOrder?,
        requestOptions: RequestOptions?,
    ): AdministrationPage<OrganizationProjectGroup> = get("organization/projects/${segment(projectId)}/groups", requestOptions) {
        after?.let { parameters.append("after", it.toString()) }
        limit?.let { parameters.append("limit", it.toString()) }
        order?.let { parameters.append("order", it.order) }
    }

    override suspend fun projectGroup(
        projectId: String,
        groupId: String,
        groupType: String?,
        requestOptions: RequestOptions?,
    ): OrganizationProjectGroup? = getOrNull("organization/projects/${segment(projectId)}/groups/${segment(groupId)}", requestOptions) {
        groupType?.let { parameters.append("group_type", it.toString()) }
    }

    override suspend fun assignProjectGroupRole(
        projectId: String,
        groupId: String,
        request: OrganizationRoleAssignmentRequest,
        requestOptions: RequestOptions?,
    ): OrganizationGroupRoleAssignment = post("projects/${segment(projectId)}/groups/${segment(groupId)}/roles", request, requestOptions)

    override suspend fun removeProjectGroupRole(
        projectId: String,
        groupId: String,
        roleId: String,
        requestOptions: RequestOptions?,
    ): AdministrationDeletion = delete("projects/${segment(projectId)}/groups/${segment(groupId)}/roles/${segment(roleId)}", requestOptions)

    override suspend fun projectGroupRoles(
        projectId: String,
        groupId: String,
        after: String?,
        limit: Int?,
        order: SortOrder?,
        requestOptions: RequestOptions?,
    ): AdministrationPage<OrganizationAssignedRole> = get("projects/${segment(projectId)}/groups/${segment(groupId)}/roles", requestOptions) {
        after?.let { parameters.append("after", it.toString()) }
        limit?.let { parameters.append("limit", it.toString()) }
        order?.let { parameters.append("order", it.order) }
    }

    override suspend fun projectGroupRole(
        projectId: String,
        groupId: String,
        roleId: String,
        requestOptions: RequestOptions?,
    ): OrganizationAssignedRole? = getOrNull("projects/${segment(projectId)}/groups/${segment(groupId)}/roles/${segment(roleId)}", requestOptions)

    override suspend fun organizationProjects(
        after: String?,
        includeArchived: Boolean?,
        limit: Int?,
        requestOptions: RequestOptions?,
    ): AdministrationPage<OrganizationProject> = get("organization/projects", requestOptions) {
        after?.let { parameters.append("after", it.toString()) }
        includeArchived?.let { parameters.append("include_archived", it.toString()) }
        limit?.let { parameters.append("limit", it.toString()) }
    }

    override suspend fun organizationProject(
        projectId: String,
        requestOptions: RequestOptions?,
    ): OrganizationProject? = getOrNull("organization/projects/${segment(projectId)}", requestOptions)

    override suspend fun createProjectRole(
        projectId: String,
        request: OrganizationRoleRequest,
        requestOptions: RequestOptions?,
    ): OrganizationRole = post("projects/${segment(projectId)}/roles", request, requestOptions)

    override suspend fun deleteProjectRole(
        projectId: String,
        roleId: String,
        requestOptions: RequestOptions?,
    ): AdministrationDeletion = delete("projects/${segment(projectId)}/roles/${segment(roleId)}", requestOptions)

    override suspend fun projectRoles(
        projectId: String,
        after: String?,
        limit: Int?,
        order: SortOrder?,
        requestOptions: RequestOptions?,
    ): AdministrationPage<OrganizationRole> = get("projects/${segment(projectId)}/roles", requestOptions) {
        after?.let { parameters.append("after", it.toString()) }
        limit?.let { parameters.append("limit", it.toString()) }
        order?.let { parameters.append("order", it.order) }
    }

    override suspend fun projectRole(
        projectId: String,
        roleId: String,
        requestOptions: RequestOptions?,
    ): OrganizationRole? = getOrNull("projects/${segment(projectId)}/roles/${segment(roleId)}", requestOptions)

    override suspend fun updateProjectRole(
        projectId: String,
        roleId: String,
        request: OrganizationRoleUpdateRequest,
        requestOptions: RequestOptions?,
    ): OrganizationRole = post("projects/${segment(projectId)}/roles/${segment(roleId)}", request, requestOptions)

    override suspend fun createProjectServiceAccountAPIKey(
        projectId: String,
        serviceAccountId: String,
        request: OrganizationServiceAccountAPIKeyRequest,
        requestOptions: RequestOptions?,
    ): OrganizationServiceAccountAPIKey = post("organization/projects/${segment(projectId)}/service_accounts/${segment(serviceAccountId)}/api_keys", request, requestOptions)

    override suspend fun createProjectServiceAccount(
        projectId: String,
        request: OrganizationServiceAccountRequest,
        requestOptions: RequestOptions?,
    ): OrganizationServiceAccount = post("organization/projects/${segment(projectId)}/service_accounts", request, requestOptions)

    override suspend fun deleteProjectServiceAccount(
        projectId: String,
        serviceAccountId: String,
        requestOptions: RequestOptions?,
    ): AdministrationDeletion = delete("organization/projects/${segment(projectId)}/service_accounts/${segment(serviceAccountId)}", requestOptions)

    override suspend fun projectServiceAccounts(
        projectId: String,
        after: String?,
        limit: Int?,
        requestOptions: RequestOptions?,
    ): AdministrationPage<OrganizationServiceAccount> = get("organization/projects/${segment(projectId)}/service_accounts", requestOptions) {
        after?.let { parameters.append("after", it.toString()) }
        limit?.let { parameters.append("limit", it.toString()) }
    }

    override suspend fun projectServiceAccount(
        projectId: String,
        serviceAccountId: String,
        requestOptions: RequestOptions?,
    ): OrganizationServiceAccount? = getOrNull("organization/projects/${segment(projectId)}/service_accounts/${segment(serviceAccountId)}", requestOptions)

    override suspend fun updateProjectServiceAccount(
        projectId: String,
        serviceAccountId: String,
        request: OrganizationServiceAccountUpdateRequest,
        requestOptions: RequestOptions?,
    ): OrganizationServiceAccount = post("organization/projects/${segment(projectId)}/service_accounts/${segment(serviceAccountId)}", request, requestOptions)

    override suspend fun updateOrganizationProject(
        projectId: String,
        request: OrganizationProjectUpdateRequest,
        requestOptions: RequestOptions?,
    ): OrganizationProject = post("organization/projects/${segment(projectId)}", request, requestOptions)

    override suspend fun createProjectUser(
        projectId: String,
        request: OrganizationProjectUserRequest,
        requestOptions: RequestOptions?,
    ): OrganizationProjectUser = post("organization/projects/${segment(projectId)}/users", request, requestOptions)

    override suspend fun deleteProjectUser(
        projectId: String,
        userId: String,
        requestOptions: RequestOptions?,
    ): AdministrationDeletion = delete("organization/projects/${segment(projectId)}/users/${segment(userId)}", requestOptions)

    override suspend fun projectUsers(
        projectId: String,
        after: String?,
        limit: Int?,
        requestOptions: RequestOptions?,
    ): AdministrationPage<OrganizationProjectUser> = get("organization/projects/${segment(projectId)}/users", requestOptions) {
        after?.let { parameters.append("after", it.toString()) }
        limit?.let { parameters.append("limit", it.toString()) }
    }

    override suspend fun projectUser(
        projectId: String,
        userId: String,
        requestOptions: RequestOptions?,
    ): OrganizationProjectUser? = getOrNull("organization/projects/${segment(projectId)}/users/${segment(userId)}", requestOptions)

    override suspend fun assignProjectUserRole(
        projectId: String,
        userId: String,
        request: OrganizationRoleAssignmentRequest,
        requestOptions: RequestOptions?,
    ): OrganizationUserRoleAssignment = post("projects/${segment(projectId)}/users/${segment(userId)}/roles", request, requestOptions)

    override suspend fun removeProjectUserRole(
        projectId: String,
        userId: String,
        roleId: String,
        requestOptions: RequestOptions?,
    ): AdministrationDeletion = delete("projects/${segment(projectId)}/users/${segment(userId)}/roles/${segment(roleId)}", requestOptions)

    override suspend fun projectUserRoles(
        projectId: String,
        userId: String,
        after: String?,
        limit: Int?,
        order: SortOrder?,
        requestOptions: RequestOptions?,
    ): AdministrationPage<OrganizationAssignedRole> = get("projects/${segment(projectId)}/users/${segment(userId)}/roles", requestOptions) {
        after?.let { parameters.append("after", it.toString()) }
        limit?.let { parameters.append("limit", it.toString()) }
        order?.let { parameters.append("order", it.order) }
    }

    override suspend fun projectUserRole(
        projectId: String,
        userId: String,
        roleId: String,
        requestOptions: RequestOptions?,
    ): OrganizationAssignedRole? = getOrNull("projects/${segment(projectId)}/users/${segment(userId)}/roles/${segment(roleId)}", requestOptions)

    override suspend fun updateProjectUser(
        projectId: String,
        userId: String,
        request: OrganizationProjectUserUpdateRequest,
        requestOptions: RequestOptions?,
    ): OrganizationProjectUser = post("organization/projects/${segment(projectId)}/users/${segment(userId)}", request, requestOptions)

    override suspend fun createOrganizationRole(
        request: OrganizationRoleRequest,
        requestOptions: RequestOptions?,
    ): OrganizationRole = post("organization/roles", request, requestOptions)

    override suspend fun deleteOrganizationRole(
        roleId: String,
        requestOptions: RequestOptions?,
    ): AdministrationDeletion = delete("organization/roles/${segment(roleId)}", requestOptions)

    override suspend fun organizationRoles(
        after: String?,
        limit: Int?,
        order: SortOrder?,
        requestOptions: RequestOptions?,
    ): AdministrationPage<OrganizationRole> = get("organization/roles", requestOptions) {
        after?.let { parameters.append("after", it.toString()) }
        limit?.let { parameters.append("limit", it.toString()) }
        order?.let { parameters.append("order", it.order) }
    }

    override suspend fun organizationRole(
        roleId: String,
        requestOptions: RequestOptions?,
    ): OrganizationRole? = getOrNull("organization/roles/${segment(roleId)}", requestOptions)

    override suspend fun updateOrganizationRole(
        roleId: String,
        request: OrganizationRoleUpdateRequest,
        requestOptions: RequestOptions?,
    ): OrganizationRole = post("organization/roles/${segment(roleId)}", request, requestOptions)

    override suspend fun deleteOrganizationUser(
        userId: String,
        requestOptions: RequestOptions?,
    ): AdministrationDeletion = delete("organization/users/${segment(userId)}", requestOptions)

    override suspend fun organizationUsers(
        after: String?,
        emails: List<String>?,
        limit: Int?,
        requestOptions: RequestOptions?,
    ): AdministrationPage<OrganizationUser> = get("organization/users", requestOptions) {
        after?.let { parameters.append("after", it.toString()) }
        emails?.forEach { parameters.append("emails[]", it) }
        limit?.let { parameters.append("limit", it.toString()) }
    }

    override suspend fun organizationUser(
        userId: String,
        requestOptions: RequestOptions?,
    ): OrganizationUser? = getOrNull("organization/users/${segment(userId)}", requestOptions)

    override suspend fun assignOrganizationUserRole(
        userId: String,
        request: OrganizationRoleAssignmentRequest,
        requestOptions: RequestOptions?,
    ): OrganizationUserRoleAssignment = post("organization/users/${segment(userId)}/roles", request, requestOptions)

    override suspend fun removeOrganizationUserRole(
        userId: String,
        roleId: String,
        requestOptions: RequestOptions?,
    ): AdministrationDeletion = delete("organization/users/${segment(userId)}/roles/${segment(roleId)}", requestOptions)

    override suspend fun organizationUserRoles(
        userId: String,
        after: String?,
        limit: Int?,
        order: SortOrder?,
        requestOptions: RequestOptions?,
    ): AdministrationPage<OrganizationAssignedRole> = get("organization/users/${segment(userId)}/roles", requestOptions) {
        after?.let { parameters.append("after", it.toString()) }
        limit?.let { parameters.append("limit", it.toString()) }
        order?.let { parameters.append("order", it.order) }
    }

    override suspend fun organizationUserRole(
        userId: String,
        roleId: String,
        requestOptions: RequestOptions?,
    ): OrganizationAssignedRole? = getOrNull("organization/users/${segment(userId)}/roles/${segment(roleId)}", requestOptions)

    override suspend fun updateOrganizationUser(
        userId: String,
        request: OrganizationUserUpdateRequest,
        requestOptions: RequestOptions?,
    ): OrganizationUser = post("organization/users/${segment(userId)}", request, requestOptions)

    private fun segment(id: String): String {
        require(id.isNotEmpty() && id != "." && id != ".." && id.all { it.isLetterOrDigit() || it == '_' || it == '-' || it == '.' }) {
            "Administrative identifiers must be single path segments"
        }
        return id
    }

    private suspend inline fun <reified T : Any> get(path: String, options: RequestOptions?, crossinline query: URLBuilder.() -> Unit = {}): T =
        requester.perform { client ->
            client.get {
                url(path = path) { query() }
                requestOptions(options)
            }.body()
        }

    private suspend inline fun <reified T : Any> getOrNull(path: String, options: RequestOptions?, crossinline query: URLBuilder.() -> Unit = {}): T? =
        try { get(path, options, query) } catch (error: OpenAIAPIException) {
            if (error.statusCode == HttpStatusCode.NotFound.value) null else throw error
        }

    private suspend inline fun <reified T : Any, reified B : Any> post(path: String, request: B, options: RequestOptions?): T =
        requester.perform { client ->
            client.post {
                url(path = path)
                contentType(ContentType.Application.Json)
                setBody(request)
                requestOptions(options)
            }.body()
        }

    private suspend inline fun <reified T : Any> postEmpty(path: String, options: RequestOptions?): T =
        post(path, buildJsonObject {}, options)

    private suspend inline fun <reified T : Any> delete(path: String, options: RequestOptions?): T =
        requester.perform { client ->
            client.delete {
                url(path = path)
                requestOptions(options)
            }.body()
        }
}
