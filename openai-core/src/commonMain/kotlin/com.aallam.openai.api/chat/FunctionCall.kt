package com.aallam.openai.api.chat

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.FunctionCall.Companion.Auto
import com.aallam.openai.api.chat.FunctionCall.Companion.None
import com.aallam.openai.api.chat.FunctionCall.Force
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
 * This interface controls how the model responds to function calls.
 *
 * There are several modes:
 * - [None]: In this mode, the model does not call a function and responds to the end-user directly. This is the default mode when no functions are present.
 * - [Auto]: In this mode, the model can decide whether to call a function or respond to the end-user directly. This is the default mode if any functions are present.
 * - [Force]: In this mode, the model will call a specific function, which is indicated by the `name` attribute.
 */
@BetaOpenAI
@Serializable(with = FunctionCallSerializer::class)
public sealed interface FunctionCall {

    /**
     * Represents a flexible function call mode.
     * The name can be any string value representing a function call mode.
     */
    @JvmInline
    public value class Flexible(public val value: String) : FunctionCall

    /**
     * Represents a named function call mode.
     * The name indicates a specific function that the model will call.
     *
     * @property name the name of the function to call.
     */
    @Serializable
    public class Force(public val name: String) : FunctionCall

    /** Contains default function call modes. */
    public companion object {
        /** Represents the 'auto' mode. */
        public val Auto: FunctionCall = Flexible("auto")

        /** Represents the 'none' mode. */
        public val None: FunctionCall = Flexible("none")
    }
}

/**
 * A custom serializer for the `FunctionCall` interface.
 */
@BetaOpenAI
internal object FunctionCallSerializer : KSerializer<FunctionCall> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FunctionCall")

    /**
     * Deserializes a `FunctionCall` from JSON format.
     */
    override fun deserialize(decoder: Decoder): FunctionCall {
        require(decoder is JsonDecoder) { "This decoder is not a JsonDecoder. Cannot deserialize `FunctionCall`" }
        return when (val json = decoder.decodeJsonElement()) {
            is JsonPrimitive -> FunctionCall.Flexible(json.content)
            is JsonObject -> Force.serializer().deserialize(decoder)
            else -> throw UnsupportedOperationException("Cannot deserialize Parameters. Unsupported JSON element.")
        }
    }

    /**
     * Serializes a `FunctionCall` to JSON format.
     */
    override fun serialize(encoder: Encoder, value: FunctionCall) {
        require(encoder is JsonEncoder) { "This encoder is not a JsonEncoder. Cannot serialize `FunctionCall`" }
        when (value) {
            is FunctionCall.Flexible -> encoder.encodeString(value.value)
            is Force -> Force.serializer().serialize(encoder, value)
        }
    }
}
