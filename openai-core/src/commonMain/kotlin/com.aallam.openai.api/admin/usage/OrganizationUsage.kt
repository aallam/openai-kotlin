@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.aallam.openai.api.admin.usage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

/** A cursor page of organization usage buckets. */
@Serializable
public data class OrganizationUsagePage(
    /** Time buckets in the requested range. */
    public val data: List<OrganizationUsageBucket>,
    /** Whether another page can be requested. */
    @SerialName("has_more") public val hasMore: Boolean,
    /** Opaque cursor for the next query, or null. */
    @SerialName("next_page") public val nextPage: String? = null,
    /** Resource object discriminator. */
    @SerialName("object") public val objectType: String = "page",
)

/** Aggregated records for an inclusive start and exclusive end, in Unix seconds. */
@Serializable
public data class OrganizationUsageBucket(
    /** Inclusive bucket start in Unix seconds. */
    @SerialName("start_time") public val startTime: Long,
    /** Exclusive bucket end in Unix seconds. */
    @SerialName("end_time") public val endTime: Long,
    /** Usage or cost records for the selected grouping. */
    public val results: List<OrganizationUsageResult>,
    /** Resource object discriminator. */
    @SerialName("object") public val objectType: String = "bucket",
)

/** A typed usage or cost record. Fields absent from a grouping remain null. */
@Serializable
@JsonClassDiscriminator("object")
public sealed interface OrganizationUsageResult {
    /** Aggregated Completions usage in a time bucket. */
    @Serializable @SerialName("organization.usage.completions.result")
    public data class Completions(
        /** Input tokens. */
        @SerialName("input_tokens") public val inputTokens: Long,
        /** Num model requests. */
        @SerialName("num_model_requests") public val numModelRequests: Long,
        /** Output tokens. */
        @SerialName("output_tokens") public val outputTokens: Long,
        /** Api key id. */
        @SerialName("api_key_id") public val apiKeyId: String? = null,
        /** Api source. */
        @SerialName("api_source") public val apiSource: String? = null,
        /** Batch. */
        @SerialName("batch") public val batch: Boolean? = null,
        /** Input audio tokens. */
        @SerialName("input_audio_tokens") public val inputAudioTokens: Long? = null,
        /** Input cache write 12h tokens. */
        @SerialName("input_cache_write_12h_tokens") public val inputCacheWrite12hTokens: Long? = null,
        /** Input cache write 30m tokens. */
        @SerialName("input_cache_write_30m_tokens") public val inputCacheWrite30mTokens: Long? = null,
        /** Input cache write tokens. */
        @SerialName("input_cache_write_tokens") public val inputCacheWriteTokens: Long? = null,
        /** Input cached audio tokens. */
        @SerialName("input_cached_audio_tokens") public val inputCachedAudioTokens: Long? = null,
        /** Input cached image tokens. */
        @SerialName("input_cached_image_tokens") public val inputCachedImageTokens: Long? = null,
        /** Input cached text tokens. */
        @SerialName("input_cached_text_tokens") public val inputCachedTextTokens: Long? = null,
        /** Input cached tokens. */
        @SerialName("input_cached_tokens") public val inputCachedTokens: Long? = null,
        /** Input image tokens. */
        @SerialName("input_image_tokens") public val inputImageTokens: Long? = null,
        /** Input text tokens. */
        @SerialName("input_text_tokens") public val inputTextTokens: Long? = null,
        /** Input uncached tokens. */
        @SerialName("input_uncached_tokens") public val inputUncachedTokens: Long? = null,
        /** Model. */
        @SerialName("model") public val model: String? = null,
        /** Output audio tokens. */
        @SerialName("output_audio_tokens") public val outputAudioTokens: Long? = null,
        /** Output image tokens. */
        @SerialName("output_image_tokens") public val outputImageTokens: Long? = null,
        /** Output text tokens. */
        @SerialName("output_text_tokens") public val outputTextTokens: Long? = null,
        /** Project id. */
        @SerialName("project_id") public val projectId: String? = null,
        /** Service tier. */
        @SerialName("service_tier") public val serviceTier: String? = null,
        /** User id. */
        @SerialName("user_id") public val userId: String? = null,
    ) : OrganizationUsageResult

