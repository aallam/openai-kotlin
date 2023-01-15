package com.aallam.openai.api.edits

import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A request for OpenAI to creates a new edit for the provided input, instruction, and parameters.
 *
 * [documentation](https://beta.openai.com/docs/api-reference/edits/create)
 */
@Serializable
public class EditsRequest(

    /**
     * ID of the model to use.
     */
    @SerialName("model") public val model: ModelId,

    /**
     * The instruction that tells the model how to edit the prompt.
     */
    @SerialName("instruction") public val instruction: String,

    /**
     * The input text to use as a starting point for the edit.
     *
     * Defaults to ''.
     */
    @SerialName("input") public val input: String? = null,

    /**
     * What sampling temperature to use. Higher values means the model will take more risks.
     * Try 0.9 for more creative applications, and 0 (argmax sampling) for ones with a well-defined answer.
     *
     * We generally recommend altering this or `top_p` but not both.
     *
     * Defaults to 1.
     */
    @SerialName("temperature") public val temperature: Double? = null,

    /**
     * An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of
     * the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are
     * considered.
     *
     * We generally recommend altering this or `temperature` but not both.
     *
     * Defaults to 1.
     */
    @SerialName("top_p") public val topP: Double? = null,
)

/**
 * A request for OpenAI to creates a new edit for the provided input, instruction, and parameters.
 *
 * [documentation](https://beta.openai.com/docs/api-reference/edits/create)
 */
public fun editsRequest(block: EditsRequestDSL.() -> Unit): EditsRequest = EditsRequestDSL().apply(block).build()

/**
 * DSL to build a [EditsRequest] instance.
 */
public class EditsRequestDSL {

    /**
     * ID of the model to use.
     */
    public var model: ModelId? = null

    /**
     * The instruction that tells the model how to edit the prompt.
     */
    public var instruction: String? = null

    /**
     * The input text to use as a starting point for the edit.
     *
     * Defaults to ''.
     */
    public var input: String? = null

    /**
     * What sampling temperature to use. Higher values means the model will take more risks.
     * Try 0.9 for more creative applications, and 0 (argmax sampling) for ones with a well-defined answer.
     *
     * We generally recommend altering this or `top_p` but not both.
     *
     * Defaults to 1.
     */
    public var temperature: Double? = null

    /**
     * An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of
     * the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are
     * considered.
     *
     * We generally recommend altering this or `temperature` but not both.
     *
     * Defaults to 1.
     */
    public var topP: Double? = null

    /**
     * Create [EditsRequest] instance.
     */
    public fun build(): EditsRequest = EditsRequest(
        model = requireNotNull(model) { "model must not be null" },
        instruction = requireNotNull(instruction) { "instruction must not be null" },
        input = input,
        temperature = temperature,
        topP = topP
    )
}
