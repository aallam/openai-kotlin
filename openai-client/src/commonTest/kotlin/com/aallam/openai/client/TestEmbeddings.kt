package com.aallam.openai.client

import com.aallam.openai.api.embedding.Embedding
import com.aallam.openai.api.embedding.embeddingRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.extension.distance
import com.aallam.openai.client.extension.similarity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestEmbeddings : TestOpenAI() {

    @Test
    fun embeddings() = test {
        val response = openAI.embeddings(request = embeddingRequest {
            model = ModelId("text-embedding-ada-002")
            input = listOf("The food was delicious and the waiter...")
        })
        assertTrue { response.embeddings.isNotEmpty() }
        val embedding = response.embeddings.first()
        assertTrue { embedding.embedding.isNotEmpty() }
        assertEquals(0, embedding.index)
    }

    @Test
    fun similarityEqual() = test {
        val embedding1 = Embedding(embedding = listOf(1.0, 2.0, 3.0, 4.0), index = 0)
        val embedding2 = Embedding(embedding = listOf(1.0, 2.0, 3.0, 4.0), index = 0)
        val similarity = embedding1.similarity(embedding2)
        assertEquals(1.0, similarity)
    }

    @Test
    fun similarityNotEqual() = test {
        val embedding1 = Embedding(embedding = listOf(0.5, 0.3, 1.2, 0.33), index = 0)
        val embedding2 = Embedding(embedding = listOf(0.3, 0.2, 1.3, 1.4), index = 0)
        val similarity = embedding1.similarity(embedding2)
        assertEquals(0.8353, similarity, 0.0001)
    }

    @Test
    fun distanceEqual() = test {
        val embedding1 = Embedding(embedding = listOf(1.0, 2.0, 3.0, 4.0), index = 0)
        val embedding2 = Embedding(embedding = listOf(1.0, 2.0, 3.0, 4.0), index = 0)
        val distance = embedding1.distance(embedding2)
        assertEquals(0.0, distance)
    }

    @Test
    fun distanceNotEqual() = test {
        val embedding1 = Embedding(embedding = listOf(0.5, 0.3, 1.2, 0.33), index = 0)
        val embedding2 = Embedding(embedding = listOf(0.3, 0.2, 1.3, 1.4), index = 0)
        val distance = embedding1.distance(embedding2)
        assertEquals(0.1646, distance, 0.0001)
    }
}
