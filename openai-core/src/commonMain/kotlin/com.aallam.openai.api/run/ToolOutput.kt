package com.aallam.openai.api.run

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a tool output.
 */
@Serializable
public data class ToolOutput(
    /**
     * The ID of the tool call in the required_action object within the run object the output is being submitted for.
     */
    @SerialName("tool_call_id") val toolCallId: String? = null,
    /**
     * The output of the tool call to be submitted to continue the run.
     */
    @SerialName("output") val output: String? = null,
)
