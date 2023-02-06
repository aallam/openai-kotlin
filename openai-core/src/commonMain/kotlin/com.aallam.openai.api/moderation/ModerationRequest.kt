package com.aallam.openai.api.moderation

import com.aallam.openai.api.OpenAIDsl
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
)

/**
 * Request to classify if text violates OpenAI's Content Policy.
 */
public fun moderationRequest(block: ModerationRequestBuilder.() -> Unit): ModerationRequest =
    ModerationRequestBuilder().apply(block).build()

/**
 * Data class representing a ModerationRequest
 */
@OpenAIDsl
public class ModerationRequestBuilder {
    /**
     * The input text to classify.
     */
    public var input: List<String>? = null

    /**
     * Moderation model.
     * Defaults to [ModerationModel.Latest].
     */
    public var model: ModerationModel? = null

    /**
     * Creates the [ModerationRequest] instance
     */
    public fun build(): ModerationRequest = ModerationRequest(
        input = requireNotNull(input) { "input is required" },
        model = model,
    )
}
