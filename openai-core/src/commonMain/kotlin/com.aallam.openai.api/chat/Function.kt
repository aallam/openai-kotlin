package com.aallam.openai.api.chat

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.Function.Call
import com.aallam.openai.api.chat.Function.Companion.Auto
import com.aallam.openai.api.chat.Function.Companion.None
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
 * - [None]: In this mode, the model does not invoke any function and directly responds to the end-user. This is the default mode when no functions are provided.
 * - [Auto]: In this mode, the model decides whether to call a function or respond directly to the end-user. This mode becomes default if any functions are specified.
 * - [Call]: In this mode, the model will call a specific function, denoted by the `name` attribute.
 */
@BetaOpenAI
@Serializable(with = FunctionCallSerializer::class)
public sealed interface Function {

    /**
     * Represents a function call mode.
     * The value can be any string representing a specific function call mode.
     */
    @JvmInline
    public value class Mode(public val value: String) : Function

    /**
     * Represents a named function call mode.
     * The name indicates a specific function that the model will call.
     *
     * @property name the name of the function to call.
     */
    @Serializable
    public data class Call(public val name: String) : Function

    /** Provides default function call modes. */
    public companion object {
        /** Represents the `auto` mode. */
        public val Auto: Function = Mode("auto")

        /** Represents the `none` mode. */
        public val None: Function = Mode("none")
    }
}

@BetaOpenAI
internal object FunctionCallSerializer : KSerializer<Function> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FunctionCall")

    override fun deserialize(decoder: Decoder): Function {
        require(decoder is JsonDecoder) { "This decoder is not a JsonDecoder. Cannot deserialize `FunctionCall`" }
        return when (val json = decoder.decodeJsonElement()) {
            is JsonPrimitive -> Function.Mode(json.content)
            is JsonObject -> Call.serializer().deserialize(decoder)
            else -> throw UnsupportedOperationException("Cannot deserialize Parameters. Unsupported JSON element.")
        }
    }

    override fun serialize(encoder: Encoder, value: Function) {
        require(encoder is JsonEncoder) { "This encoder is not a JsonEncoder. Cannot serialize `FunctionCall`" }
        when (value) {
            is Function.Mode -> encoder.encodeString(value.value)
            is Call -> Call.serializer().serialize(encoder, value)
        }
    }
}
