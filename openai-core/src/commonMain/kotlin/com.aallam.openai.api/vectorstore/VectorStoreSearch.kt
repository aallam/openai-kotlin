package com.aallam.openai.api.vectorstore

import com.aallam.openai.api.file.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

/** Searches indexed files using one or more natural-language queries. */
@Serializable
public data class VectorStoreSearchRequest(
    /** Query strings. */
    @SerialName("query") public val query: List<String>,
    /** Attribute comparison or compound filter. */
    @SerialName("filters") public val filters: VectorStoreFilter? = null,
    /** Maximum number of results, from 1 to 50. */
    @SerialName("max_num_results") public val maxNumResults: Int? = null,
    /** Result ranking options. */
    @SerialName("ranking_options") public val rankingOptions: VectorStoreRankingOptions? = null,
    /** Whether the server should rewrite the search query. */
    @SerialName("rewrite_query") public val rewriteQuery: Boolean? = null,
)

/** Attribute filter. Comparisons use key/value; `and` and `or` use nested filters. */
@Serializable
public data class VectorStoreFilter(
    /** Comparison operator or logical `and`/`or`. */
    @SerialName("type") public val type: String,
    /** Attribute name for comparisons. */
    @SerialName("key") public val key: String? = null,
    /** Scalar comparison value, or an array for `in`/`nin`. */
    @SerialName("value") public val value: JsonElement? = null,
    /** Child filters for logical operators. */
    @SerialName("filters") public val filters: List<VectorStoreFilter>? = null,
)

/** Re-ranking configuration for vector search. */
@Serializable
public data class VectorStoreRankingOptions(
    /** Ranker identifier, including `auto` and `none`. */
    @SerialName("ranker") public val ranker: String? = null,
    /** Minimum relevance score. */
    @SerialName("score_threshold") public val scoreThreshold: Double? = null,
)

/** Text extracted from an indexed file. */
@Serializable
public data class VectorStoreContent(
    /** Content kind. */
    @SerialName("type") public val type: String? = null,
    /** Extracted text. */
    @SerialName("text") public val text: String? = null,
)

/** A relevant file and the matched chunks. */
@Serializable
public data class VectorStoreSearchResult(
    /** Matched file identifier. */
    @SerialName("file_id") public val fileId: FileId,
    /** Original file name. */
    @SerialName("filename") public val filename: String,
    /** Relevance score. */
    @SerialName("score") public val score: Double,
    /** Matched content chunks. */
    @SerialName("content") public val content: List<VectorStoreContent>,
    /** Indexed attributes. */
    @SerialName("attributes") public val attributes: Map<String, JsonPrimitive>? = null,
)

/** Search results and the queries used by the server. */
@Serializable
public data class VectorStoreSearchPage(
    /** Relevant file results. */
    @SerialName("data") public val data: List<VectorStoreSearchResult>,
    /** Whether additional results exist. */
    @SerialName("has_more") public val hasMore: Boolean,
    /** Next page token, when provided. */
    @SerialName("next_page") public val nextPage: String? = null,
    /** Query strings after any server rewriting. */
    @SerialName("search_query") public val searchQuery: List<String> = emptyList(),
)

/** A page of parsed file content. */
@Serializable
public data class VectorStoreContentPage(
    /** Parsed content chunks. */
    @SerialName("data") public val data: List<VectorStoreContent>,
    /** Whether additional content exists. */
    @SerialName("has_more") public val hasMore: Boolean,
    /** Next page token, when provided. */
    @SerialName("next_page") public val nextPage: String? = null,
)
