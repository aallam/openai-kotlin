package com.aallam.openai.api.answer.internal

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.answer.QuestionAnswer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * [QuestionAnswer] serializer/deserializer.
 */
@ExperimentalOpenAI
internal class QuestionAnswerSerializer : KSerializer<QuestionAnswer> {

    private val serializer = ListSerializer(String.serializer())

    override val descriptor: SerialDescriptor = serializer.descriptor

    override fun deserialize(decoder: Decoder): QuestionAnswer {
        val strings = serializer.deserialize(decoder)
        return QuestionAnswer(question = strings[0], answer = strings[1])
    }

    override fun serialize(encoder: Encoder, value: QuestionAnswer) {
        serializer.serialize(encoder, listOf(value.question, value.answer))
    }
}
