package com.aallam.openai.api.moderation.internal

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializer for Booleans as Ints.
 * 1 is true, otherwise it's false.
 */
internal class BooleanIntSerializer : KSerializer<Boolean> {

    override val descriptor: SerialDescriptor = Int.serializer().descriptor

    override fun deserialize(decoder: Decoder): Boolean {
        return decoder.decodeInt() == 1 // true if 1, false otherwise
    }

    override fun serialize(encoder: Encoder, value: Boolean) {
        val encoded = if (value) 1 else 0
        encoder.encodeInt(encoded)
    }
}
