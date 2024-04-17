package com.aallam.openai.client

import com.aallam.openai.api.vectorstore.VectorStoreRequest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class TestVectorStores : TestOpenAI() {

    @Test
    fun test() = test {
        val request = VectorStoreRequest(name = "Support FAQ")
        val vectorStore = openAI.createVectorStore(request = request)
        assertEquals("Support FAQ", vectorStore.name)

        val vectorStores = openAI.vectorStores()
        assertContains(vectorStores, vectorStore)

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
}