    /** Aggregated Decisions usage in a time bucket. */
    @Serializable @SerialName("organization.usage.decisions.result")
    public data class Decisions(
        /** Input tokens. */
        @SerialName("input_tokens") public val inputTokens: Long,
        /** Num model requests. */
        @SerialName("num_model_requests") public val numModelRequests: Long,
        /** Output tokens. */
        @SerialName("output_tokens") public val outputTokens: Long,
        /** Api key id. */
        @SerialName("api_key_id") public val apiKeyId: String? = null,
        /** Api source. */
        @SerialName("api_source") public val apiSource: String? = null,
        /** Batch. */
        @SerialName("batch") public val batch: Boolean? = null,
        /** Input audio tokens. */
        @SerialName("input_audio_tokens") public val inputAudioTokens: Long? = null,
        /** Input cache write 12h tokens. */
        @SerialName("input_cache_write_12h_tokens") public val inputCacheWrite12hTokens: Long? = null,
        /** Input cache write 30m tokens. */
        @SerialName("input_cache_write_30m_tokens") public val inputCacheWrite30mTokens: Long? = null,
        /** Input cache write tokens. */
        @SerialName("input_cache_write_tokens") public val inputCacheWriteTokens: Long? = null,
        /** Input cached audio tokens. */
        @SerialName("input_cached_audio_tokens") public val inputCachedAudioTokens: Long? = null,
        /** Input cached image tokens. */
        @SerialName("input_cached_image_tokens") public val inputCachedImageTokens: Long? = null,
        /** Input cached text tokens. */
        @SerialName("input_cached_text_tokens") public val inputCachedTextTokens: Long? = null,
        /** Input cached tokens. */
        @SerialName("input_cached_tokens") public val inputCachedTokens: Long? = null,
        /** Input image tokens. */
        @SerialName("input_image_tokens") public val inputImageTokens: Long? = null,
        /** Input text tokens. */
        @SerialName("input_text_tokens") public val inputTextTokens: Long? = null,
        /** Input uncached tokens. */
        @SerialName("input_uncached_tokens") public val inputUncachedTokens: Long? = null,
        /** Model. */
        @SerialName("model") public val model: String? = null,
        /** Output audio tokens. */
        @SerialName("output_audio_tokens") public val outputAudioTokens: Long? = null,
        /** Output image tokens. */
        @SerialName("output_image_tokens") public val outputImageTokens: Long? = null,
        /** Output text tokens. */
        @SerialName("output_text_tokens") public val outputTextTokens: Long? = null,
        /** Project id. */
        @SerialName("project_id") public val projectId: String? = null,
        /** Service tier. */
        @SerialName("service_tier") public val serviceTier: String? = null,
        /** User id. */
        @SerialName("user_id") public val userId: String? = null,
    ) : OrganizationUsageResult

    /** Aggregated Embeddings usage in a time bucket. */
    @Serializable @SerialName("organization.usage.embeddings.result")
    public data class Embeddings(
        /** Input tokens. */
        @SerialName("input_tokens") public val inputTokens: Long,
        /** Num model requests. */
        @SerialName("num_model_requests") public val numModelRequests: Long,
        /** Api key id. */
        @SerialName("api_key_id") public val apiKeyId: String? = null,
        /** Model. */
        @SerialName("model") public val model: String? = null,
        /** Project id. */
        @SerialName("project_id") public val projectId: String? = null,
        /** User id. */
        @SerialName("user_id") public val userId: String? = null,
    ) : OrganizationUsageResult

    /** Aggregated Moderations usage in a time bucket. */
    @Serializable @SerialName("organization.usage.moderations.result")
    public data class Moderations(
        /** Input tokens. */
        @SerialName("input_tokens") public val inputTokens: Long,
        /** Num model requests. */
        @SerialName("num_model_requests") public val numModelRequests: Long,
        /** Api key id. */
        @SerialName("api_key_id") public val apiKeyId: String? = null,
        /** Model. */
        @SerialName("model") public val model: String? = null,
        /** Project id. */
        @SerialName("project_id") public val projectId: String? = null,
        /** User id. */
        @SerialName("user_id") public val userId: String? = null,
    ) : OrganizationUsageResult

    /** Aggregated Images usage in a time bucket. */
    @Serializable @SerialName("organization.usage.images.result")
    public data class Images(
        /** Images. */
        @SerialName("images") public val images: Long,
        /** Num model requests. */
        @SerialName("num_model_requests") public val numModelRequests: Long,
        /** Api key id. */
        @SerialName("api_key_id") public val apiKeyId: String? = null,
        /** Model. */
        @SerialName("model") public val model: String? = null,
        /** Project id. */
        @SerialName("project_id") public val projectId: String? = null,
        /** Size. */
        @SerialName("size") public val size: String? = null,
        /** Source. */
        @SerialName("source") public val source: String? = null,
        /** User id. */
        @SerialName("user_id") public val userId: String? = null,
    ) : OrganizationUsageResult

