package com.aallam.openai.api.core

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.OpenAIDsl
import kotlinx.serialization.Serializable


@BetaOpenAI
@Serializable
public data class JsonSchema(

    /**
     * The name of the response format. Must be a-z, A-Z, 0-9, or contain underscores and dashes,
     * with a maximum length of 64.
     */
    val name: String,

    /**
     * A description of what the response format is for,
     * used by the model to determine how to respond in the format.
     */
    val description: String? = null,

    /**
     * The schema for the response format, described as a JSON Schema object.
     */
    val schema: Schema,

    /**
     * Whether to enable strict schema adherence when generating the output.
     * If set to true, the model will always follow the exact schema defined in the schema field.
     * Only a subset of JSON Schema is supported when strict is true.
     * To learn more, read the [Structured Outputs guide](https://platform.openai.com/docs/guides/structured-outputs).
     */
    val strict: Boolean? = null
)

@BetaOpenAI
@OpenAIDsl
public class JsonSchemaBuilder {
    /**
     * The name of the response format. Must be a-z, A-Z, 0-9, or contain underscores and dashes,
     * with a maximum length of 64.
     */
    public var name: String? = null

    /**
     * A description of what the response format is for,
     * used by the model to determine how to respond in the format.
     */
    public var description: String? = null

    /**
     * The schema for the response format, described as a JSON Schema object.
     */
    public var schema: Schema? = Schema.Empty

    /**
     * Whether to enable strict schema adherence when generating the output.
     * If set to true, the model will always follow the exact schema defined in the schema field.
     * Only a subset of JSON Schema is supported when strict is true.
     * To learn more, read the [Structured Outputs guide](https://platform.openai.com/docs/guides/structured-outputs).
     */
    public var strict: Boolean? = true

    public fun build(): JsonSchema = JsonSchema(
        name = requireNotNull(name) { "name is required" },
        description = description,
        schema = requireNotNull(schema) { "schema is required" },
        strict = strict
    )
}

/**
 * Creates a [JsonSchema] instance using a [JsonSchemaBuilder].
 *
 * @param block The [JsonSchemaBuilder] to use.
 */
@OptIn(BetaOpenAI::class)
public fun jsonSchema(block: JsonSchemaBuilder.() -> Unit): JsonSchema =
    JsonSchemaBuilder().apply(block).build()
