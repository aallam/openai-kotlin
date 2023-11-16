package com.aallam.openai.api.assistant

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.internal.AssistantToolSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Tool enabled on the assistant. There can be a maximum of 128 tools per assistant.
 * Tools can be of types code_interpreter, retrieval, or function.
 */
@BetaOpenAI
@Serializable(with = AssistantToolSerializer::class)
public sealed interface AssistantTool {
    /**
     * The type of tool being defined: code_interpreter
     */
    @BetaOpenAI
    @Serializable
    public object CodeInterpreter : AssistantTool


    /**
     * The type of tool being defined: retrieval
     */
    @BetaOpenAI
    @Serializable
    public object RetrievalTool : AssistantTool


    /**
     * The type of tool being defined: function
     */
    @BetaOpenAI
    @Serializable
    public class FunctionTool(
        /**
         * The name of the function to be called.
         */
        @SerialName("function") public val function: Function,
    ) : AssistantTool

}

