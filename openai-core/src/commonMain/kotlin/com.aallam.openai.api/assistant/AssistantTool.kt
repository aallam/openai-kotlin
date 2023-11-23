package com.aallam.openai.api.assistant

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Tool enabled on the assistant. There can be a maximum of 128 tools per assistant.
 * Tools can be of types code_interpreter, retrieval, or function.
 */
@BetaOpenAI
@Serializable
public sealed interface AssistantTool {
    /**
     * The type of tool being defined: code_interpreter
     */
    @BetaOpenAI
    @Serializable
    @SerialName("code_interpreter")
    public data object CodeInterpreter : AssistantTool


    /**
     * The type of tool being defined: retrieval
     */
    @BetaOpenAI
    @Serializable
    @SerialName("retrieval")
    public data object RetrievalTool : AssistantTool


    /**
     * The type of tool being defined: function
     */
    @BetaOpenAI
    @Serializable
    @SerialName("function")
    public class FunctionTool(
        /**
         * The name of the function to be called.
         */
        @SerialName("function") public val function: Function,
    ) : AssistantTool

}
