package com.aallam.openai.api.file.internal

import com.aallam.openai.api.file.FileStatus
import com.aallam.openai.api.file.Processed
import com.aallam.openai.api.file.Uploaded
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * [FileStatus] serializer/deserializer.
 */
internal class FileStatusSerializer : KSerializer<FileStatus> {

    override val descriptor: SerialDescriptor = String.serializer().descriptor

    override fun deserialize(decoder: Decoder): FileStatus {
        return when (val value = decoder.decodeString()) {
            Uploaded.raw -> Uploaded
            Processed.raw -> Processed
            else -> FileStatus(value)
        }
    }

    override fun serialize(encoder: Encoder, value: FileStatus) {
        encoder.encodeString(value.raw)
    }
}
