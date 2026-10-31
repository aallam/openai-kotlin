package com.aallam.openai.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Automatic context management entry. */
@Serializable
public data class ResponseContextManagement(
    /** Context management strategy. */
    @SerialName("type") public val type: String? = null,
    /** Input token threshold for compaction. */
    @SerialName("compact_threshold") public val compactThreshold: Int? = null,
)

/** Moderation configuration. */
@Serializable
public data class ResponseModeration(
    /** Moderation model. */
    @SerialName("model") public val model: String? = null,
    /** Moderation policy. */
    @SerialName("policy") public val policy: ResponseModerationPolicy? = null,
)

/** Prompt cache configuration. */
@Serializable
public data class ResponsePromptCacheOptions(
    /** Implicit or explicit caching. */
    @SerialName("mode") public val mode: String? = null,
    /** Minimum cache lifetime. */
    @SerialName("ttl") public val ttl: String? = null,
    /** Prewarm the reusable prefix. */
    @SerialName("prewarm") public val prewarm: Boolean? = null,
    /** Response used for cache diagnostics. */
    @SerialName("comparison_response_id") public val comparisonResponseId: String? = null,
)

/** Streaming payload configuration. */
@Serializable
public data class ResponseStreamOptions(
    /** Include stream obfuscation data. */
    @SerialName("include_obfuscation") public val includeObfuscation: Boolean? = null,
)

/** Restricted access programs. */
@Serializable
public data class ResponseAccessPrograms(
    /** Cyber access program configuration. */
    @SerialName("cyber") public val cyber: String? = null,
)

/** File-search ranking configuration. */
@Serializable
public data class ResponseRankingOptions(
    /** Ranker version. */
    @SerialName("ranker") public val ranker: String? = null,
    /** Minimum result score. */
    @SerialName("score_threshold") public val scoreThreshold: Double? = null,
    /** Hybrid search weighting configuration. */
    @SerialName("hybrid_search") public val hybridSearch: ResponseHybridSearch? = null,
)

/** Per-direction moderation policies. */
@Serializable
public data class ResponseModerationPolicy(
    /** Policy applied to input content. */
    @SerialName("input") public val input: ResponseModerationMode? = null,
    /** Policy applied to generated output. */
    @SerialName("output") public val output: ResponseModerationMode? = null,
)

/** Whether moderation scores content or blocks it. */
@Serializable
public data class ResponseModerationMode(
    /** Either score or block. */
    @SerialName("mode") public val mode: String,
)

/** Mask selected by uploaded file ID or image URL. */
@Serializable
public data class ResponseImageMask(
    /** Uploaded mask file ID. */
    @SerialName("file_id") public val fileId: String? = null,
    /** Mask image URL. */
    @SerialName("image_url") public val imageUrl: String? = null,
)

/** Weights used to combine embedding and text search. */
@Serializable
public data class ResponseHybridSearch(
    /** Relative weight of embedding results. */
    @SerialName("embedding_weight") public val embeddingWeight: Double,
    /** Relative weight of text matches. */
    @SerialName("text_weight") public val textWeight: Double,
)
