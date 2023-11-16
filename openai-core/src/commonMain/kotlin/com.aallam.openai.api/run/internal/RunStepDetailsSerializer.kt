package com.aallam.openai.api.run.internal

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.run.RunStepDetails
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@BetaOpenAI
internal class RunStepDetailsSerializer : KSerializer<RunStepDetails> {
    override val descriptor: SerialDescriptor
        get() = TODO("Not yet implemented")

    override fun deserialize(decoder: Decoder): RunStepDetails {
        TODO("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: RunStepDetails) {
        TODO("Not yet implemented")
    }

}
