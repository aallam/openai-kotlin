package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.decision.Decision
import com.aallam.openai.api.decision.DecisionRequest

/** Evaluates evidence using predicate, choice and score questions. */
public interface Decisions {
    /**
     * Answers the questions in request order. Any answer may be a refusal.
     *
     * The default implementation preserves source compatibility for custom clients.
     * Clients created by the SDK implement this operation.
     * @throws UnsupportedOperationException when a custom client does not support Decisions.
     */
    public suspend fun decision(
        request: DecisionRequest,
        requestOptions: RequestOptions? = null,
    ): Decision = throw UnsupportedOperationException("Decisions are not supported by this client.")
}
