package com.aallam.openai.client

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.classification.Classification
import com.aallam.openai.api.classification.ClassificationRequest

/**
 * Given a query and a set of labeled examples, the model will predict the most likely label for the query.
 * Useful as a drop-in replacement for any ML classification or text-to-label task.
 */
@Deprecated("Classification APIs are deprecated")
public interface Classifications {

    /**
     * The endpoint first searches over the labeled examples to select the ones most relevant for the particular query.
     * Then, the relevant examples are combined with the query to construct a prompt to produce the final label via
     * the completions endpoint.
     */
    @Deprecated("Classification APIs are deprecated")
    @ExperimentalOpenAI
    public suspend fun classifications(request: ClassificationRequest): Classification
}
