package com.aallam.openai.api.file.internal

import com.aallam.openai.api.file.FileId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * [FileId] serializer/deserializer.
 */
internal class FileIdSerializer : KSerializer<FileId> {

    override val descriptor: SerialDescriptor = String.serializer().descriptor

    override fun deserialize(decoder: Decoder): FileId {
        val value = decoder.decodeString()
        return FileId(value)
    }

    override fun serialize(encoder: Encoder, value: FileId) {
        encoder.encodeString(value.raw)
    }
}
