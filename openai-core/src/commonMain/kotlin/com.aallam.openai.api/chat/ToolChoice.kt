package com.aallam.openai.api.chat

import com.aallam.openai.api.chat.internal.ToolChoiceSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Controls which (if any) function is called by the model.
 */
@Serializable(with = ToolChoiceSerializer::class)
public sealed interface ToolChoice {

    /**
     * Represents a function call mode.
     * - `"none"` means the model will not call a function and instead generates a message.
     * - `"auto"` means the model can pick between generating a message or calling a function.
     */
    @JvmInline
    @Serializable
    public value class Mode(public val value: String) : ToolChoice

    /**
     * Specifies a tool the model should use.
     */
    @Serializable
    public data class Named(
        @SerialName("type") public val type: ToolType? = null,
        @SerialName("function") public val function: FunctionToolChoice? = null,
    ) : ToolChoice

    public companion object {
        /** Represents the `auto` mode. */
        public val Auto: ToolChoice = Mode("auto")

        /** Represents the `none` mode. */
        public val None: ToolChoice = Mode("none")

        /** Specifies a function for the model to call **/
        public fun function(name: String): ToolChoice =
            Named(type = ToolType.Function, function = FunctionToolChoice(name = name))
    }
}

/**
 * Represents the function to call by the model.
 */
@Serializable
public data class FunctionToolChoice(val name: String)
