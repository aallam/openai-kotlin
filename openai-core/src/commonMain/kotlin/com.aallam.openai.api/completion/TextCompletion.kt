package com.aallam.openai.api.completion

import com.aallam.openai.api.core.Usage
import com.aallam.openai.api.model.ModelId
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
    @SerialName("model") public val model: ModelId,

    /**
     * A list of generated completions
     */
    @SerialName("choices") public val choices: List<Choice>,

    /**
     * Text completion usage data.
     */
    @SerialName("usage") public val usage: Usage,
)
