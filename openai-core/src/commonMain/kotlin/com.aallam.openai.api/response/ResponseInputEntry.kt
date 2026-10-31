package com.aallam.openai.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

/** A structured message, tool result, approval or encrypted context item. */
@Serializable
public data class ResponseInputEntry(
    /** Item discriminator. */
    @SerialName("type") public val type: String,
    /** Item identifier. */
    @SerialName("id") public val id: String? = null,
    /** Message author. */
    @SerialName("role") public val role: String? = null,
    /** Text or structured content. */
    @SerialName("content") public val content: JsonElement? = null,
    /** Call whose result is supplied. */
    @SerialName("call_id") public val callId: String? = null,
    /** Tool result. */
    @SerialName("output") public val output: JsonElement? = null,
    /** MCP approval request identifier. */
    @SerialName("approval_request_id") public val approvalRequestId: String? = null,
    /** Whether the MCP call is approved. */
    @SerialName("approve") public val approve: Boolean? = null,
    /** Encrypted compaction or reasoning context. */
    @SerialName("encrypted_content") public val encryptedContent: String? = null,
    /** Tool name. */
    @SerialName("name") public val name: String? = null,
    /** Tool arguments. */
    @SerialName("arguments") public val arguments: String? = null,
    /** Item processing status. */
    @SerialName("status") public val status: String? = null,
    /** MCP server label. */
    @SerialName("server_label") public val serverLabel: String? = null,
    /** Tool error details. */
    @SerialName("error") public val error: JsonElement? = null,
    /** Hosted-tool action. */
    @SerialName("action") public val action: JsonElement? = null,
    /** Hosted image result. */
    @SerialName("result") public val result: String? = null,
    /** Reasoning summary. */
    @SerialName("summary") public val summary: JsonElement? = null,
) {
    public companion object {
        /** Creates a message with typed input content. */
        public fun message(role: String, content: List<ResponseInputContent>): ResponseInputEntry =
            ResponseInputEntry("message", role = role, content = Json.encodeToJsonElement(content))
    }
}

/** Text, image or file content supplied to a response. */
@Serializable
public data class ResponseInputContent(
    /** Content discriminator. */
    @SerialName("type") public val type: String,
    /** Input text. */
    @SerialName("text") public val text: String? = null,
    /** Image URL or data URL. */
    @SerialName("image_url") public val imageUrl: String? = null,
    /** Uploaded file identifier. */
    @SerialName("file_id") public val fileId: String? = null,
    /** Inline file data. */
    @SerialName("file_data") public val fileData: String? = null,
    /** Remote file URL. */
    @SerialName("file_url") public val fileUrl: String? = null,
    /** Inline file name. */
    @SerialName("filename") public val filename: String? = null,
    /** Image/file rendering detail. */
    @SerialName("detail") public val detail: String? = null,
    /** Explicit prefix cache boundary. */
    @SerialName("prompt_cache_breakpoint") public val promptCacheBreakpoint: ResponseCacheBreakpoint? = null,
)

/** Reusable prefix boundary. */
@Serializable
public data class ResponseCacheBreakpoint(
    /** Boundary mode, currently explicit. */
    @SerialName("mode") public val mode: String = "explicit",
    /** Minimum lifetime, otherwise inherited from the request. */
    @SerialName("ttl") public val ttl: String? = null,
)

/** Creates response input from typed messages, tool results or encrypted context. */
public fun responseInput(entries: List<ResponseInputEntry>): ResponseInput = ResponseInput(Json.encodeToJsonElement(entries))
