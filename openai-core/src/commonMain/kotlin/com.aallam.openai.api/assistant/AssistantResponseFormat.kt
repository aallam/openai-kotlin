package com.aallam.openai.api.assistant

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * string: auto is the default value
 *
 * object: An object describing the expected output of the model. If json_object only function type tools are allowed to be passed to the Run.
 * If text, the model can return text or any value needed.
 * type: string Must be one of text or json_object.
 */
@Serializable
@BetaOpenAI
public sealed interface AssistantResponseFormat {

    /**
     * The type of response format being defined: text
     */
    @BetaOpenAI
    @Serializable
    @JvmInline
    public value class Text(public val value: String) : AssistantResponseFormat


    /**
     * An object describing the expected output of the model.
     */
    @BetaOpenAI
    @Serializable
    public data class Object(@SerialName("type") val value: String) : AssistantResponseFormat

    public companion object {
        /**
         * The type of response format being defined: auto
         */
        public val Auto: AssistantResponseFormat = Text("auto")

        /**
         * The type of response format being defined: text
         */
        public val Text: AssistantResponseFormat = Object("text")

        /**
         * The type of response format being defined: json_object
         */
        public val JsonObject: AssistantResponseFormat = Object("json_object")
    }
}