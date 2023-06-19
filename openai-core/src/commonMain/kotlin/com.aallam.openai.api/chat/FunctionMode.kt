package com.aallam.openai.api.chat

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.FunctionMode.Companion.Auto
import com.aallam.openai.api.chat.FunctionMode.Companion.None
import com.aallam.openai.api.chat.FunctionMode.Default
import com.aallam.openai.api.chat.FunctionMode.Named
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.jvm.JvmInline

/**
 * This interface determines how the model handles function calls.
 *
 * There are several modes of operation:
 * - [Default]: In this mode, the model does not invoke any function [None] or decides itself [Auto] on calling a function or responding directly to the end-user. This mode becomes default if any functions are specified.
 * - [Named]: In this mode, the model will call a specific function, denoted by the `name` attribute.
 */
@BetaOpenAI
@Serializable(with = FunctionModeSerializer::class)
public sealed interface FunctionMode {

    /**
     * Represents a function call mode.
     * The value can be any string representing a specific function call mode.
     */
    @JvmInline
    public value class Default(public val value: String) : FunctionMode

    /**
     * Represents a named function call mode.
     * The name indicates a specific function that the model will call.
     *
     * @property name the name of the function to call.
     */
    @Serializable
    public data class Named(public val name: String) : FunctionMode

    /** Provides default function call modes. */
    public companion object {
        /** Represents the `auto` mode. */
        public val Auto: FunctionMode = Default("auto")

        /** Represents the `none` mode. */
        public val None: FunctionMode = Default("none")
    }
}

@BetaOpenAI
internal object FunctionModeSerializer : KSerializer<FunctionMode> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FunctionCall")

    override fun deserialize(decoder: Decoder): FunctionMode {
        require(decoder is JsonDecoder) { "This decoder is not a JsonDecoder. Cannot deserialize `FunctionCall`" }
        return when (val json = decoder.decodeJsonElement()) {
            is JsonPrimitive -> Default(json.content)
            is JsonObject -> Named.serializer().deserialize(decoder)
            else -> throw UnsupportedOperationException("Cannot deserialize FunctionMode. Unsupported JSON element.")
        }
    }

    override fun serialize(encoder: Encoder, value: FunctionMode) {
        require(encoder is JsonEncoder) { "This encoder is not a JsonEncoder. Cannot serialize `FunctionCall`" }
        when (value) {
            is Default -> encoder.encodeString(value.value)
            is Named -> Named.serializer().serialize(encoder, value)
        }
    }
}
