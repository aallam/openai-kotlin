package com.aallam.openai.api.edits

import com.aallam.openai.api.completion.Choice
import kotlinx.serialization.SerialName

/**
 * Response to the edit creation request.
 */
public data class EditsResponse(
    /**
     * The creation time in epoch milliseconds.
     */
    @SerialName("created") val created: Long,

    /**
     * A list of generated completions.
     */
    @SerialName("choices") val choices: List<Choice>
)
