package com.aallam.openai.api.misc

import com.aallam.openai.api.container.*
import com.aallam.openai.api.file.FileId
import kotlinx.serialization.json.*
import kotlin.test.*

class TestContainers {
    @Test
    fun domainSecretDiagnosticsAreRedactedWithoutChangingTheWireValue() {
        val secret = ContainerDomainSecret("example.com", "Authorization", "private-fixture-value")
        val policy = ContainerNetworkPolicy.Allowlist(listOf("example.com"), listOf(secret))
        assertFalse(policy.toString().contains(secret.value))
        assertContains(policy.toString(), "[REDACTED]")
        assertContains(Json.encodeToJsonElement<ContainerNetworkPolicy>(policy).toString(), secret.value)
    }

    @Test
    fun completeCreationRequestPreservesNetworkSkillsAndBuilderOptions() {
        val request = containerRequest {
            name = "tools"
            expiresAfter = ContainerExpirationPolicy(20)
            fileIds = listOf(FileId("file_1"))
            memoryLimit = ContainerMemoryLimit.G4
            networkPolicy = ContainerNetworkPolicy.Allowlist(listOf("example.com"), listOf(
                ContainerDomainSecret("example.com", "API_TOKEN", "test-value"),
            ))
            skills = listOf(ContainerSkill.Reference("skill_1", "latest"),
                ContainerSkill.Inline("arithmetic", "Simple arithmetic", ContainerInlineSkillSource("UEs=")))
        }
        val expected = Json.parseToJsonElement("""{
            "name":"tools","expires_after":{"minutes":20,"anchor":"last_active_at"},
            "file_ids":["file_1"],"memory_limit":"4g",
            "network_policy":{"type":"allowlist","allowed_domains":["example.com"],"domain_secrets":[{"domain":"example.com","name":"API_TOKEN","value":"test-value"}]},
            "skills":[{"type":"skill_reference","skill_id":"skill_1","version":"latest"},{"type":"inline","name":"arithmetic","description":"Simple arithmetic","source":{"data":"UEs=","media_type":"application/zip","type":"base64"}}]
        }""")
        assertEquals(expected, Json.encodeToJsonElement(request))
        assertEquals(request, Json.decodeFromJsonElement<ContainerRequest>(expected))
        assertEquals(Json.parseToJsonElement("""{"name":"minimal"}"""), Json.encodeToJsonElement(ContainerRequest("minimal")))
        assertEquals(Json.parseToJsonElement("""{"name":"offline","network_policy":{"type":"disabled"}}"""),
            Json.encodeToJsonElement(ContainerRequest("offline", networkPolicy = ContainerNetworkPolicy.Disabled)))
    }

    @Test
    fun responseExpiryAllowsOmittedFieldsAndFileSizesRemainLong() {
        val container = Json.decodeFromString<Container>("""{
            "id":"cntr_1","created_at":1747857508,"name":"tools","status":"running",
            "expires_after":{"anchor":"last_active_at"},"network_policy":{"type":"disabled"},"memory_limit":"128g"
        }""")
        assertNull(container.expiresAfter!!.minutes)
        assertNull(container.networkPolicy!!.allowedDomains)
        assertEquals(ContainerMemoryLimit("128g"), container.memoryLimit)
        val file = Json.decodeFromString<ContainerFile>("""{
            "id":"cfile_1","bytes":3000000000,"container_id":"cntr_1","created_at":1747857509,"path":"/mnt/data/data.bin","source":"user"
        }""")
        assertEquals(3000000000L, file.bytes)
        assertEquals(ContainerId("cntr_1"), file.containerId)
    }
}
