package com.aallam.openai.api.engine.internal

import com.aallam.openai.api.engine.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * [EngineId] serializer/deserializer.
 */
internal class EngineIdSerializer : KSerializer<EngineId> {

    override val descriptor: SerialDescriptor = String.serializer().descriptor

    override fun deserialize(decoder: Decoder): EngineId {
        return when (val value = decoder.decodeString()) {
            Ada.id -> Ada
            Babbage.id -> Babbage
            Curie.id -> Curie
            Davinci.id -> Davinci
            else -> EngineId(value)
        }
    }

    override fun serialize(encoder: Encoder, value: EngineId) {
        encoder.encodeString(value.id)
    }
}
