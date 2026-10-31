package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.embedding.EmbeddingCreateRequest
import com.aallam.openai.api.embedding.EmbeddingResult
import com.aallam.openai.api.embedding.EmbeddingResponse

/**
 * Get a vector representation of a given input that can be easily consumed by machine learning models and algorithms.
 */
public interface Embeddings {

    /**
     * Creates an embedding vector representing the input text.
     *
     * @param request embedding request.
     * @param requestOptions request options.
     */
    public suspend fun embeddings(request: EmbeddingRequest, requestOptions: RequestOptions? = null): EmbeddingResponse
    /** Create embeddings from text or token IDs and retain vector encoding metadata. */
    public suspend fun createEmbeddings(
        request: EmbeddingCreateRequest,
        requestOptions: RequestOptions? = null,
    ): EmbeddingResult = throw UnsupportedOperationException("Encoded embeddings are not supported by this implementation")
}
