package com.aallam.openai.client

import com.aallam.openai.api.embedding.embeddingRequest
import com.aallam.openai.api.model.ModelId
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestEmbeddings : TestOpenAI() {

    @Test
    fun embeddings() = runTest {
        val response = openAI.embeddings(
            request = embeddingRequest {
                model = ModelId("text-similarity-babbage-001")
                input = listOf("The food was delicious and the waiter...")
            }
        )
        assertTrue { response.isNotEmpty() }
        val embedding = response.first()
        assertTrue { embedding.embedding.isNotEmpty() }
        assertEquals(embedding.index, 0)
    }
}
