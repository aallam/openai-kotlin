package com.aallam.openai.client

import com.aallam.openai.api.admin.governance.*
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.exception.PermissionException
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.internal.api.OrganizationGovernanceApi
import com.aallam.openai.client.internal.createHttpClient
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineOrganizationGovernance {
    @Test
    fun certificateLifecyclePreservesScopeEnvelopesAndContentSelection() = runTest {
        withApi(listOf(
            expected(HttpMethod.Post, "/organization/certificates", certificate, """{"certificate":"PEM","name":"client"}"""),
            expected(HttpMethod.Get, "/organization/certificates/cert_1", certificate, query = mapOf("include[]" to listOf("content"))),
            expected(HttpMethod.Post, "/organization/certificates/cert_1", certificate, """{"name":"renamed"}"""),
            expected(HttpMethod.Get, "/organization/certificates", page(certificate), query = mapOf("after" to listOf("cert_0"), "limit" to listOf("2"), "order" to listOf("asc"))),
            expected(HttpMethod.Post, "/organization/certificates/activate", activation("organization.certificate.activation"), """{"certificate_ids":["cert_1"]}"""),
            expected(HttpMethod.Post, "/organization/certificates/deactivate", activation("organization.certificate.deactivation"), """{"certificate_ids":["cert_1"]}"""),
            expected(HttpMethod.Get, "/organization/projects/proj_1/certificates", page(certificate)),
            expected(HttpMethod.Post, "/organization/projects/proj_1/certificates/activate", activation("organization.project.certificate.activation"), """{"certificate_ids":["cert_1"]}"""),
            expected(HttpMethod.Post, "/organization/projects/proj_1/certificates/deactivate", activation("organization.project.certificate.deactivation"), """{"certificate_ids":["cert_1"]}"""),
            expected(HttpMethod.Delete, "/organization/certificates/cert_1", """{"id":"cert_1","object":"certificate.deleted"}"""),
        )) { api ->
            val options = RequestOptions(headers = mapOf("X-Test" to "value"))
            assertEquals("cert_1", api.uploadOrganizationCertificate(CertificateUploadRequest("PEM", "client"), options).id)
            assertEquals("PEM", api.organizationCertificate("cert_1", listOf("content"))!!.certificateDetails.content)
            assertEquals("cert_1", api.updateOrganizationCertificate("cert_1", CertificateUpdateRequest("renamed")).id)
            val listed = api.organizationCertificates(CertificateListQuery("cert_0", 2, SortOrder.Ascending))
            assertTrue(listed.hasMore!!)
            assertEquals("last", listed.lastId)
            val request = CertificateActivationRequest(listOf("cert_1"))
            assertEquals("organization.certificate.activation", api.activateOrganizationCertificates(request).objectType)
            assertEquals("organization.certificate.deactivation", api.deactivateOrganizationCertificates(request).objectType)
            assertEquals(1, api.projectCertificates("proj_1").size)
            assertEquals("organization.project.certificate.activation", api.activateProjectCertificates("proj_1", request).objectType)
            assertEquals("organization.project.certificate.deactivation", api.deactivateProjectCertificates("proj_1", request).objectType)
            assertEquals("cert_1", api.deleteOrganizationCertificate("cert_1").id)
        }
    }

    @Test
    fun retentionAndPermissionUpdatesKeepNullableFieldsDistinctFromOmission() = runTest {
        withApi(listOf(
            expected(HttpMethod.Get, "/organization/data_retention", orgRetention),
            expected(HttpMethod.Post, "/organization/data_retention", orgRetention, """{"retention_type":"zero_data_retention"}"""),
            expected(HttpMethod.Get, "/organization/projects/proj_1/data_retention", projectRetention),
            expected(HttpMethod.Post, "/organization/projects/proj_1/data_retention", projectRetention, """{"retention_type":"organization_default"}"""),
            expected(HttpMethod.Get, "/organization/projects/proj_1/model_permissions", modelPermissions),
            expected(HttpMethod.Post, "/organization/projects/proj_1/model_permissions", modelPermissions, """{"mode":"allow_list","model_ids":["test-model"]}"""),
            expected(HttpMethod.Delete, "/organization/projects/proj_1/model_permissions", """{"deleted":true,"object":"project.model_permissions.deleted"}"""),
            expected(HttpMethod.Get, "/organization/projects/proj_1/hosted_tool_permissions", hostedPermissions),
            expected(HttpMethod.Post, "/organization/projects/proj_1/hosted_tool_permissions", hostedPermissions, """{"mcp":{"enabled":false},"web_search":null}"""),
        )) { api ->
            assertEquals("zero_data_retention", api.organizationDataRetention().type)
            assertEquals("zero_data_retention", api.updateOrganizationDataRetention(OrganizationRetentionRequest(OrganizationRetentionType.ZeroDataRetention)).type)
            assertEquals("organization_default", api.projectDataRetention("proj_1").type)
            assertEquals("organization_default", api.updateProjectDataRetention("proj_1", ProjectRetentionRequest(ProjectRetentionType.OrganizationDefault)).type)
            assertEquals(listOf("test-model"), api.projectModelPermissions("proj_1")!!.modelIds)
            assertEquals(ModelPermissionMode.AllowList, api.updateProjectModelPermissions("proj_1", ProjectModelPermissionsRequest(ModelPermissionMode.AllowList, listOf("test-model"))).mode)
            assertTrue(api.deleteProjectModelPermissions("proj_1").deleted)
            assertTrue(api.projectHostedToolPermissions("proj_1").codeInterpreter.enabled)
            assertFalse(api.updateProjectHostedToolPermissions("proj_1", ProjectHostedToolPermissionsRequest(
                mcp = HostedToolPermission(false), nullFields = setOf(HostedTool.WebSearch),
            )).mcp.enabled)
        }
    }

    @Test
    fun externalKeyAndStorageLifecycleUseTheDocumentedResponseShapes() = runTest {
        withApi(listOf(
            expected(HttpMethod.Post, "/organization/external_keys", externalKey, """{"type":"aws","external_id":"org_1","kms_arn":"arn:key","name":"kms","role_arn":"arn:role","purpose":"ekm"}"""),
            expected(HttpMethod.Get, "/organization/external_keys/key_1", externalKey),
            expected(HttpMethod.Get, "/organization/external_keys", page(externalKey), query = mapOf("after" to listOf("key_0"), "limit" to listOf("3"), "order" to listOf("desc"), "purpose" to listOf("ekm"))),
            expected(HttpMethod.Post, "/organization/external_keys/key_1/validate", """{"status":"valid"}"""),
            expected(HttpMethod.Delete, "/organization/external_keys/key_1", externalKey),
            expected(HttpMethod.Post, "/organization/external_storage", storage, """{"project_id":"proj_1","provider":{"type":"aws","bucket":"bucket","role_arn":"arn:role"}}"""),
            expected(HttpMethod.Get, "/organization/external_storage/store_1", storage),
            expected(HttpMethod.Get, "/organization/external_storage", page(storage), query = mapOf("after" to listOf("store_0"), "limit" to listOf("4"), "order" to listOf("asc"), "project_id" to listOf("proj_1"))),
            expected(HttpMethod.Post, "/organization/external_storage/store_1/validate", storage),
            expected(HttpMethod.Delete, "/organization/external_storage/store_1", """{"id":"store_1","deleted":true,"object":"organization.external_storage.deleted"}"""),
        )) { api ->
            assertEquals("key_1", api.registerExternalKey(ExternalKeyRequest.Aws("org_1", "arn:key", "kms", "arn:role", "ekm")).id)
            assertEquals("arn:key", api.externalKey("key_1")!!.kmsArn)
            assertEquals("last", api.externalKeys(ExternalKeyListQuery("key_0", 3, SortOrder.Descending, "ekm")).lastId)
            assertEquals("valid", api.validateExternalKey("key_1").status)
            assertEquals("key_1", api.deleteExternalKey("key_1").id)
            assertEquals("store_1", api.registerExternalStorage(ExternalStorageRequest("proj_1", ExternalStorageProviderRequest.Aws("bucket", "arn:role"))).id)
            assertIs<ExternalStorageProvider.Aws>(api.externalStorage("store_1")!!.provider)
            assertTrue(api.externalStorageConfigurations(ExternalStorageListQuery("store_0", 4, SortOrder.Ascending, "proj_1")).hasMore!!)
            assertEquals("validated", api.validateExternalStorage("store_1").status)
            assertTrue(api.deleteExternalStorage("store_1").deleted)
        }
    }

    @Test
    fun auditAndRateLimitQueriesKeepArraysRangesAndCursorMetadata() = runTest {
        val event = """{"id":"audit_1","effective_at":1700000000,"type":"tenant.new_event","actor":{"type":"session","session":{"ip_address":"127.0.0.1","user":{"id":"user_1","email":"test@example.com"}}},"details":{"future_field":true},"extra":{"kept":true}}"""
        withApi(listOf(
            expected(HttpMethod.Get, "/organization/audit_logs", page(event), query = mapOf(
                "actor_emails[]" to listOf("one@example.com", "two@example.com"), "actor_ids[]" to listOf("user_1"),
                "after" to listOf("audit_0"), "before" to listOf("audit_2"), "limit" to listOf("2"),
                "effective_at[gt]" to listOf("1"), "effective_at[gte]" to listOf("2"), "effective_at[lt]" to listOf("4"), "effective_at[lte]" to listOf("5"),
                "event_types[]" to listOf("tenant.new_event"), "project_ids[]" to listOf("proj_1"), "resource_ids[]" to listOf("resource_1"), "tenant_only" to listOf("true"),
            )),
            expected(HttpMethod.Get, "/organization/projects/proj_1/rate_limits", page(rateLimit), query = mapOf("after" to listOf("rate_0"), "before" to listOf("rate_2"), "limit" to listOf("10"))),
            expected(HttpMethod.Post, "/organization/projects/proj_1/rate_limits/rate_1", rateLimit, """{"max_requests_per_1_minute":12,"max_tokens_per_1_minute":3000,"batch_1_day_max_input_tokens":4000000000,"max_audio_megabytes_per_1_minute":5,"max_images_per_1_minute":6,"max_requests_per_1_day":7000}"""),
        )) { api ->
            val logs = api.auditLogs(AuditLogQuery(
                actorEmails = listOf("one@example.com", "two@example.com"), actorIds = listOf("user_1"),
                after = "audit_0", before = "audit_2", effectiveAt = AuditLogTimeRange(1, 2, 4, 5),
                eventTypes = listOf("tenant.new_event"), limit = 2, projectIds = listOf("proj_1"), resourceIds = listOf("resource_1"), tenantOnly = true,
            ))
            assertEquals("test@example.com", logs.single().actor!!.session!!.user!!.email)
            assertEquals(JsonPrimitive(true), logs.single().details!!["future_field"])
            assertNotNull(logs.single().rawData["extra"])
            assertEquals("last", api.projectRateLimits("proj_1", ProjectRateLimitQuery("rate_0", "rate_2", 10)).lastId)
            val updated = api.updateProjectRateLimit("proj_1", "rate_1", ProjectRateLimitRequest(12, 3000, 4000000000, 5, 6, 7000))
            assertEquals(4000000000L, updated.batchInputTokensPerDay)
        }
    }

    @Test
    fun missingResourcesAreNullableButOtherApiFailuresPropagate() = runTest {
        var status = HttpStatusCode.NotFound
        val engine = MockEngine { respond("""{"error":{"message":"Unavailable","type":"invalid_request_error"}}""", status, jsonHeaders) }
        withEngine(engine) { api ->
            assertNull(api.organizationCertificate("missing"))
            assertNull(api.externalKey("missing"))
            assertNull(api.externalStorage("missing"))
            assertNull(api.projectModelPermissions("missing"))
            status = HttpStatusCode.Forbidden
            assertFailsWith<PermissionException> { api.organizationCertificate("missing") }
            assertFailsWith<PermissionException> { api.externalKey("missing") }
            assertFailsWith<PermissionException> { api.externalStorage("missing") }
            assertFailsWith<PermissionException> { api.projectModelPermissions("missing") }
        }
    }

    @Test
    fun identifiersCannotAlterRoutesAndCustomClientsNeedNoNewMethods() = runTest {
        withEngine(MockEngine { error("Invalid identifiers must fail before HTTP") }) { api ->
            for (id in listOf("", ".", "..", "../other", "a/b", "a\\b", "a?query", "a#fragment", "%2f", "a\nb")) {
                assertFailsWith<IllegalArgumentException> { api.organizationCertificate(id) }
                assertFailsWith<IllegalArgumentException> { api.projectDataRetention(id) }
            }
        }
        val custom = object : OrganizationGovernance {}
        assertFailsWith<UnsupportedOperationException> { custom.auditLogs() }
        assertFailsWith<UnsupportedOperationException> { custom.organizationDataRetention() }
    }

    private data class Exchange(val method: HttpMethod, val path: String, val response: String, val body: String?, val query: Map<String, List<String>>)

    private fun expected(method: HttpMethod, path: String, response: String, body: String? = null, query: Map<String, List<String>> = emptyMap()) =
        Exchange(method, path, response, body, query)

    private suspend fun withApi(exchanges: List<Exchange>, block: suspend (OrganizationGovernance) -> Unit) {
        var index = 0
        withEngine(MockEngine { request ->
            val expected = exchanges[index++]
            assertEquals(expected.method, request.method)
            assertEquals("/v1${expected.path}", request.url.encodedPath)
            assertEquals(expected.query, request.url.parameters.entries().associate { it.key to it.value })
            val body = request.body.toByteArray().decodeToString()
            if (expected.body == null) assertEquals("", body)
            else assertEquals(Json.parseToJsonElement(expected.body), Json.parseToJsonElement(body))
            if (expected.path == "/organization/certificates" && request.method == HttpMethod.Post) assertEquals("value", request.headers["X-Test"])
            respond(expected.response, headers = jsonHeaders)
        }, block)
        assertEquals(exchanges.size, index, "Every documented operation must execute")
    }

    private suspend fun withEngine(engine: MockEngine, block: suspend (OrganizationGovernance) -> Unit) {
        val transport = HttpTransport(createHttpClient(OpenAIConfig(token = "test", engine = engine, retry = RetryStrategy(maxRetries = 0), logging = LoggingConfig(LogLevel.None))))
        try { block(OrganizationGovernanceApi(transport)) } finally { transport.close() }
    }

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val certificate = """{"id":"cert_1","created_at":1700000000,"certificate_details":{"content":"PEM","valid_at":1,"expires_at":2},"name":null,"object":"certificate"}"""
    private fun activation(type: String) = """{"object":"$type","data":[$certificate]}"""
    private fun page(item: String) = """{"object":"list","data":[$item],"has_more":true,"first_id":"first","last_id":"last"}"""
    private val orgRetention = """{"object":"organization.data_retention","type":"zero_data_retention"}"""
    private val projectRetention = """{"object":"project.data_retention","type":"organization_default"}"""
    private val modelPermissions = """{"object":"project.model_permissions","mode":"allow_list","model_ids":["test-model"]}"""
    private val hostedPermissions = """{"code_interpreter":{"enabled":true},"file_search":{"enabled":true},"image_generation":{"enabled":true},"mcp":{"enabled":false},"web_search":{"enabled":true}}"""
    private val externalKey = """{"id":"key_1","api_project_ids":["proj_1"],"created_at":1700000000,"name":"kms","object":"organization.external_key","type":"aws","workspace_id":"org_1","kms_arn":"arn:key"}"""
    private val storage = """{"id":"store_1","created_at":1700000000,"geography":"us","object":"organization.external_storage","project_id":"proj_1","status":"validated","provider":{"type":"aws","account_id":"account_1","bucket":"bucket","external_id":"org_1","region":"us-east-1","role_arn":"arn:role"}}"""
    private val rateLimit = """{"id":"rate_1","object":"project.rate_limit","model":"test-model","max_requests_per_1_minute":12,"max_tokens_per_1_minute":3000,"batch_1_day_max_input_tokens":4000000000}"""
}
