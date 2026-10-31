package com.aallam.openai.api.misc

import com.aallam.openai.api.admin.governance.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlin.test.*

class TestGovernanceModels {
    @Test
    fun keyRequestsPreserveProviderDiscriminatorsAndVersionedConfiguration() {
        val requests = listOf<ExternalKeyRequest>(
            ExternalKeyRequest.Aws("org_1", "arn:key", "aws", "arn:role", "ekm"),
            ExternalKeyRequest.Gcp("org_1", "us", "key", "ring", "project", "gcp", "pool", "123", "provider", "2", "psp_decryption"),
            ExternalKeyRequest.Azure("key", "azure", "tenant", "https://example.vault.azure.net", "version", "psp_decryption"),
        )
        for ((index, request) in requests.withIndex()) {
            val encoded = Json.encodeToJsonElement<ExternalKeyRequest>(request)
            assertEquals(listOf("aws", "gcp", "azure")[index], encoded.jsonObject.getValue("type").jsonPrimitive.content)
            assertEquals(request, Json.decodeFromJsonElement<ExternalKeyRequest>(encoded))
        }
        val gcp = Json.encodeToJsonElement<ExternalKeyRequest>(requests[1]).jsonObject
        assertEquals(JsonPrimitive("2"), gcp["kms_key_version"])
        assertEquals(JsonPrimitive("123"), gcp["workload_identity_project_number"])
        assertFalse(Json.encodeToJsonElement<ExternalKeyRequest>(ExternalKeyRequest.Aws("org_1", "arn:key", "aws", "arn:role")).jsonObject.containsKey("purpose"))
    }

    @Test
    fun storageRequestsAndResponsesPreserveEveryProviderVariant() {
        val providers = listOf<ExternalStorageProviderRequest>(
            ExternalStorageProviderRequest.Aws("bucket", "arn:role"),
            ExternalStorageProviderRequest.Azure("account", "container", "group", "subscription", "tenant"),
            ExternalStorageProviderRequest.Gcp("bucket", "pool", "123", "provider"),
            ExternalStorageProviderRequest.Oci("bucket", "region", "tenancy"),
        )
        for ((index, provider) in providers.withIndex()) {
            val request = ExternalStorageRequest("proj_1", provider)
            val encoded = Json.encodeToJsonElement(request)
            assertEquals(listOf("aws", "azure", "gcp", "oci")[index], encoded.jsonObject.getValue("provider").jsonObject.getValue("type").jsonPrimitive.content)
            assertEquals(request, Json.decodeFromJsonElement<ExternalStorageRequest>(encoded))
        }
        val responses = listOf<ExternalStorageProvider>(
            ExternalStorageProvider.Aws("account", "bucket", "external", "region", "arn:role"),
            ExternalStorageProvider.Azure("account", "container", "region", "group", "subscription", "tenant"),
            ExternalStorageProvider.Gcp("audience", "bucket", "region", "pool", "123", "provider"),
            ExternalStorageProvider.Oci("bucket", "region", "tenancy"),
        )
        for (provider in responses) {
            assertEquals(provider, Json.decodeFromString<ExternalStorageProvider>(Json.encodeToString<ExternalStorageProvider>(provider)))
        }
    }

    @Test
    fun hostedToolUpdatesDistinguishUnspecifiedExplicitNullAndBooleanValues() {
        val request = ProjectHostedToolPermissionsRequest(
            mcp = HostedToolPermission(false), nullFields = setOf(HostedTool.WebSearch),
        )
        val json = Json.encodeToJsonElement(request)
        assertEquals(Json.parseToJsonElement("""{"mcp":{"enabled":false},"web_search":null}"""), json)
        assertEquals(request, Json.decodeFromJsonElement<ProjectHostedToolPermissionsRequest>(json))
        assertEquals(JsonObject(emptyMap()), Json.encodeToJsonElement(ProjectHostedToolPermissionsRequest()))
        assertFailsWith<IllegalArgumentException> {
            ProjectHostedToolPermissionsRequest(mcp = HostedToolPermission(true), nullFields = setOf(HostedTool.Mcp))
        }
        assertFailsWith<IllegalArgumentException> { CertificateActivationRequest(emptyList()) }
    }

    @Test
    fun auditEventsRetainUnknownVariantPayloadsAlongsideTypedCommonFields() {
        val payload = Json.parseToJsonElement("""{
            "id":"audit_1","effective_at":1700000000,"type":"api_key.created",
            "actor":{"type":"api_key","api_key":{"id":"tracking_1","type":"service_account","service_account":{"id":"sa_1"}}},
            "project":{"id":"proj_1","name":"Example"},"api_key.created":{"id":"tracking_2","data":{"scopes":["api.model.request"]}},
            "future_field":{"nested":[1,true,null]}
        }""")
        val event = Json.decodeFromJsonElement<AuditLog>(payload)
        assertEquals("sa_1", event.actor!!.apiKey!!.serviceAccount!!.id)
        assertEquals("proj_1", event.project!!.id)
        assertEquals(JsonPrimitive("tracking_2"), event.details!!["id"])
        assertEquals(payload, event.rawData)
        assertEquals(payload, Json.encodeToJsonElement(event))
        val tenant = Json.decodeFromString<AuditLog>("""{"id":"audit_2","effective_at":1,"type":"tenant.new_event","actor":null,"details":{"id":"resource_1"}}""")
        assertNull(tenant.actor)
        assertEquals(JsonPrimitive("resource_1"), tenant.details!!["id"])
    }
}
