package com.aallam.openai.api.moderation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request to classify if text violates OpenAI's Content Policy.
 */
@Serializable
public class ModerationRequest(
    /**
     * The input text to classify.
     */
    @SerialName("input") public val input: List<String>,

    /**
     * Moderation model.
     * Defaults to [ModerationModel.Latest].
     */
    @SerialName("model") public val model: ModerationModel? = null,
) {

    /**
     * Convenience constructor with [input] as [String].
     */
    public constructor(input: String, model: ModerationModel? = null) : this(listOf(input), model)
}
