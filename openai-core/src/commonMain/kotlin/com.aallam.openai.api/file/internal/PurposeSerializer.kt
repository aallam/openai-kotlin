package com.aallam.openai.api.file.internal

import com.aallam.openai.api.file.Purpose
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * [Purpose] serializer/deserializer.
 */
internal class PurposeSerializer : KSerializer<Purpose> {

    override val descriptor: SerialDescriptor = String.serializer().descriptor

    override fun deserialize(decoder: Decoder): Purpose {
        return when (val value = decoder.decodeString()) {
            Purpose.Search.raw -> Purpose.Search
            Purpose.Answers.raw -> Purpose.Answers
            Purpose.Classifications.raw -> Purpose.Classifications
            else -> Purpose.Custom(value)
        }
    }

    override fun serialize(encoder: Encoder, value: Purpose) {
        encoder.encodeString(value.raw)
    }
}
