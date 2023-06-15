package com.aallam.openai.api.chat

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlin.jvm.JvmInline

@JvmInline
@Serializable(with = JsonData.JsonDataSerializer::class)
public value class JsonData(public val jsonData: String){
    public object JsonDataSerializer: KSerializer<JsonData>{
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("JsonData") {}

        override fun deserialize(decoder: Decoder): JsonData {
            if(decoder is JsonDecoder){
                val json = decoder.decodeJsonElement()
                return JsonData(Json.encodeToString(json))
            }
            throw UnsupportedOperationException("Cannot deserialize Parameters")
        }

        override fun serialize(encoder: Encoder, value: JsonData) {
            if(encoder is JsonEncoder){
                val json: JsonElement = Json.decodeFromString(value.jsonData)
                encoder.encodeJsonElement(json)
                return
            }
        }

    }
}