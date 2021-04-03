package com.aallam.openai.api.classification.internal

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.classification.LabeledExample
import com.aallam.openai.api.file.Purpose
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * [LabeledExample] serializer/deserializer.
 */
@ExperimentalOpenAI
internal class LabeledExampleSerializer : KSerializer<LabeledExample> {

    private val serializer = ListSerializer(String.serializer())

    override val descriptor: SerialDescriptor = serializer.descriptor

    override fun deserialize(decoder: Decoder): LabeledExample {
        val strings = serializer.deserialize(decoder)
        return LabeledExample(example = strings[0], label = strings[1])
    }

    override fun serialize(encoder: Encoder, value: LabeledExample) {
        serializer.serialize(encoder, listOf(value.example, value.label))
    }
}
