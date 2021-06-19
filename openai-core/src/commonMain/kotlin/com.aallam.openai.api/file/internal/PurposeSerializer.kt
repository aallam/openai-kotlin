package com.aallam.openai.api.file.internal

import com.aallam.openai.api.file.Answers
import com.aallam.openai.api.file.Classifications
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.file.Search
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
            Search.raw -> Search
            Answers.raw -> Answers
            Classifications.raw -> Classifications
            else -> Purpose(value)
        }
    }

    override fun serialize(encoder: Encoder, value: Purpose) {
        encoder.encodeString(value.raw)
    }
}
