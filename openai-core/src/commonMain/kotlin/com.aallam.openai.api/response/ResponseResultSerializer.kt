package com.aallam.openai.api.response

import com.aallam.openai.api.exception.OpenAIErrorDetails
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

internal object ResponseResultSerializer : KSerializer<ResponseResult> {
    override val descriptor: SerialDescriptor = ResponseResultPayload.serializer().descriptor

    override fun deserialize(decoder: Decoder): ResponseResult {
        require(decoder is JsonDecoder)
        val raw = decoder.decodeJsonElement().jsonObject
        val payload = decoder.json.decodeFromJsonElement(ResponseResultPayload.serializer(), raw)
        return ResponseResult(
            id = payload.id,
            objectType = payload.objectType,
            createdAt = payload.createdAt,
            status = payload.status,
            error = payload.error,
            incompleteDetails = payload.incompleteDetails,
            instructions = payload.instructions,
            maxOutputTokens = payload.maxOutputTokens,
            model = payload.model,
            output = payload.output,
            parallelToolCalls = payload.parallelToolCalls,
            previousResponseId = payload.previousResponseId,
            reasoning = payload.reasoning,
            store = payload.store,
            temperature = payload.temperature,
            text = payload.text,
            toolChoice = payload.toolChoice,
            tools = payload.tools,
            topP = payload.topP,
            truncation = payload.truncation,
            usage = payload.usage,
            user = payload.user,
            metadata = payload.metadata,
            outputText = payload.outputText,
            background = payload.background,
            conversation = payload.conversation,
            serviceTier = payload.serviceTier,
            completedAt = payload.completedAt,
            maxToolCalls = payload.maxToolCalls,
            promptCacheKey = payload.promptCacheKey,
            promptCacheOptions = payload.promptCacheOptions,
            contextManagement = payload.contextManagement,
            accessPrograms = payload.accessPrograms,
            safetyIdentifier = payload.safetyIdentifier,
            topLogprobs = payload.topLogprobs,
            moderation = payload.moderation,
            rawData = raw,
        )
    }

    override fun serialize(encoder: Encoder, value: ResponseResult) {
        require(encoder is JsonEncoder)
        val payload = ResponseResultPayload(
            id = value.id,
            objectType = value.objectType,
            createdAt = value.createdAt,
            status = value.status,
            error = value.error,
            incompleteDetails = value.incompleteDetails,
            instructions = value.instructions,
            maxOutputTokens = value.maxOutputTokens,
            model = value.model,
            output = value.output,
            parallelToolCalls = value.parallelToolCalls,
            previousResponseId = value.previousResponseId,
            reasoning = value.reasoning,
            store = value.store,
            temperature = value.temperature,
            text = value.text,
            toolChoice = value.toolChoice,
            tools = value.tools,
            topP = value.topP,
            truncation = value.truncation,
            usage = value.usage,
            user = value.user,
            metadata = value.metadata,
            outputText = value.outputText,
            background = value.background,
            conversation = value.conversation,
            serviceTier = value.serviceTier,
            completedAt = value.completedAt,
            maxToolCalls = value.maxToolCalls,
            promptCacheKey = value.promptCacheKey,
            promptCacheOptions = value.promptCacheOptions,
            contextManagement = value.contextManagement,
            accessPrograms = value.accessPrograms,
            safetyIdentifier = value.safetyIdentifier,
            topLogprobs = value.topLogprobs,
            moderation = value.moderation,
        )
        val encoded = encoder.json.encodeToJsonElement(ResponseResultPayload.serializer(), payload).jsonObject
        encoder.encodeJsonElement(encoded)
    }
}

@Serializable
@SerialName("com.aallam.openai.api.response.ResponseResult")
private data class ResponseResultPayload(
    @SerialName("id") val id: ResponseId,
    @SerialName("object") val objectType: String? = null,
    @SerialName("created_at") val createdAt: Long? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("error") val error: OpenAIErrorDetails? = null,
    @SerialName("incomplete_details") val incompleteDetails: JsonObject? = null,
    @SerialName("instructions") val instructions: String? = null,
    @SerialName("max_output_tokens") val maxOutputTokens: Int? = null,
    @SerialName("model") val model: ModelId? = null,
    @SerialName("output") val output: List<ResponseResultItem> = emptyList(),
    @SerialName("parallel_tool_calls") val parallelToolCalls: Boolean? = null,
    @SerialName("previous_response_id") val previousResponseId: ResponseId? = null,
    @SerialName("reasoning") val reasoning: ResponseReasoningOptions? = null,
    @SerialName("store") val store: Boolean? = null,
    @SerialName("temperature") val temperature: Double? = null,
    @SerialName("text") val text: JsonObject? = null,
    @SerialName("tool_choice") val toolChoice: JsonElement? = null,
    @SerialName("tools") val tools: JsonElement? = null,
    @SerialName("top_p") val topP: Double? = null,
    @SerialName("truncation") val truncation: String? = null,
    @SerialName("usage") val usage: ResponseUsage? = null,
    @SerialName("user") val user: String? = null,
    @SerialName("metadata") val metadata: Map<String, String>? = null,
    @SerialName("output_text") val outputText: String? = null,
    @SerialName("background") val background: Boolean? = null,
    @SerialName("conversation") val conversation: JsonObject? = null,
    @SerialName("service_tier") val serviceTier: String? = null,
    @SerialName("completed_at") val completedAt: Long? = null,
    @SerialName("max_tool_calls") val maxToolCalls: Int? = null,
    @SerialName("prompt_cache_key") val promptCacheKey: String? = null,
    @SerialName("prompt_cache_options") val promptCacheOptions: ResponsePromptCacheOptions? = null,
    @SerialName("context_management") val contextManagement: List<ResponseContextManagement>? = null,
    @SerialName("access_programs") val accessPrograms: ResponseAccessPrograms? = null,
    @SerialName("safety_identifier") val safetyIdentifier: String? = null,
    @SerialName("top_logprobs") val topLogprobs: Int? = null,
    @SerialName("moderation") val moderation: JsonElement? = null,
)
