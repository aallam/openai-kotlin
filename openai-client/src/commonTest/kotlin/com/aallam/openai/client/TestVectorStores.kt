package com.aallam.openai.client

import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.file.fileSource
import com.aallam.openai.api.file.fileUpload
import com.aallam.openai.api.vectorstore.FileBatchRequest
import com.aallam.openai.api.vectorstore.VectorStoreFileRequest
import com.aallam.openai.api.vectorstore.VectorStoreRequest
import com.aallam.openai.client.internal.asSource
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class TestVectorStores : TestOpenAI() {

    @Test
    fun testVectorStore() = test {
        val request = VectorStoreRequest(name = "Support FAQ")
        val vectorStore = openAI.createVectorStore(request = request)
        assertEquals("Support FAQ", vectorStore.name)

        // Listing may be eventually consistent and paginated, so a just-created store
        // is not guaranteed to appear in the first page immediately.
        val vectorStores = openAI.vectorStores(limit = 100)
        val listedVectorStore = vectorStores.firstOrNull { it.id == vectorStore.id }
        listedVectorStore?.let { listed ->
            assertEquals(vectorStore.id, listed.id)
            assertEquals(vectorStore.name, listed.name)
        }

        val retrieved = openAI.vectorStore(id = vectorStore.id)
        assertEquals(vectorStore, retrieved)

        val updated = openAI.updateVectorStore(
            id = vectorStore.id,
            request = VectorStoreRequest(name = "Support FAQ v2")
        )
        assertEquals("Support FAQ v2", updated.name)

        val deleted = openAI.delete(vectorStore.id)
        assertEquals(true, deleted)
    }

    @Ignore
    @Test
    fun testVectorStoreFiles() = test {
        val filetxt = """
                Name: Tech Innovations Inc.
                Founded: 2010
                Industry: Technology
                CEO: Jane Doe
                Headquarters: Silicon Valley, California
                Mission: To innovate solutions that advance everyday life globally.
            """.trimIndent()

        val fileRequest = fileUpload {
            file = fileSource {
                name = "info.txt"
                source = filetxt.asSource()
            }
            purpose = Purpose("assistants")
        }

        val createdFile = openAI.file(fileRequest)

        val vectorStore = openAI.createVectorStore(
            request = VectorStoreRequest(
                name = "Company Info",
                fileIds = listOf(createdFile.id)
            )
        )
        assertEquals("Company Info", vectorStore.name)

        val vectorStoreFile = openAI.createVectorStoreFile(
            id = vectorStore.id,
            request = VectorStoreFileRequest(fileId = createdFile.id),
        )
        assertEquals(createdFile.id, vectorStoreFile.id)
        assertEquals(vectorStore.id, vectorStoreFile.vectorStoreId)

        val files = openAI.vectorStoreFiles(id = vectorStore.id)
        assertEquals(createdFile.id, files.first().id)

        openAI.delete(id = vectorStore.id, fileId = createdFile.id)

        val fileBatch = openAI.createVectorStoreFilesBatch(
            id = vectorStore.id,
            request = FileBatchRequest(fileIds = listOf(createdFile.id)),
        )

        val retrievedFileBatch = openAI.vectorStoreFileBatch(vectorStoreId = vectorStore.id, batchId = fileBatch.id)
        assertEquals(fileBatch.id, retrievedFileBatch?.id)

        val filesBatch = openAI.vectorStoreFilesBatches(vectorStoreId = vectorStore.id, batchId = fileBatch.id)
        assertEquals(createdFile.id, filesBatch.first().id)

        openAI.cancel(vectorStoreId = vectorStore.id, batchId = fileBatch.id)
        openAI.delete(id = vectorStore.id)
    }

}
