package com.aallam.openai.api.chat.internal

import com.aallam.openai.api.chat.Content
import com.aallam.openai.api.chat.MessageContent
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal class ContentPartsSerializer : KSerializer<Content.Parts> {
    override val descriptor: SerialDescriptor = listSerialDescriptor(MessageContent.serializer().descriptor)

    override fun deserialize(decoder: Decoder): Content.Parts {
        val parts = ListSerializer(MessageContent.serializer()).deserialize(decoder)
        return Content.Parts(parts)
    }

    override fun serialize(encoder: Encoder, value: Content.Parts) {
        ListSerializer(MessageContent.serializer()).serialize(encoder, value.parts)
    }
}