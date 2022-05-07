package com.aallam.openai.client

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.engine.EngineId
import kotlinx.coroutines.flow.Flow

/**
 * Given a prompt, the model will return one or more predicted completions, and can also return the probabilities
 * of alternative tokens at each position.
 */
public interface Completions {

    /**
     * This is the main endpoint of the API. Returns the predicted completion for the given prompt,
     * and can also return the probabilities of alternative tokens at each position if requested.
     */
    public suspend fun completion(engineId: EngineId, request: CompletionRequest? = null): TextCompletion

    /**
     * This is the main endpoint of the API. Returns the predicted completion for the given prompt,
     * and can also return the probabilities of alternative tokens at each position if requested.
     *
     * [request]'s `model` must be set.
     */
    @ExperimentalOpenAI
    public suspend fun completion(request: CompletionRequest): TextCompletion

    /**
     * Stream variant of [completion].
     */
    public fun completions(engineId: EngineId, request: CompletionRequest? = null): Flow<TextCompletion>
}
