package com.aallam.openai.client

import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.file.fileSource
import com.aallam.openai.api.file.fileUpload
import com.aallam.openai.client.internal.asSource
import com.aallam.openai.client.internal.waitFileProcess
import kotlin.test.*

class TestFiles : TestOpenAI() {

    @Test
    fun file() = test {
        val jsonl = """
                {"prompt": "<prompt text>", "completion": "<ideal generated text>"}
                {"prompt": "<prompt text>", "completion": "<ideal generated text>"}
                {"prompt": "<prompt text>", "completion": "<ideal generated text>"}
            """.trimIndent()
        val id = "d227742e-c572-4f51-b8a3-89f1d5105ebe"

        val source = fileSource {
            name = "$id.jsonl"
            source = jsonl.asSource()
        }
        val request = fileUpload {
            file = source
            purpose = Purpose("fine-tune")
        }

        // Create file
        val fileCreate = openAI.file(request)
        assertEquals(source.name, fileCreate.filename)

        // Get created file
        openAI.waitFileProcess(fileCreate.id)

        val bytes = openAI.download(fileCreate.id)
        val decoded = bytes.decodeToString()
        assertEquals(jsonl, decoded)

        // Delete file
        openAI.delete(fileCreate.id)

        // Check deleted file
        val fileGetAfterDelete = openAI.file(fileCreate.id)
        assertNull(fileGetAfterDelete)
    }

    @Test
    fun files() = test {
        val response = openAI.files()
        assertNotNull(response)
        assertTrue(response.isNotEmpty())

    }
}
