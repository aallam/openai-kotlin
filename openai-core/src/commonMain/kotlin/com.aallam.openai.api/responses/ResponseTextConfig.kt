package com.aallam.openai.api.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/** Configuration for text responses */
@Serializable
public data class ResponseTextConfig(
    /** The format to use for text responses */
    @SerialName("format") val format: TextResponseFormatConfiguration
)

/**
 * Configuration for text response format
 */
@Serializable
public sealed interface TextResponseFormatConfiguration

/**
 * Plain text format - default response format.
 * Used to generate text responses.
 */
@Serializable
@SerialName("text")
public data object TextFormat : TextResponseFormatConfiguration

/**
 * JSON object response format. An older method of generating JSON responses.
 * Using `json_schema` is recommended for models that support it.
 * Note that the model will not generate JSON without a system or user message
 * instructing it to do so.
 */
@Serializable
@SerialName("json_object")
public data object JsonObjectFormat : TextResponseFormatConfiguration

/**
 * JSON Schema response format. Used to generate structured JSON responses.
 */
@Serializable
@SerialName("json_schema")
public data class JsonSchemaFormat(
    /** Structured Outputs configuration options, including a JSON Schema */
    @SerialName("json_schema") val jsonSchema: ResponseJsonSchema
) : TextResponseFormatConfiguration

/**
 * Structured Outputs configuration options, including a JSON Schema
 */
@Serializable
public data class ResponseJsonSchema(
    /**
     * A description of what the response format is for, used by the model to
     * determine how to respond in the format.
     */
    @SerialName("description") val description: String? = null,

    /**
     * The name of the response format. Must be a-z, A-Z, 0-9, or contain
     * underscores and dashes, with a maximum length of 64.
     */
    @SerialName("name") val name: String? = null,

    /**
     * The schema for the response format, described as a JSON Schema object.
     */
    @SerialName("schema") val schema: JsonObject,

    /**
     * Whether to enable strict schema adherence when generating the output.
     * If set to true, the model will always follow the exact schema defined
     * in the `schema` field.
     */
    @SerialName("strict") val strict: Boolean? = null
)
