package com.aallam.openai.api.completion

import com.aallam.openai.api.LegacyOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Log probabilities of different token options?
 * Returned if [CompletionRequest.logprobs] is greater than zero.
 *
 * [documentation](https://beta.openai.com/docs/api-reference/create-completion)
 */
@LegacyOpenAI
@Serializable
public data class Logprobs(
    /**
     * The tokens chosen by the completion api
     */
    @SerialName("tokens") public val tokens: List<String>,

    /**
     * The log probability of each token in [tokens]
     */
    @SerialName("token_logprobs") public val tokenLogprobs: List<Double>,

    /**
     * A map for each index in the completion result.
     * The map contains the top [CompletionRequest.logprobs] tokens and their probabilities
     */
    @SerialName("top_logprobs") public val topLogprobs: List<Map<String, Double>>,

    /**
     * The character offset from the start of the returned text for each of the chosen tokens.
     */
    @SerialName("text_offset") public val textOffset: List<Int>,
)
