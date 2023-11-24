package com.aallam.openai.api.run

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ToolCall
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@BetaOpenAI
@Serializable
public data class RequiredAction(
    @SerialName("submit_tool_outputs")
    public val submitToolOutputs: SubmitToolOutputs? = null,
) {
    @Serializable
    public data class SubmitToolOutputs(
        /**
         * A list of the relevant tool calls.
         */
        @SerialName("tool_calls") public val toolCalls: List<ToolCall>,
    )
}
