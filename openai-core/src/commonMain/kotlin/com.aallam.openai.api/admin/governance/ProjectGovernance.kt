package com.aallam.openai.api.admin.governance

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/** Organization retention policies supported by the API. */
@Serializable
public enum class OrganizationRetentionType {
    /** Zero data retention. */
    @SerialName("zero_data_retention") ZeroDataRetention,
    /** Modified abuse monitoring. */
    @SerialName("modified_abuse_monitoring") ModifiedAbuseMonitoring,
    /** Enhanced zero data retention. */
    @SerialName("enhanced_zero_data_retention") EnhancedZeroDataRetention,
    /** Enhanced modified abuse monitoring. */
    @SerialName("enhanced_modified_abuse_monitoring") EnhancedModifiedAbuseMonitoring,
}

/** Project retention policies, including inheritance and no retention override. */
@Serializable
public enum class ProjectRetentionType {
    /** Inherit the organization's policy. */
    @SerialName("organization_default") OrganizationDefault,
    /** No project retention policy. */
    @SerialName("none") None,
    /** Zero data retention. */
    @SerialName("zero_data_retention") ZeroDataRetention,
    /** Modified abuse monitoring. */
    @SerialName("modified_abuse_monitoring") ModifiedAbuseMonitoring,
    /** Enhanced zero data retention. */
    @SerialName("enhanced_zero_data_retention") EnhancedZeroDataRetention,
    /** Enhanced modified abuse monitoring. */
    @SerialName("enhanced_modified_abuse_monitoring") EnhancedModifiedAbuseMonitoring,
}

/** Updates organization retention controls. */
@Serializable
public data class OrganizationRetentionRequest(
    /** Desired organization retention policy. */
    @SerialName("retention_type") public val retentionType: OrganizationRetentionType,
)

/** Updates project retention controls. */
@Serializable
public data class ProjectRetentionRequest(
    /** Desired project retention policy. */
    @SerialName("retention_type") public val retentionType: ProjectRetentionType,
)

/** Current organization or project retention setting. */
@Serializable
public data class DataRetentionSetting(
    /** Organization/project retention response discriminator. */
    @SerialName("object") public val objectType: String,
    /** Configured retention policy. */
    public val type: String,
)

/** Whether a model policy allows or denies the listed models. */
@Serializable
public enum class ModelPermissionMode {
    /** Only listed models are allowed. */
    @SerialName("allow_list") AllowList,
    /** Listed models are denied. */
    @SerialName("deny_list") DenyList,
}

/** Replaces a project's explicit model permission policy. */
@Serializable
public data class ProjectModelPermissionsRequest(
    /** Allowlist or denylist mode. */
    public val mode: ModelPermissionMode,
    /** Model identifiers covered by this policy. */
    @SerialName("model_ids") public val modelIds: List<String>,
)

/** A project's explicit model permission policy. */
@Serializable
public data class ProjectModelPermissions(
    /** Allowlist or denylist mode. */
    public val mode: ModelPermissionMode,
    /** Model identifiers covered by this policy. */
    @SerialName("model_ids") public val modelIds: List<String>,
    /** Response discriminator. */
    @SerialName("object") public val objectType: String = "project.model_permissions",
)

/** Model-policy deletion confirmation. */
@Serializable
public data class ProjectModelPermissionsDeletion(
    /** Whether the explicit policy was deleted. */
    public val deleted: Boolean,
    /** Response discriminator. */
    @SerialName("object") public val objectType: String = "project.model_permissions.deleted",
)

/** Enablement state of one hosted tool. */
@Serializable
public data class HostedToolPermission(
    /** Whether this tool is enabled for the project. */
    public val enabled: Boolean,
)

/** Hosted tools with independently configurable project permissions. */
public enum class HostedTool {
    /** Code interpreter permission. */
    CodeInterpreter,
    /** File-search permission. */
    FileSearch,
    /** Image-generation permission. */
    ImageGeneration,
    /** MCP permission. */
    Mcp,
    /** Web-search permission. */
    WebSearch,
}

/** Current hosted-tool permissions for a project. */
@Serializable
public data class ProjectHostedToolPermissions(
    /** Code interpreter permission. */
    @SerialName("code_interpreter") public val codeInterpreter: HostedToolPermission,
    /** File search permission. */
    @SerialName("file_search") public val fileSearch: HostedToolPermission,
    /** Image generation permission. */
    @SerialName("image_generation") public val imageGeneration: HostedToolPermission,
    /** MCP permission. */
    public val mcp: HostedToolPermission,
    /** Web search permission. */
    @SerialName("web_search") public val webSearch: HostedToolPermission,
)

