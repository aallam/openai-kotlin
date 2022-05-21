package com.aallam.openai.api.edits

import com.aallam.openai.api.engine.EngineId
import kotlinx.serialization.SerialName

public data class EditsRequest(

    /**
     * The ID of the engine to use for this request
     */
    @SerialName("engine_id")
    val engineId: EngineId,

    /**
     * The instruction that tells the model how to edit the prompt.
     */
    @SerialName("instruction")
    val instruction: String,

    /**
     * The input text to use as a starting point for the edit.
     *
     * Defaults to ''.
     */
    @SerialName("input")
    val input: String? = null,

    /**
     * What sampling temperature to use. Higher values means the model will take more risks.
     * Try 0.9 for more creative applications, and 0 (argmax sampling) for ones with a well-defined answer.
     *
     * We generally recommend altering this or `top_p` but not both.
     *
     * Defaults to 1.
     */
    @SerialName("temperature")
    val temperature: Double? = null,

    /**
     * An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of
     * the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are
     * considered.
     *
     * We generally recommend altering this or `temperature` but not both.
     *
     * Defaults to 1.
     */
    @SerialName("top_p")
    public val topP: Double? = null,
)
