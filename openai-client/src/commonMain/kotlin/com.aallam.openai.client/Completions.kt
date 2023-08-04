package com.aallam.openai.client

import com.aallam.openai.api.LegacyOpenAI
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
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
    @LegacyOpenAI
    public suspend fun completion(request: CompletionRequest): TextCompletion

    /**
     * Stream variant of [completion].
     */
    @LegacyOpenAI
    public fun completions(request: CompletionRequest): Flow<TextCompletion>
}