/** Updates supplied tool states; [nullFields] explicitly sends JSON null for selected tools. */
@Serializable(with = HostedToolPermissionsUpdateSerializer::class)
public data class ProjectHostedToolPermissionsRequest(
    /** Code interpreter update; null normally omits this field. */
    public val codeInterpreter: HostedToolPermission? = null,
    /** File search update; null normally omits this field. */
    public val fileSearch: HostedToolPermission? = null,
    /** Image generation update; null normally omits this field. */
    public val imageGeneration: HostedToolPermission? = null,
    /** MCP update; null normally omits this field. */
    public val mcp: HostedToolPermission? = null,
    /** Web search update; null normally omits this field. */
    public val webSearch: HostedToolPermission? = null,
    /** Fields to send as JSON null rather than omit. Null semantics remain defined by the API. */
    public val nullFields: Set<HostedTool> = emptySet(),
) {
    init { require(nullFields.none { value(it) != null }) { "Cannot supply a permission value and JSON null together" } }

    internal fun value(tool: HostedTool): HostedToolPermission? = when (tool) {
        HostedTool.CodeInterpreter -> codeInterpreter
        HostedTool.FileSearch -> fileSearch
        HostedTool.ImageGeneration -> imageGeneration
        HostedTool.Mcp -> mcp
        HostedTool.WebSearch -> webSearch
    }
}

internal object HostedToolPermissionsUpdateSerializer : KSerializer<ProjectHostedToolPermissionsRequest> {
    override val descriptor: SerialDescriptor = JsonObject.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ProjectHostedToolPermissionsRequest) {
        require(encoder is JsonEncoder)
        encoder.encodeJsonElement(buildJsonObject {
            for (tool in HostedTool.values()) {
                if (tool in value.nullFields) put(tool.field(), JsonNull)
                else value.value(tool)?.let { put(tool.field(), encoder.json.encodeToJsonElement(it)) }
            }
        })
    }

    override fun deserialize(decoder: Decoder): ProjectHostedToolPermissionsRequest {
        require(decoder is JsonDecoder)
        val obj = decoder.decodeJsonElement().jsonObject
        fun value(tool: HostedTool): HostedToolPermission? = obj[tool.field()]?.takeUnless { it is JsonNull }?.let {
            decoder.json.decodeFromJsonElement<HostedToolPermission>(it)
        }
        return ProjectHostedToolPermissionsRequest(
            value(HostedTool.CodeInterpreter), value(HostedTool.FileSearch), value(HostedTool.ImageGeneration),
            value(HostedTool.Mcp), value(HostedTool.WebSearch),
            HostedTool.values().filter { obj[it.field()] is JsonNull }.toSet(),
        )
    }
}

private fun HostedTool.field(): String = when (this) {
    HostedTool.CodeInterpreter -> "code_interpreter"
    HostedTool.FileSearch -> "file_search"
    HostedTool.ImageGeneration -> "image_generation"
    HostedTool.Mcp -> "mcp"
    HostedTool.WebSearch -> "web_search"
}

/** Per-model rate limit returned for a project. */
@Serializable
public data class ProjectRateLimit(
    /** Rate-limit identifier. */
    public val id: String,
    /** Maximum requests per minute. */
    @SerialName("max_requests_per_1_minute") public val maxRequestsPerMinute: Long,
    /** Maximum tokens per minute. */
    @SerialName("max_tokens_per_1_minute") public val maxTokensPerMinute: Long,
    /** Model this configuration applies to. */
    public val model: String,
    /** Response discriminator. */
    @SerialName("object") public val objectType: String = "project.rate_limit",
    /** Maximum batch input tokens per day, when relevant. */
    @SerialName("batch_1_day_max_input_tokens") public val batchInputTokensPerDay: Long? = null,
    /** Maximum audio megabytes per minute, when relevant. */
    @SerialName("max_audio_megabytes_per_1_minute") public val maxAudioMegabytesPerMinute: Long? = null,
    /** Maximum images per minute, when relevant. */
    @SerialName("max_images_per_1_minute") public val maxImagesPerMinute: Long? = null,
    /** Maximum requests per day, when relevant. */
    @SerialName("max_requests_per_1_day") public val maxRequestsPerDay: Long? = null,
)

/** Updates only supplied project rate-limit values. */
@Serializable
public data class ProjectRateLimitRequest(
    /** Maximum requests per minute. */
    @SerialName("max_requests_per_1_minute") public val maxRequestsPerMinute: Long? = null,
    /** Maximum tokens per minute. */
    @SerialName("max_tokens_per_1_minute") public val maxTokensPerMinute: Long? = null,
    /** Maximum batch input tokens per day. */
    @SerialName("batch_1_day_max_input_tokens") public val batchInputTokensPerDay: Long? = null,
    /** Maximum audio megabytes per minute. */
    @SerialName("max_audio_megabytes_per_1_minute") public val maxAudioMegabytesPerMinute: Long? = null,
    /** Maximum images per minute. */
    @SerialName("max_images_per_1_minute") public val maxImagesPerMinute: Long? = null,
    /** Maximum requests per day. */
    @SerialName("max_requests_per_1_day") public val maxRequestsPerDay: Long? = null,
)
