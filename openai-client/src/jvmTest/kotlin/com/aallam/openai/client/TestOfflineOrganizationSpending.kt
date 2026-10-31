package com.aallam.openai.client

import com.aallam.openai.api.admin.*
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.exception.PermissionException
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.internal.api.OrganizationSpendingApi
import com.aallam.openai.client.internal.createHttpClient
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineOrganizationSpending {
    @Test
    fun hardLimitRoutesRetainCentsEnforcementAndIdlessDeletionForBothScopes() = runTest {
        val calls = mutableListOf<String>()
        withClient(MockEngine { request ->
            calls += "${request.method.value} ${request.url.encodedPath}"
            assertEquals("spend", request.headers["X-Test"])
            assertEquals("yes", request.url.parameters["trace"])
            val scope = when (request.url.encodedPath) {
                "/v1/organization/spend_limit" -> "organization"
                "/v1/organization/projects/proj_1/spend_limit" -> "project"
                else -> error("Unexpected limit route ${request.url}")
            }
            when (request.method) {
                HttpMethod.Post -> {
                    assertEquals(ContentType.Application.Json, request.body.contentType?.withoutParameters())
                    assertEquals(Json.parseToJsonElement("""{"threshold_amount":12345678901,"currency":"USD","interval":"month"}"""),
                        Json.parseToJsonElement(request.body.toByteArray().decodeToString()))
                    respond(limitJson(scope), headers = jsonHeaders)
                }
                HttpMethod.Get -> respond(limitJson(scope), headers = jsonHeaders)
                HttpMethod.Delete -> respond("""{"object":"$scope.spend_limit.deleted","deleted":true}""", headers = jsonHeaders)
                else -> error("Unexpected limit method ${request.method}")
            }
        }) { client ->
            val request = spendLimitRequest { thresholdAmount = 12345678901L }
            val organization = client.updateOrganizationSpendLimit(request, options)
            assertEquals(12345678901L, organization.thresholdAmount)
            assertEquals("organization.spend_limit", organization.objectType)
            assertEquals("enforcing", organization.enforcement.status)
            assertEquals(organization, client.organizationSpendLimit(options))
            val organizationDeleted = client.deleteOrganizationSpendLimit(options)
            assertTrue(organizationDeleted.deleted)
            assertEquals("organization.spend_limit.deleted", organizationDeleted.objectType)
            val project = client.updateProjectSpendLimit("proj_1", request, options)
            assertEquals("project.spend_limit", project.objectType)
            assertEquals(project, client.projectSpendLimit("proj_1", options))
            val projectDeleted = client.deleteProjectSpendLimit("proj_1", options)
            assertTrue(projectDeleted.deleted)
            assertEquals("project.spend_limit.deleted", projectDeleted.objectType)
        }
        assertEquals(listOf(
            "POST /v1/organization/spend_limit", "GET /v1/organization/spend_limit", "DELETE /v1/organization/spend_limit",
            "POST /v1/organization/projects/proj_1/spend_limit", "GET /v1/organization/projects/proj_1/spend_limit", "DELETE /v1/organization/projects/proj_1/spend_limit",
        ), calls)
    }

    @Test
    fun malformedProjectAndAlertIdsCannotRedirectAnySpendingMutation() = runTest {
        var requests = 0
        withClient(MockEngine { requests++; error("Malformed IDs must not reach HTTP") }) { client ->
            val limit = SpendLimitRequest(1)
            val alert = SpendAlertRequest(0, SpendAlertEmailChannel(emptyList()))
            val invalid = listOf(".", "..", "proj_1\\other", "proj_1/other", "proj_1?other", "proj_1#other", "%2e%2e") +
                (0..31).map { "proj_${it.toChar()}1" } + (127..159).map { "proj_${it.toChar()}1" }
            for (id in invalid) {
                assertFailsWith<IllegalArgumentException> { client.updateProjectSpendLimit(id, limit) }
                assertFailsWith<IllegalArgumentException> { client.deleteProjectSpendLimit(id) }
                assertFailsWith<IllegalArgumentException> { client.createProjectSpendAlert(id, alert) }
                assertFailsWith<IllegalArgumentException> { client.updateProjectSpendAlert(id, "alert_1", alert) }
                assertFailsWith<IllegalArgumentException> { client.deleteProjectSpendAlert(id, "alert_1") }
                assertFailsWith<IllegalArgumentException> { client.updateOrganizationSpendAlert(id, alert) }
                assertFailsWith<IllegalArgumentException> { client.deleteOrganizationSpendAlert(id) }
                assertFailsWith<IllegalArgumentException> { client.updateProjectSpendAlert("proj_1", id, alert) }
                assertFailsWith<IllegalArgumentException> { client.deleteProjectSpendAlert("proj_1", id) }
            }
        }
        assertEquals(0, requests)
    }

    @Test
    fun allAlertOperationsRetainReplacementBodiesEmailMetadataAndPageCursors() = runTest {
        val calls = mutableListOf<String>()
        withClient(MockEngine { request ->
            calls += "${request.method.value} ${request.url.encodedPath}"
            assertEquals("spend", request.headers["X-Test"])
            assertEquals("yes", request.url.parameters["trace"])
            val scope = when (request.url.encodedPath) {
                "/v1/organization/spend_alerts", "/v1/organization/spend_alerts/alert_1" -> "organization"
                "/v1/organization/projects/proj_1/spend_alerts", "/v1/organization/projects/proj_1/spend_alerts/alert_1" -> "project"
                else -> error("Unexpected alert route ${request.url}")
            }
            val isItem = request.url.encodedPath.endsWith("/alert_1")
            when {
                request.method == HttpMethod.Post -> {
                    assertEquals(ContentType.Application.Json, request.body.contentType?.withoutParameters())
                    val expected = if (isItem) replacementBody else createBody
                    assertEquals(Json.parseToJsonElement(expected), Json.parseToJsonElement(request.body.toByteArray().decodeToString()))
                    respond(alertJson(scope, if (isItem) 15000 else 0), headers = jsonHeaders)
                }
                request.method == HttpMethod.Get && isItem -> respond(alertJson(scope, 0), headers = jsonHeaders)
                request.method == HttpMethod.Get -> {
                    if (scope == "organization") {
                        assertEquals("alert_0", request.url.parameters["after"])
                        assertNull(request.url.parameters["before"])
                        assertEquals("2", request.url.parameters["limit"])
                        assertEquals("desc", request.url.parameters["order"])
                    } else {
                        assertEquals("alert_2", request.url.parameters["before"])
                        assertNull(request.url.parameters["after"])
                        assertEquals("0", request.url.parameters["limit"])
                        assertEquals("asc", request.url.parameters["order"])
                    }
                    respond("""{"object":"list","data":[${alertJson(scope, 0)}],"has_more":true,"first_id":"alert_1","last_id":"alert_1"}""", headers = jsonHeaders)
                }
                request.method == HttpMethod.Delete && isItem -> respond("""{"id":"alert_1","object":"$scope.spend_alert.deleted","deleted":true}""", headers = jsonHeaders)
                else -> error("Unexpected alert method ${request.method}")
            }
        }) { client ->
            val create = spendAlertRequest {
                thresholdAmount = 0
                notificationChannel = SpendAlertEmailChannel(listOf("finance@example.com"), "Billing")
            }
            val replace = create.copy(thresholdAmount = 15000)
            val organization = client.createOrganizationSpendAlert(create, options)
            assertEquals("organization.spend_alert", organization.objectType)
            assertEquals("Billing", organization.notificationChannel.subjectPrefix)
            assertEquals(listOf("finance@example.com"), organization.notificationChannel.recipients)
            assertEquals(organization, client.organizationSpendAlert("alert_1", options))
            assertEquals(15000L, client.updateOrganizationSpendAlert("alert_1", replace, options).thresholdAmount)
            val organizationPage = client.organizationSpendAlerts(after = "alert_0", limit = 2, order = SortOrder.Descending, requestOptions = options)
            assertEquals("list", organizationPage.objectType)
            assertTrue(organizationPage.hasMore)
            assertEquals("alert_1", organizationPage.firstId)
            assertEquals("alert_1", organizationPage.lastId)
            assertEquals(organization, organizationPage.single())
            assertEquals(SpendAlertDeleted("alert_1", true, "organization.spend_alert.deleted"), client.deleteOrganizationSpendAlert("alert_1", options))
            val project = client.createProjectSpendAlert("proj_1", create, options)
            assertEquals("project.spend_alert", project.objectType)
            assertEquals(project, client.projectSpendAlert("proj_1", "alert_1", options))
            assertEquals(15000L, client.updateProjectSpendAlert("proj_1", "alert_1", replace, options).thresholdAmount)
            val projectPage = client.projectSpendAlerts("proj_1", before = "alert_2", limit = 0, order = SortOrder.Ascending, requestOptions = options)
            assertEquals(project, projectPage.single())
            assertEquals(SpendAlertDeleted("alert_1", true, "project.spend_alert.deleted"), client.deleteProjectSpendAlert("proj_1", "alert_1", options))
        }
        assertEquals(listOf(
            "POST /v1/organization/spend_alerts", "GET /v1/organization/spend_alerts/alert_1", "POST /v1/organization/spend_alerts/alert_1",
            "GET /v1/organization/spend_alerts", "DELETE /v1/organization/spend_alerts/alert_1",
            "POST /v1/organization/projects/proj_1/spend_alerts", "GET /v1/organization/projects/proj_1/spend_alerts/alert_1",
            "POST /v1/organization/projects/proj_1/spend_alerts/alert_1", "GET /v1/organization/projects/proj_1/spend_alerts", "DELETE /v1/organization/projects/proj_1/spend_alerts/alert_1",
        ), calls)
    }

    @Test
    fun apiFailuresAreNeverReportedAsSuccessfulSettingsChanges() = runTest {
        var status = HttpStatusCode.BadRequest
        withClient(MockEngine {
            respond("""{"error":{"message":"Invalid spend control","type":"invalid_request_error","param":"threshold_amount","code":"invalid_value"}}""", status, jsonHeaders)
        }) { client ->
            val error = assertFailsWith<InvalidRequestException> { client.updateOrganizationSpendLimit(SpendLimitRequest(1)) }
            assertEquals("threshold_amount", error.error.detail?.param)
            assertEquals("invalid_value", error.error.detail?.code)
            status = HttpStatusCode.Unauthorized
            assertFailsWith<AuthenticationException> { client.organizationSpendLimit() }
            status = HttpStatusCode.Forbidden
            assertFailsWith<PermissionException> { client.deleteProjectSpendLimit("proj_1") }
            status = HttpStatusCode.NotFound
            assertFailsWith<InvalidRequestException> { client.deleteOrganizationSpendAlert("missing") }
        }
    }

    @Test
    fun defaultsRemainOptionalForCustomClientsAndIdsCannotChangeResourcePaths() = runTest {
        val custom = object : OrganizationSpending {}
        val limit = SpendLimitRequest(1)
        val alert = SpendAlertRequest(0, SpendAlertEmailChannel(emptyList()))
        val operations = listOf<suspend () -> Any>(
            { custom.organizationSpendLimit() }, { custom.updateOrganizationSpendLimit(limit) }, { custom.deleteOrganizationSpendLimit() },
            { custom.projectSpendLimit("proj_1") }, { custom.updateProjectSpendLimit("proj_1", limit) }, { custom.deleteProjectSpendLimit("proj_1") },
            { custom.createOrganizationSpendAlert(alert) }, { custom.organizationSpendAlert("alert_1") }, { custom.updateOrganizationSpendAlert("alert_1", alert) },
            { custom.organizationSpendAlerts() }, { custom.deleteOrganizationSpendAlert("alert_1") },
            { custom.createProjectSpendAlert("proj_1", alert) }, { custom.projectSpendAlert("proj_1", "alert_1") },
            { custom.updateProjectSpendAlert("proj_1", "alert_1", alert) }, { custom.projectSpendAlerts("proj_1") }, { custom.deleteProjectSpendAlert("proj_1", "alert_1") },
        )
        for (operation in operations) assertFailsWith<UnsupportedOperationException> { operation() }
        withClient(MockEngine { error("No HTTP request should be made") }) { client ->
            assertFailsWith<IllegalArgumentException> { client.projectSpendLimit("proj_1/other") }
            assertFailsWith<IllegalArgumentException> { client.organizationSpendAlert("alert_1?other") }
        }
    }

    private suspend fun withClient(engine: MockEngine, block: suspend (OrganizationSpending) -> Unit) {
        val transport = HttpTransport(createHttpClient(OpenAIConfig("test", engine = engine, logging = LoggingConfig(LogLevel.None), retry = RetryStrategy(maxRetries = 0))))
        try { block(OrganizationSpendingApi(transport)) } finally { transport.close() }
    }

    private fun limitJson(scope: String): String = """{"threshold_amount":12345678901,"currency":"USD","interval":"month","enforcement":{"status":"enforcing"},"object":"$scope.spend_limit"}"""
    private fun alertJson(scope: String, threshold: Int): String = """{"id":"alert_1","threshold_amount":$threshold,"currency":"USD","interval":"month","notification_channel":{"recipients":["finance@example.com"],"type":"email","subject_prefix":"Billing"},"object":"$scope.spend_alert"}"""

    private val createBody = """{"threshold_amount":0,"currency":"USD","interval":"month","notification_channel":{"recipients":["finance@example.com"],"type":"email","subject_prefix":"Billing"}}"""
    private val replacementBody = """{"threshold_amount":15000,"currency":"USD","interval":"month","notification_channel":{"recipients":["finance@example.com"],"type":"email","subject_prefix":"Billing"}}"""
    private val options = RequestOptions(headers = mapOf("X-Test" to "spend"), urlParameters = mapOf("trace" to "yes"))
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
}
