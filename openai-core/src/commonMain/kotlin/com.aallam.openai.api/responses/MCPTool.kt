package com.aallam.openai.api.responses

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.jvm.JvmInline

@Serializable(with = MCPAllowedToolsSerializer::class)
public sealed interface MCPAllowedTools

@Serializable
@JvmInline
public value class MCPAllowedToolsList(
    public val value: List<String>
) : MCPAllowedTools

internal class MCPAllowedToolsSerializer : JsonContentPolymorphicSerializer<MCPAllowedTools>(MCPAllowedTools::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<MCPAllowedTools> {
        return when (element) {
            is JsonPrimitive -> MCPAllowedToolsList.serializer()
            is JsonObject -> MCPToolFilter.serializer()
            else -> throw SerializationException("Unsupported JSON element: $element")
        }
    }
}


@Serializable(with = MCPRequireApprovalSerializer::class)
public sealed interface MCPRequireApproval

internal class MCPRequireApprovalSerializer :
    JsonContentPolymorphicSerializer<MCPRequireApproval>(MCPRequireApproval::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<MCPRequireApproval> {
        return when (element) {
            is JsonPrimitive -> MCPToolApprovalSetting.serializer()
            is JsonObject -> MCPToolApprovalFilter.serializer()
            else -> throw SerializationException("Unsupported JSON element: $element")
        }
    }
}

/**
 * Specify a single approval policy for all tools.
 * One of always or never.
 * When set to always, all tools will require approval.
 * When set to never, all tools will not require approval.
 *
 * @see [ResponseTool.MCP.requireApproval]
 */
@Serializable
@JvmInline
public value class MCPToolApprovalSetting(
    public val value: String,
) : MCPRequireApproval {
    public companion object {
        public val Always: MCPToolApprovalSetting = MCPToolApprovalSetting("always")
        public val Never: MCPToolApprovalSetting = MCPToolApprovalSetting("never")
    }
}

@Serializable
public data class MCPToolApprovalFilter(

    /**
     * A list of tools that always require approval.
     */
    @SerialName("always") val always: MCPToolFilter? = null,

    /**
     * A list of tools that never require approval.
     */
    @SerialName("never") val never: MCPToolFilter? = null

) : MCPRequireApproval


/**
 * A filter object to specify which tools are allowed.
 */
@Serializable
public data class MCPToolFilter(
    /**
     * List of allowed tool names.
     */
    @SerialName("tool_names") val toolNames: List<String>? = null

) : MCPAllowedTools

/**
 * A list of tools available on an MCP server.
 */
@Serializable
@SerialName("mcp_list_tools")
public data class MCPListTools(

    /**
     * The unique ID of the list.
     */
    @SerialName("id")
    override val id: String,

    /**
     * The label of the MCP server.
     */
    @SerialName("server_label")
    val serverLabel: String,

    /**
     * The list of tools available on the MCP server.
     */
    @SerialName("tools")
    val tools: List<MCPAvailableTool>,

    /**
     * Error message if the server could not list tools.
     */
    @SerialName("error")
    val error: String? = null
) : ResponseOutput

/**
 * A tool available on an MCP server.
 */
@Serializable
public data class MCPAvailableTool(
    /**
     * The name of the tool.
     */
    @SerialName("name") val name: String,

    /**
     * The description of the tool.
     */
    @SerialName("description") val description: String? = null,

    /**
     * The parameters required by the tool.
     */
    @SerialName("parameters") val parameters: JsonObject? = null
)

/**
 * An invocation of a tool on an MCP server.
 */
@Serializable
@SerialName("mcp_call")
public data class MCPToolCall(

    /**
     * A JSON string of the arguments passed to the tool.
     */
    @SerialName("arguments")
    val arguments: String,

    /**
     * The unique ID of the tool call.
     */
    @SerialName("id")
    override val id: String,

    /**
     * The name of the tool that was run.
     */
    @SerialName("name")
    val name: String,

    /**
     * The label of the MCP server.
     */
    @SerialName("server_label")
    val serverLabel: String,

    /**
     * Error message if the server could not list tools.
     */
    @SerialName("error")
    val error: String? = null,

    /**
     * The output from the tool call.
     */
    @SerialName("output")
    val output: String? = null
) : ResponseOutput

/**
 * A request for human approval of a tool invocation.
 */
@Serializable
@SerialName("mcp_approval_request")
public data class MCPToolApprovalRequest(

    /**
     * A JSON string of the arguments for the tool.
     */
    @SerialName("arguments")
    val arguments: String,

    /**
     * The unique ID of the approval request.
     */
    @SerialName("id")
    override val id: String,

    /**
     * The name of the tool to run.
     */
    @SerialName("name")
    val name: String,

    /**
     * The label of the MCP server.
     */
    @SerialName("server_label")
    val serverLabel: String
) : ResponseOutput

/**
 * A response to an MCP approval request.
 */
@Serializable
@SerialName("mcp_approval_response")
public data class MCPToolApprovalResponse(

    /**
     * The ID of the approval request being answered.
     */
    @SerialName("approval_request_id")
    val approvalRequestId: String,

    /**
     * Whether the request was approved.
     */
    @SerialName("approve")
    val approve: Boolean,

    /**
     * The unique ID of the approval response.
     */
    @SerialName("id")
    val id: String? = null,

    /**
     * Optional reason for the decision.
     */
    @SerialName("reason")
    val reason: String? = null
) : ResponseItem
