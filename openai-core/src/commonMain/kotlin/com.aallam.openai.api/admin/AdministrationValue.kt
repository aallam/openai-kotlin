package com.aallam.openai.api.admin

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * A nullable administration field. [Unspecified] omits the field;
 * [Set] supplies a value and [Clear] writes an explicit JSON null.
 */
@Serializable(with = AdministrationValueSerializer::class)
public sealed interface AdministrationValue<out T> {
    /** Leaves the field absent from the request. */
    public data object Unspecified : AdministrationValue<Nothing>

    /** Supplies [value] rather than leaving the field unchanged. */
    public data class Set<T>(public val value: T) : AdministrationValue<T>

    /** Supplies JSON null where the endpoint permits it. */
    public data object Clear : AdministrationValue<Nothing>
}

internal class AdministrationValueSerializer<T>(private val valueSerializer: KSerializer<T>) : KSerializer<AdministrationValue<T>> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

    override fun serialize(encoder: Encoder, value: AdministrationValue<T>) {
        val json = encoder as? JsonEncoder ?: throw SerializationException("Administration fields require JSON")
        json.encodeJsonElement(when (value) {
            AdministrationValue.Unspecified -> throw SerializationException("Unspecified fields must be omitted")
            AdministrationValue.Clear -> JsonNull
            is AdministrationValue.Set -> json.json.encodeToJsonElement(valueSerializer, value.value)
        })
    }

    override fun deserialize(decoder: Decoder): AdministrationValue<T> {
        val json = decoder as? JsonDecoder ?: throw SerializationException("Administration fields require JSON")
        val element = json.decodeJsonElement()
        return if (element == JsonNull) AdministrationValue.Clear
        else AdministrationValue.Set(json.json.decodeFromJsonElement(valueSerializer, element))
    }
}
