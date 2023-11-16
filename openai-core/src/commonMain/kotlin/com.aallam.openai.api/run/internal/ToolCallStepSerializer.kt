package com.aallam.openai.api.run.internal

import com.aallam.openai.api.run.ToolCallStep
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal class ToolCallStepSerializer : KSerializer<ToolCallStep> {
    override val descriptor: SerialDescriptor
        get() = TODO("Not yet implemented")

    override fun deserialize(decoder: Decoder): ToolCallStep {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: ToolCallStep) {
        TODO("Not yet implemented")
    }
}
