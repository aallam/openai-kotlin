@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.aallam.openai.client

import com.aallam.openai.api.admin.*
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.internal.api.OrganizationAdministrationApi
import com.aallam.openai.client.internal.createHttpClient
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.http.content.TextContent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineOrganizationAdministration {
    @Test
    fun oneTimeKeyValuesAreRedactedFromDiagnosticStrings() {
        val secret = "private-fixture-key"
        val adminKey = OrganizationAdminAPIKeyCreation(secret, id = "key_1")
        val serviceKey = OrganizationServiceAccountAPIKey("key_2", 1, "test", "organization.project.service_account.api_key", secret)
        val serviceAccount = OrganizationServiceAccount("svc_1", 1, "test", "organization.project.service_account", "member", serviceKey)
        for (value in listOf(adminKey, serviceKey, serviceAccount)) {
            assertFalse(value.toString().contains(secret))
            assertContains(value.toString(), "[REDACTED]")
        }
        assertEquals(secret, adminKey.value)
        assertEquals(secret, serviceKey.value)
    }

    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }
    private val fixtures = json.parseToJsonElement(requireNotNull(javaClass.getResourceAsStream("/json/administration-identity.json"))
        .bufferedReader().use { it.readText() }).jsonObject
    private val options = RequestOptions(headers = mapOf("X-Test" to "admin-fixture"), urlParameters = mapOf("extra" to "value"))

    @Test
    fun allIdentityOperationsMatchIndependentReferenceFixtures() = runTest {
        val cases = listOf(

            Case("admin_api_keys-create") { fixture -> json.encodeToJsonElement(createOrganizationAdminAPIKey(request = json.decodeFromJsonElement<OrganizationAdminAPIKeyRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("admin_api_keys-delete") { fixture -> json.encodeToJsonElement(deleteOrganizationAdminAPIKey(keyId = "key_fixture", requestOptions = options)) },
            Case("admin_api_keys-list") { fixture -> json.encodeToJsonElement(organizationAdminAPIKeys(after = "cursor", limit = 2, order = SortOrder.Descending, requestOptions = options)) },
            Case("admin_api_keys-retrieve") { fixture -> json.encodeToJsonElement(organizationAdminAPIKey(keyId = "key_fixture", requestOptions = options)) },
            Case("groups-create") { fixture -> json.encodeToJsonElement(createOrganizationGroup(request = json.decodeFromJsonElement<OrganizationGroupRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("groups-delete") { fixture -> json.encodeToJsonElement(deleteOrganizationGroup(groupId = "group_fixture", requestOptions = options)) },
            Case("groups-list") { fixture -> json.encodeToJsonElement(organizationGroups(after = "cursor", limit = 2, order = SortOrder.Descending, requestOptions = options)) },
            Case("groups-retrieve") { fixture -> json.encodeToJsonElement(organizationGroup(groupId = "group_fixture", requestOptions = options)) },
            Case("groups-roles-create") { fixture -> json.encodeToJsonElement(assignOrganizationGroupRole(groupId = "group_fixture", request = json.decodeFromJsonElement<OrganizationRoleAssignmentRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("groups-roles-delete") { fixture -> json.encodeToJsonElement(removeOrganizationGroupRole(groupId = "group_fixture", roleId = "role_fixture", requestOptions = options)) },
            Case("groups-roles-list") { fixture -> json.encodeToJsonElement(organizationGroupRoles(groupId = "group_fixture", after = "cursor", limit = 2, order = SortOrder.Descending, requestOptions = options)) },
            Case("groups-roles-retrieve") { fixture -> json.encodeToJsonElement(organizationGroupRole(groupId = "group_fixture", roleId = "role_fixture", requestOptions = options)) },
            Case("groups-update") { fixture -> json.encodeToJsonElement(updateOrganizationGroup(groupId = "group_fixture", request = json.decodeFromJsonElement<OrganizationGroupRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("groups-users-create") { fixture -> json.encodeToJsonElement(addOrganizationGroupUser(groupId = "group_fixture", request = json.decodeFromJsonElement<OrganizationGroupMemberRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("groups-users-delete") { fixture -> json.encodeToJsonElement(removeOrganizationGroupUser(groupId = "group_fixture", userId = "user_fixture", requestOptions = options)) },
            Case("groups-users-list") { fixture -> json.encodeToJsonElement(organizationGroupUsers(groupId = "group_fixture", after = "cursor", limit = 2, order = SortOrder.Descending, requestOptions = options)) },
            Case("groups-users-retrieve") { fixture -> json.encodeToJsonElement(organizationGroupUser(groupId = "group_fixture", userId = "user_fixture", requestOptions = options)) },
            Case("invites-create") { fixture -> json.encodeToJsonElement(createOrganizationInvite(request = json.decodeFromJsonElement<OrganizationInviteRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("invites-delete") { fixture -> json.encodeToJsonElement(deleteOrganizationInvite(inviteId = "invite_fixture", requestOptions = options)) },
            Case("invites-list") { fixture -> json.encodeToJsonElement(organizationInvites(after = "cursor", limit = 2, requestOptions = options)) },
            Case("invites-retrieve") { fixture -> json.encodeToJsonElement(organizationInvite(inviteId = "invite_fixture", requestOptions = options)) },
            Case("projects-api_keys-delete") { fixture -> json.encodeToJsonElement(deleteProjectAPIKey(projectId = "project_fixture", apiKeyId = "api_key_fixture", requestOptions = options)) },
            Case("projects-api_keys-list") { fixture -> json.encodeToJsonElement(projectAPIKeys(projectId = "project_fixture", after = "cursor", limit = 2, ownerProjectAccess = "inactive", requestOptions = options)) },
            Case("projects-api_keys-retrieve") { fixture -> json.encodeToJsonElement(projectAPIKey(projectId = "project_fixture", apiKeyId = "api_key_fixture", requestOptions = options)) },
            Case("projects-api_keys-update") { fixture -> json.encodeToJsonElement(updateProjectAPIKey(projectId = "project_fixture", apiKeyId = "api_key_fixture", request = json.decodeFromJsonElement<OrganizationProjectAPIKeyUpdateRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("projects-archive") { fixture -> json.encodeToJsonElement(archiveOrganizationProject(projectId = "project_fixture", requestOptions = options)) },
            Case("projects-create") { fixture -> json.encodeToJsonElement(createOrganizationProject(request = json.decodeFromJsonElement<OrganizationProjectRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("projects-groups-create") { fixture -> json.encodeToJsonElement(createProjectGroup(projectId = "project_fixture", request = json.decodeFromJsonElement<OrganizationProjectGroupRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("projects-groups-delete") { fixture -> json.encodeToJsonElement(deleteProjectGroup(projectId = "project_fixture", groupId = "group_fixture", requestOptions = options)) },
            Case("projects-groups-list") { fixture -> json.encodeToJsonElement(projectGroups(projectId = "project_fixture", after = "cursor", limit = 2, order = SortOrder.Descending, requestOptions = options)) },
            Case("projects-groups-retrieve") { fixture -> json.encodeToJsonElement(projectGroup(projectId = "project_fixture", groupId = "group_fixture", groupType = "tenant_group", requestOptions = options)) },
            Case("projects-groups-roles-create") { fixture -> json.encodeToJsonElement(assignProjectGroupRole(projectId = "project_fixture", groupId = "group_fixture", request = json.decodeFromJsonElement<OrganizationRoleAssignmentRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("projects-groups-roles-delete") { fixture -> json.encodeToJsonElement(removeProjectGroupRole(projectId = "project_fixture", groupId = "group_fixture", roleId = "role_fixture", requestOptions = options)) },
            Case("projects-groups-roles-list") { fixture -> json.encodeToJsonElement(projectGroupRoles(projectId = "project_fixture", groupId = "group_fixture", after = "cursor", limit = 2, order = SortOrder.Descending, requestOptions = options)) },
            Case("projects-groups-roles-retrieve") { fixture -> json.encodeToJsonElement(projectGroupRole(projectId = "project_fixture", groupId = "group_fixture", roleId = "role_fixture", requestOptions = options)) },
            Case("projects-list") { fixture -> json.encodeToJsonElement(organizationProjects(after = "cursor", includeArchived = true, limit = 2, requestOptions = options)) },
            Case("projects-retrieve") { fixture -> json.encodeToJsonElement(organizationProject(projectId = "project_fixture", requestOptions = options)) },
            Case("projects-roles-create") { fixture -> json.encodeToJsonElement(createProjectRole(projectId = "project_fixture", request = json.decodeFromJsonElement<OrganizationRoleRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("projects-roles-delete") { fixture -> json.encodeToJsonElement(deleteProjectRole(projectId = "project_fixture", roleId = "role_fixture", requestOptions = options)) },
            Case("projects-roles-list") { fixture -> json.encodeToJsonElement(projectRoles(projectId = "project_fixture", after = "cursor", limit = 2, order = SortOrder.Descending, requestOptions = options)) },
            Case("projects-roles-retrieve") { fixture -> json.encodeToJsonElement(projectRole(projectId = "project_fixture", roleId = "role_fixture", requestOptions = options)) },
            Case("projects-roles-update") { fixture -> json.encodeToJsonElement(updateProjectRole(projectId = "project_fixture", roleId = "role_fixture", request = json.decodeFromJsonElement<OrganizationRoleUpdateRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("projects-service_accounts-api_keys-create") { fixture -> json.encodeToJsonElement(createProjectServiceAccountAPIKey(projectId = "project_fixture", serviceAccountId = "service_account_fixture", request = json.decodeFromJsonElement<OrganizationServiceAccountAPIKeyRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("projects-service_accounts-create") { fixture -> json.encodeToJsonElement(createProjectServiceAccount(projectId = "project_fixture", request = json.decodeFromJsonElement<OrganizationServiceAccountRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("projects-service_accounts-delete") { fixture -> json.encodeToJsonElement(deleteProjectServiceAccount(projectId = "project_fixture", serviceAccountId = "service_account_fixture", requestOptions = options)) },
            Case("projects-service_accounts-list") { fixture -> json.encodeToJsonElement(projectServiceAccounts(projectId = "project_fixture", after = "cursor", limit = 2, requestOptions = options)) },
            Case("projects-service_accounts-retrieve") { fixture -> json.encodeToJsonElement(projectServiceAccount(projectId = "project_fixture", serviceAccountId = "service_account_fixture", requestOptions = options)) },
            Case("projects-service_accounts-update") { fixture -> json.encodeToJsonElement(updateProjectServiceAccount(projectId = "project_fixture", serviceAccountId = "service_account_fixture", request = json.decodeFromJsonElement<OrganizationServiceAccountUpdateRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("projects-update") { fixture -> json.encodeToJsonElement(updateOrganizationProject(projectId = "project_fixture", request = json.decodeFromJsonElement<OrganizationProjectUpdateRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("projects-users-create") { fixture -> json.encodeToJsonElement(createProjectUser(projectId = "project_fixture", request = json.decodeFromJsonElement<OrganizationProjectUserRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("projects-users-delete") { fixture -> json.encodeToJsonElement(deleteProjectUser(projectId = "project_fixture", userId = "user_fixture", requestOptions = options)) },
            Case("projects-users-list") { fixture -> json.encodeToJsonElement(projectUsers(projectId = "project_fixture", after = "cursor", limit = 2, requestOptions = options)) },
            Case("projects-users-retrieve") { fixture -> json.encodeToJsonElement(projectUser(projectId = "project_fixture", userId = "user_fixture", requestOptions = options)) },
            Case("projects-users-roles-create") { fixture -> json.encodeToJsonElement(assignProjectUserRole(projectId = "project_fixture", userId = "user_fixture", request = json.decodeFromJsonElement<OrganizationRoleAssignmentRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("projects-users-roles-delete") { fixture -> json.encodeToJsonElement(removeProjectUserRole(projectId = "project_fixture", userId = "user_fixture", roleId = "role_fixture", requestOptions = options)) },
            Case("projects-users-roles-list") { fixture -> json.encodeToJsonElement(projectUserRoles(projectId = "project_fixture", userId = "user_fixture", after = "cursor", limit = 2, order = SortOrder.Descending, requestOptions = options)) },
            Case("projects-users-roles-retrieve") { fixture -> json.encodeToJsonElement(projectUserRole(projectId = "project_fixture", userId = "user_fixture", roleId = "role_fixture", requestOptions = options)) },
            Case("projects-users-update") { fixture -> json.encodeToJsonElement(updateProjectUser(projectId = "project_fixture", userId = "user_fixture", request = json.decodeFromJsonElement<OrganizationProjectUserUpdateRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("roles-create") { fixture -> json.encodeToJsonElement(createOrganizationRole(request = json.decodeFromJsonElement<OrganizationRoleRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("roles-delete") { fixture -> json.encodeToJsonElement(deleteOrganizationRole(roleId = "role_fixture", requestOptions = options)) },
            Case("roles-list") { fixture -> json.encodeToJsonElement(organizationRoles(after = "cursor", limit = 2, order = SortOrder.Descending, requestOptions = options)) },
            Case("roles-retrieve") { fixture -> json.encodeToJsonElement(organizationRole(roleId = "role_fixture", requestOptions = options)) },
            Case("roles-update") { fixture -> json.encodeToJsonElement(updateOrganizationRole(roleId = "role_fixture", request = json.decodeFromJsonElement<OrganizationRoleUpdateRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("users-delete") { fixture -> json.encodeToJsonElement(deleteOrganizationUser(userId = "user_fixture", requestOptions = options)) },
            Case("users-list") { fixture -> json.encodeToJsonElement(organizationUsers(after = "cursor", emails = listOf("one@example.test", "two@example.test"), limit = 2, requestOptions = options)) },
            Case("users-retrieve") { fixture -> json.encodeToJsonElement(organizationUser(userId = "user_fixture", requestOptions = options)) },
            Case("users-roles-create") { fixture -> json.encodeToJsonElement(assignOrganizationUserRole(userId = "user_fixture", request = json.decodeFromJsonElement<OrganizationRoleAssignmentRequest>(fixture.getValue("body")), requestOptions = options)) },
            Case("users-roles-delete") { fixture -> json.encodeToJsonElement(removeOrganizationUserRole(userId = "user_fixture", roleId = "role_fixture", requestOptions = options)) },
            Case("users-roles-list") { fixture -> json.encodeToJsonElement(organizationUserRoles(userId = "user_fixture", after = "cursor", limit = 2, order = SortOrder.Descending, requestOptions = options)) },
            Case("users-roles-retrieve") { fixture -> json.encodeToJsonElement(organizationUserRole(userId = "user_fixture", roleId = "role_fixture", requestOptions = options)) },
            Case("users-update") { fixture -> json.encodeToJsonElement(updateOrganizationUser(userId = "user_fixture", request = json.decodeFromJsonElement<OrganizationUserUpdateRequest>(fixture.getValue("body")), requestOptions = options)) },
        )
        assertEquals(fixtures.keys, cases.map { it.name }.toSet())
        for (case in cases) {
            val fixture = fixtures.getValue(case.name).jsonObject
            var requests = 0
            val transport = transport(MockEngine { request ->
                requests++
                assertEquals(fixture.getValue("method").jsonPrimitive.content, request.method.value, case.name)
                assertEquals(fixture.getValue("path").jsonPrimitive.content, request.url.encodedPath, case.name)
                assertEquals("admin-fixture", request.headers["X-Test"], case.name)
                assertEquals("value", request.url.parameters["extra"], case.name)
                val expectedQuery = fixture.getValue("query").jsonObject
                assertEquals(expectedQuery.keys + "extra", request.url.parameters.names(), case.name)
                for ((key, values) in expectedQuery) assertEquals(values.jsonArray.map { it.jsonPrimitive.content }, request.url.parameters.getAll(key), case.name)
                fixture["body"]?.let { body ->
                    assertEquals(ContentType.Application.Json, request.body.contentType?.withoutParameters(), case.name)
                    assertEquals(body, json.parseToJsonElement((request.body as TextContent).text), case.name)
                }
                respond(fixture.getValue("response").toString(), headers = jsonHeaders)
            })
            try {
                val result = case.call(OrganizationAdministrationApi(transport), fixture)
                assertEquals(withoutNulls(fixture.getValue("response")), withoutNulls(result), case.name)
                assertEquals(1, requests, case.name)
            } finally { transport.close() }
        }
    }

    @Test
    fun onlyMissingResourcesBecomeNullAndInvalidIdentifiersNeverReachHttp() = runTest {
        var requests = 0
        var status = HttpStatusCode.NotFound
        val transport = transport(MockEngine {
            requests++
            respond("""{"error":{"message":"unavailable","type":"invalid_request_error"}}""", status, jsonHeaders)
        })
        val api = OrganizationAdministrationApi(transport)
        try {
            assertNull(api.organizationUser("user_missing"))
            status = HttpStatusCode.Unauthorized
            assertFailsWith<AuthenticationException> { api.organizationUser("user_missing") }
            val before = requests
            for (invalid in listOf("", ".", "..", "user/../roles", "id?role=owner", "id%2Fother", "id#fragment", "id\\child")) {
                assertFailsWith<IllegalArgumentException> { api.organizationUser(invalid) }
                assertFailsWith<IllegalArgumentException> { api.projectRole("proj_good", invalid) }
            }
            assertEquals(before, requests)
        } finally { transport.close() }
    }

    @Test
    fun nullUpdatesRemainExplicitWhileOmittedFieldsStayAbsent() = runTest {
        val request = OrganizationProjectUpdateRequest(name = AdministrationValue.Clear)
        val transport = transport(MockEngine { http ->
            assertEquals(json.parseToJsonElement("""{"name":null}"""), json.parseToJsonElement((http.body as TextContent).text))
            respond(fixtures.getValue("projects-update").jsonObject.getValue("response").toString(), headers = jsonHeaders)
        })
        try { OrganizationAdministrationApi(transport).updateOrganizationProject("proj_fixture", request) }
        finally { transport.close() }
        val encoded = json.encodeToJsonElement(request)
        assertEquals(request, json.decodeFromJsonElement<OrganizationProjectUpdateRequest>(encoded))
        assertEquals(buildJsonObject {}, json.encodeToJsonElement(OrganizationProjectUpdateRequest()))
        assertEquals(buildJsonObject {}, Json { encodeDefaults = true }.encodeToJsonElement(OrganizationProjectUpdateRequest()))
    }

    @Test
    fun customClientsCanOmitAdministrationCapabilities() = runTest {
        val custom = object : OrganizationAdministration {}
        assertFailsWith<UnsupportedOperationException> { custom.organizationUsers() }
        assertFailsWith<UnsupportedOperationException> { custom.createOrganizationGroup(OrganizationGroupRequest("example")) }
    }

    private fun transport(engine: MockEngine) = HttpTransport(createHttpClient(OpenAIConfig(
        token = "test", engine = engine, logging = LoggingConfig(LogLevel.None), retry = RetryStrategy(maxRetries = 0),
    )))

    private fun withoutNulls(element: JsonElement): JsonElement = when (element) {
        is JsonObject -> JsonObject(element.filterValues { it != JsonNull }.mapValues { withoutNulls(it.value) })
        is JsonArray -> JsonArray(element.map(::withoutNulls))
        else -> element
    }

    private data class Case(val name: String, val call: suspend OrganizationAdministration.(JsonObject) -> JsonElement)
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
}
