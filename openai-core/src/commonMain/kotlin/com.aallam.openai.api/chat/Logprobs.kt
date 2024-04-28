package com.aallam.openai.api.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An object containing log probability information for the choice.
 *
 * [documentation](https://platform.openai.com/docs/api-reference/chat/object)
 */
@Serializable
public data class Logprobs(
    /**
     * A list of message content tokens with log probability information.
     */
    @SerialName("content") public val content: List<LogprobsContent>? = null,
)
