package com.aallam.openai.client

import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.skill.*
import com.aallam.openai.client.internal.withTestCleanup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.io.Buffer
import kotlin.test.*

class TestSkills : TestOpenAI() {
    @Test
    fun uploadVersionDefaultAndContentLifecycle() = test {
        val client = OpenAI(OpenAIConfig(token = token, logging = LoggingConfig(LogLevel.None), retry = RetryStrategy(maxRetries = 0)))
        try {
            withTestCleanup {
                val skill = client.createSkill(SkillUploadRequest(files("SDK lifecycle fixture.")))
                var deleted = false
                afterTest { if (!deleted) client.deleteSkill(skill.id) }
                val initial = awaitVersion(client, skill.id, skill.defaultVersion)
                assertEquals(skill.id, client.retrieveSkill(skill.id)?.id)
                assertTrue(client.skillsPage(SkillListQuery(limit = 100)).any { it.id == skill.id })

                val uploaded = client.createSkillVersion(skill.id, SkillVersionUploadRequest(files("SDK version fixture."), setAsDefault = false))
                val version = awaitVersion(client, skill.id, uploaded.version)
                assertEquals(uploaded.id, version.id)
                assertTrue(client.skillVersionsPage(skill.id).any { it.id == version.id })
                assertEquals(version.version, client.updateSkillDefaultVersion(skill.id, SkillDefaultVersionRequest(version.version)).defaultVersion)

                for (bundle in listOf(client.downloadSkill(skill.id), client.downloadSkillVersion(skill.id, version.version))) {
                    assertTrue(bundle.size >= 4)
                    assertContentEquals(byteArrayOf(80, 75), bundle.copyOfRange(0, 2))
                }
                assertTrue(client.deleteSkillVersion(skill.id, initial.version))
                assertTrue(client.deleteSkill(skill.id))
                deleted = true
            }
        } finally {
            client.close()
        }
    }

    private fun files(description: String) = SkillFiles.Directory(listOf(FileSource(
        "openai-kotlin-fixture/SKILL.md",
        Buffer().apply { write("---\nname: openai-kotlin-skill-fixture\ndescription: $description\n---\n\nTransport-only fixture.\n".encodeToByteArray()) },
    )))

    private suspend fun awaitVersion(client: OpenAI, id: SkillId, version: String): SkillVersion = withContext(Dispatchers.Default) {
        // The upload contract explicitly permits a short 404 window before a new version is available.
        withTimeout(20_000) {
            var result = client.retrieveSkillVersion(id, version)
            while (result == null) {
                delay(250)
                result = client.retrieveSkillVersion(id, version)
            }
            result
        }
    }
}
