package com.aallam.openai.api.responses

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = FileSearchFilterSerializer::class)
public sealed interface FileSearchFilter

/**
 * A filter used to compare a specified attribute key to a given value using a defined comparison operation.
 */
@Serializable
public data class ComparisonFilter(

    /**
     * Specifies the comparison operator: eq, ne, gt, gte, lt, lte.
     */
    @SerialName("type")
    public val type: String,

    /**
     * The key to compare against the value.
     */
    @SerialName("key")
    public val key: String,

    /**
     * The value to compare the attribute key to.
     */
    @SerialName("value")
    public val value: String

) : FileSearchFilter

/**
 * Combine multiple filters using 'and' or 'or'.
 */
@Serializable
public data class CompoundFilter(
    /**
     * The logical operator to use: 'and' or 'or'.
     */
    @SerialName("type")
    public val type: String,

    /**
     * Array of filters to combine. Items can be ComparisonFilter or CompoundFilter.
     */
    @SerialName("filters")
    public val filters: List<FileSearchFilter>

) : FileSearchFilter

/**
 * Ranking options for search.
 */
@Serializable
public data class FileSearchRankingOptions(
    /**
     * The ranker to use for the file search.
     * Defaults to "auto"
     */
    @SerialName("ranker")
    val ranker: String? = null,

    /**
     * The score threshold for the file search, a number between 0 and 1.
     * Numbers closer to 1 will attempt to return only the most relevant results, but may return fewer results.
     * Defaults to 0
     */
    @SerialName("score_threshold")
    val scoreThreshold: Int? = null,
)

internal class FileSearchFilterSerializer : KSerializer<FileSearchFilter> {

    override val descriptor = buildClassSerialDescriptor("FileSearchFilter")

    override fun serialize(encoder: Encoder, value: FileSearchFilter) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw IllegalArgumentException("This serializer can only be used with JSON")

        when (value) {
            is ComparisonFilter -> ComparisonFilter.serializer().serialize(jsonEncoder, value)
            is CompoundFilter -> CompoundFilter.serializer().serialize(jsonEncoder, value)
        }
    }

    override fun deserialize(decoder: Decoder): FileSearchFilter {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw IllegalArgumentException("This serializer can only be used with JSON")

        return when (val type = jsonDecoder.decodeJsonElement().jsonObject["type"]?.jsonPrimitive?.content) {
            "and" -> CompoundFilter.serializer().deserialize(jsonDecoder)
            "or" -> CompoundFilter.serializer().deserialize(jsonDecoder)
            "eq" -> ComparisonFilter.serializer().deserialize(jsonDecoder)
            "ne" -> ComparisonFilter.serializer().deserialize(jsonDecoder)
            "gt" -> ComparisonFilter.serializer().deserialize(jsonDecoder)
            "gte" -> ComparisonFilter.serializer().deserialize(jsonDecoder)
            "lt" -> ComparisonFilter.serializer().deserialize(jsonDecoder)
            "lte" -> ComparisonFilter.serializer().deserialize(jsonDecoder)
            else -> throw IllegalArgumentException("Unknown filter type: $type")
        }
    }
}

/**
 * File search tool call in a response
 */
@Serializable
@SerialName("file_search_call")
public data class FileSearchToolCall(
    /**
     * The unique ID of the file search tool call.
     */
    @SerialName("id")
    override val id: String,

    /**
     * The status of the file search tool call.
     */
    @SerialName("status")
    val status: ResponseStatus,

    /**
     * The queries used to search for files.
     */
    @SerialName("queries")
    val queries: List<String>,

    /**
     * The results of the file search tool call.
     */
    @SerialName("results")
    val results: List<FileSearchResult>? = null
) : ResponseOutput

/**
 * Result of a file search
 */
@Serializable
public data class FileSearchResult(
    /**
     * The ID of the file
     */
    @SerialName("file_id")
    val fileId: String,

    /**
     * The text content from the file
     */
    @SerialName("text")
    val text: String,

    /**
     * The filename
     */
    @SerialName("filename")
    val filename: String,

    /**
     * The score or relevance rating
     */
    @SerialName("score")
    val score: Double
)
