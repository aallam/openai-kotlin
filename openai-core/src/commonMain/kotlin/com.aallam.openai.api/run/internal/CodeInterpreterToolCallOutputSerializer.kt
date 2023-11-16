package com.aallam.openai.api.run.internal

import com.aallam.openai.api.run.CodeInterpreterToolCallOutput
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal class CodeInterpreterToolCallOutputSerializer: KSerializer<CodeInterpreterToolCallOutput> {
    override val descriptor: SerialDescriptor
        get() = TODO("Not yet implemented")

    override fun deserialize(decoder: Decoder): CodeInterpreterToolCallOutput {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: CodeInterpreterToolCallOutput) {
        TODO("Not yet implemented")
    }
}
