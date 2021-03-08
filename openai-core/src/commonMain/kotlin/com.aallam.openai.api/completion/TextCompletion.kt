package com.aallam.openai.api.completion

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An object containing a response from the completion api.
 *
 * [documentation](https://beta.openai.com/docs/api-reference/create-completion)
 */
@Serializable
public data class TextCompletion(
    /**
     * A unique id assigned to this completion
     */
    @SerialName("id") public val id: String,

    /**
     * The creation time in epoch milliseconds.
     */
    @SerialName("created") public val created: Long,

    /**
     * The GPT-3 model used
     */
    @SerialName("model") public val model: String,

    /**
     * A list of generated completions
     */
    @SerialName("choices") public val choices: List<Choice>,
)
