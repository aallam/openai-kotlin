package com.aallam.openai.api.response

import com.aallam.openai.api.OpenAIDsl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
public sealed interface Tool

@Serializable
@SerialName("function")
public data class FunctionTool(
    val name: ToolName,
    val description: ToolDescription,
    val parameters: Schema,
    val strict: Boolean? = null,
) : Tool

@Serializable
@SerialName("web_search")
public data object WebSearchTool : Tool

@Serializable
public sealed interface Schema

@Serializable @SerialName("object")
public data class ObjectSchema(
    val properties: Map<PropertyName, Schema>,
    val required: List<PropertyName>? = null,
    val additionalProperties: Boolean? = null,
) : Schema

@Serializable @SerialName("string")
public data class StringSchema(
    val description: PropertyDescription,
) : Schema

@Serializable @SerialName("number")
public data class NumberSchema(
    val description: PropertyDescription,
    val minimum: Double? = null,
    val maximum: Double? = null,
) : Schema

@Serializable @SerialName("boolean")
public data class BooleanSchema(
    val description: PropertyDescription,
) : Schema

@Serializable @SerialName("array")
public data class ArraySchema(
    val items: Schema,
    val description: PropertyDescription,
) : Schema

@JvmInline
@Serializable
public value class ToolName(public val name: String)

@JvmInline
@Serializable
public value class ToolDescription(public val description: String)

@JvmInline
@Serializable
public value class PropertyName(public val name: String)

@JvmInline
@Serializable
public value class PropertyDescription(public val description: String)

@JvmInline
@Serializable
public value class EnumValue(public val type: String)

@OpenAIDsl
public class ToolBuilder {
    public var name: ToolName? = null
    public var description: ToolDescription? = null
    public var parameters: Schema? = null
    public var strict: Boolean? = null

    internal fun build(): Tool = FunctionTool(
        name = requireNotNull(name) { "name is required" },
        description = requireNotNull(description) { "description is required" },
        parameters = requireNotNull(parameters) { "parameters is required" },
        strict = strict,
    )
}


