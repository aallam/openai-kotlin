package com.aallam.openai.client

import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.file.fileSource
import com.aallam.openai.api.file.fileUpload
import com.aallam.openai.client.internal.asSource
import com.aallam.openai.client.internal.waitFileProcess
import kotlinx.coroutines.test.runTest
import ulid.ULID
import kotlin.test.*

class TestFiles : TestOpenAI() {

    @Test
    fun file() {
        runTest {
            val jsonl = """
                {"prompt": "<prompt text>", "completion": "<ideal generated text>"}
                {"prompt": "<prompt text>", "completion": "<ideal generated text>"}
                {"prompt": "<prompt text>", "completion": "<ideal generated text>"}
            """.trimIndent()
            val id = ULID.randomULID()

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
    }

    @Test
    fun files() {
        runTest {
            val response = openAI.files()
            assertNotNull(response)
            assertTrue(response.isNotEmpty())

        }
    }
}
