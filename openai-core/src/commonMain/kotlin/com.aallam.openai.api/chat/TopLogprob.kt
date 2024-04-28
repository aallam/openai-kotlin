package com.aallam.openai.api.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An object containing a token and their log probability.
 *
 * [documentation](https://platform.openai.com/docs/api-reference/chat/object)
 */
@Serializable
public data class TopLogprob(
    /**
     * The token
     */
    @SerialName("token") public val token: String,
    /**
     * The log probability of this token, if it is within the top 20 most likely tokens.
     * Otherwise, the value `-9999.0` is used to signify that the token is very unlikely.
     */
    @SerialName("logprob") public val logprob: Double,
    /**
     * A list of integers representing the UTF-8 bytes representation of the token. Useful in instances where
     * characters are represented by multiple tokens and their byte representations must be combined to generate
     * the correct text representation. Can be `null` if there is no bytes representation for the token.
     */
    @SerialName("bytes") public val bytes: List<Int>? = null,
)
