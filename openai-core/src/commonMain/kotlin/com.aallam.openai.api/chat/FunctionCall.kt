package com.aallam.openai.api.chat

import com.aallam.openai.api.BetaOpenAI
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

@BetaOpenAI
@Serializable(with = FunctionCallSerializer::class)
public sealed interface FunctionCall {
    public val name: String

    public companion object {
        public val Auto: FunctionCall = FunctionCallString("auto")
        public val None: FunctionCall = FunctionCallString("none")
        public fun forceCall(name: String): FunctionCall = FunctionCallObject(name)
    }
}
@OptIn(BetaOpenAI::class)
internal object FunctionCallSerializer: KSerializer<FunctionCall>{
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FunctionCall") {}
    private val objectSerializer = FunctionCallObject.serializer()
    override fun deserialize(decoder: Decoder): FunctionCall {
        if(decoder is JsonDecoder){
            return when(val json = decoder.decodeJsonElement()){
                is JsonPrimitive -> FunctionCallString(json.content)
                is JsonObject -> objectSerializer.deserialize(decoder)
                else -> throw UnsupportedOperationException("Cannot deserialize Parameters")
            }
        }
        throw UnsupportedOperationException("Cannot deserialize Parameters")
    }

    override fun serialize(encoder: Encoder, value: FunctionCall) {
        if(encoder is JsonEncoder){
            when(value){
                is FunctionCallString -> encoder.encodeString(value.name)
                is FunctionCallObject -> objectSerializer.serialize(encoder, value)
            }
            return
        }
        throw UnsupportedOperationException("Cannot deserialize Parameters")
    }
}

@OptIn(BetaOpenAI::class)
@JvmInline
@Serializable
internal value class FunctionCallString(override val name: String): FunctionCall

@OptIn(BetaOpenAI::class)
@Serializable
internal data class FunctionCallObject(override val name: String): FunctionCall