    /** Aggregated AudioSpeeches usage in a time bucket. */
    @Serializable @SerialName("organization.usage.audio_speeches.result")
    public data class AudioSpeeches(
        /** Characters. */
        @SerialName("characters") public val characters: Long,
        /** Num model requests. */
        @SerialName("num_model_requests") public val numModelRequests: Long,
        /** Api key id. */
        @SerialName("api_key_id") public val apiKeyId: String? = null,
        /** Model. */
        @SerialName("model") public val model: String? = null,
        /** Project id. */
        @SerialName("project_id") public val projectId: String? = null,
        /** User id. */
        @SerialName("user_id") public val userId: String? = null,
    ) : OrganizationUsageResult

    /** Aggregated AudioTranscriptions usage in a time bucket. */
    @Serializable @SerialName("organization.usage.audio_transcriptions.result")
    public data class AudioTranscriptions(
        /** Num model requests. */
        @SerialName("num_model_requests") public val numModelRequests: Long,
        /** Seconds. */
        @SerialName("seconds") public val seconds: Long,
        /** Api key id. */
        @SerialName("api_key_id") public val apiKeyId: String? = null,
        /** Model. */
        @SerialName("model") public val model: String? = null,
        /** Project id. */
        @SerialName("project_id") public val projectId: String? = null,
        /** User id. */
        @SerialName("user_id") public val userId: String? = null,
    ) : OrganizationUsageResult

    /** Aggregated VectorStores usage in a time bucket. */
    @Serializable @SerialName("organization.usage.vector_stores.result")
    public data class VectorStores(
        /** Usage bytes. */
        @SerialName("usage_bytes") public val usageBytes: Long,
        /** Project id. */
        @SerialName("project_id") public val projectId: String? = null,
    ) : OrganizationUsageResult

    /** Aggregated CodeInterpreterSessions usage in a time bucket. */
    @Serializable @SerialName("organization.usage.code_interpreter_sessions.result")
    public data class CodeInterpreterSessions(
        /** Num sessions. */
        @SerialName("num_sessions") public val numSessions: Long,
        /** Project id. */
        @SerialName("project_id") public val projectId: String? = null,
    ) : OrganizationUsageResult

    /** Aggregated FileSearches usage in a time bucket. */
    @Serializable @SerialName("organization.usage.file_searches.result")
    public data class FileSearches(
        /** Num requests. */
        @SerialName("num_requests") public val numRequests: Long,
        /** Api key id. */
        @SerialName("api_key_id") public val apiKeyId: String? = null,
        /** Project id. */
        @SerialName("project_id") public val projectId: String? = null,
        /** User id. */
        @SerialName("user_id") public val userId: String? = null,
        /** Vector store id. */
        @SerialName("vector_store_id") public val vectorStoreId: String? = null,
    ) : OrganizationUsageResult

    /** Aggregated WebSearches usage in a time bucket. */
    @Serializable @SerialName("organization.usage.web_searches.result")
    public data class WebSearches(
        /** Num model requests. */
        @SerialName("num_model_requests") public val numModelRequests: Long,
        /** Num requests. */
        @SerialName("num_requests") public val numRequests: Long,
        /** Api key id. */
        @SerialName("api_key_id") public val apiKeyId: String? = null,
        /** Api source. */
        @SerialName("api_source") public val apiSource: String? = null,
        /** Context level. */
        @SerialName("context_level") public val contextLevel: String? = null,
        /** Model. */
        @SerialName("model") public val model: String? = null,
        /** Project id. */
        @SerialName("project_id") public val projectId: String? = null,
        /** User id. */
        @SerialName("user_id") public val userId: String? = null,
    ) : OrganizationUsageResult

    /** Aggregated Costs usage in a time bucket. */
    @Serializable @SerialName("organization.costs.result")
    public data class Costs(
        /** Amount. */
        @SerialName("amount") public val amount: OrganizationCostAmount? = null,
        /** Api key id. */
        @SerialName("api_key_id") public val apiKeyId: String? = null,
        /** Api source. */
        @SerialName("api_source") public val apiSource: String? = null,
        /** Line item. */
        @SerialName("line_item") public val lineItem: String? = null,
        /** Project id. */
        @SerialName("project_id") public val projectId: String? = null,
        /** Quantity. */
        @SerialName("quantity") public val quantity: Double? = null,
        /** Quantity unit. */
        @SerialName("quantity_unit") public val quantityUnit: String? = null,
        /** User id. */
        @SerialName("user_id") public val userId: String? = null,
    ) : OrganizationUsageResult
 }

/** Monetary cost in its reported currency. */
@Serializable
public data class OrganizationCostAmount(
    /** Lowercase ISO-4217 currency, such as usd. */
    public val currency: String? = null,
    /** Numeric amount in the reported currency. */
    public val value: Double? = null,
)
