package com.aallam.openai.api.file.internal

import com.aallam.openai.api.file.FileStatus
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal class FileStatusSerializer : KSerializer<FileStatus> {

    override val descriptor: SerialDescriptor = String.serializer().descriptor

    override fun deserialize(decoder: Decoder): FileStatus {
        return when (val value = decoder.decodeString()) {
            FileStatus.Uploaded.raw -> FileStatus.Uploaded
            FileStatus.Processed.raw -> FileStatus.Processed
            else -> FileStatus.Custom(value)
        }
    }

    override fun serialize(encoder: Encoder, value: FileStatus) {
        encoder.encodeString(value.raw)
    }
}
