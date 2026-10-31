package com.aallam.openai.api.misc

import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.skill.*
import kotlinx.io.Buffer
import kotlinx.serialization.json.*
import kotlin.test.*

class TestSkills {
    @Test
    fun metadataKeepsDefaultLatestAndVersionResourceIdentitySeparate() {
        val skill = Json.decodeFromString<Skill>("""{
            "id":"skill_1","created_at":1747857508,"default_version":"1","latest_version":"2","name":"arithmetic","description":"Simple calculations"
        }""")
        assertEquals("1", skill.defaultVersion)
        assertEquals("2", skill.latestVersion)
        val version = Json.decodeFromString<SkillVersion>("""{
            "id":"skillver_2","created_at":1747857509,"skill_id":"skill_1","version":"2","name":"arithmetic","description":"Updated calculations"
        }""")
        assertEquals(SkillVersionId("skillver_2"), version.id)
        assertEquals("2", version.version)
        assertEquals(skill.id, version.skillId)
        assertEquals(Json.parseToJsonElement("""{"default_version":"2"}"""),
            Json.encodeToJsonElement(SkillDefaultVersionRequest(version.version)))
    }

    @Test
    fun requestBuildersRetainUploadKindAndDefaultPointerChoice() {
        val directory = SkillFiles.Directory(listOf(FileSource("arithmetic/SKILL.md", Buffer())))
        assertSame(directory, skillUploadRequest { files = directory }.files)
        val zip = SkillFiles.Zip(FileSource("arithmetic.zip", Buffer()))
        val version = skillVersionUploadRequest { files = zip; setAsDefault = false }
        assertSame(zip, version.files)
        assertEquals(false, version.setAsDefault)
        assertNull(SkillVersionUploadRequest(zip).setAsDefault)
        assertFailsWith<IllegalArgumentException> { SkillFiles.Directory(emptyList()) }
    }
}
