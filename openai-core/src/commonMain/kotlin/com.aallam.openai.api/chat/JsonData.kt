package com.aallam.openai.api.chat

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlin.jvm.JvmInline

@JvmInline
@Serializable(with = JsonData.JsonDataSerializer::class)
public value class JsonData(public val json: JsonElement){
    public object JsonDataSerializer: KSerializer<JsonData>{
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("JsonData") {}

        override fun deserialize(decoder: Decoder): JsonData {
            if(decoder is JsonDecoder){
                return JsonData(decoder.decodeJsonElement())
            }
            throw UnsupportedOperationException("Cannot deserialize Parameters")
        }

        override fun serialize(encoder: Encoder, value: JsonData) {
            if(encoder is JsonEncoder){
                encoder.encodeJsonElement(value.json)
                return
            }
        }
    }
    public companion object{
        public fun fromString(json: String): JsonData = fromJsonElement(Json.parseToJsonElement(json))

        public fun fromJsonElement(json: JsonElement): JsonData = JsonData(json)

        public fun builder(block: JsonObjectBuilder.() -> Unit): JsonData{
            val json = buildJsonObject (
                block
            )
            return fromJsonElement(json)
        }

    }
}