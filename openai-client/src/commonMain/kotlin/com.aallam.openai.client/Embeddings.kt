package com.aallam.openai.client

import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.embedding.EmbeddingResponse

/**
 * Get a vector representation of a given input that can be easily consumed by machine learning models and algorithms.
 */
public interface Embeddings {

    /**
     * Creates an embedding vector representing the input text.
     */
    public suspend fun embeddings(request: EmbeddingRequest): EmbeddingResponse
}
