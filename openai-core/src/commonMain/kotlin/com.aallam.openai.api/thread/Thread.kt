package com.aallam.openai.api.thread

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantTool
import com.aallam.openai.api.assistant.ToolResources
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a thread that contains messages.
 */
@BetaOpenAI
@Serializable
public data class Thread(
    /**
     * The identifier of the thread, which can be referenced in API endpoints.
     */
    @SerialName("id") val id: ThreadId,
    /**
     * The object type, which is always thread.
     */
    @SerialName("object") val objectType: String? = null,
    /**
     * The Unix timestamp (in seconds) for when the thread was created.
     */
    @SerialName("created_at") val createdAt: Int,
    /**
     * A set of resources that are made available to the assistant's tools in this thread.
     * The resources are specific to the type of tool.
     * For example, the code_interpreter tool requires a list of file IDs,
     * while the file_search tool requires a list of vector store IDs.
     */
    @SerialName("tool_resources") val toolResources: ToolResources? = null,
    /**
     * A map representing a set of key-value pairs that can be attached to the thread.
     * This can be useful for storing additional information about the thread in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    @SerialName("metadata") val metadata: Map<String, String>
)
