package com.aallam.openai.api.finetuning.internal

import com.aallam.openai.api.finetuning.Hyperparameters
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull

internal class NEpochsSerializer : KSerializer<Hyperparameters.NEpochs> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("n_epochs")

    override fun deserialize(decoder: Decoder): Hyperparameters.NEpochs {
        val decode = decoder as JsonDecoder
        val element = decode.decodeJsonElement() as JsonPrimitive
        return if (element.isString && element.content == "auto") {
            Hyperparameters.NEpochs.Auto
        } else if (element.intOrNull != null) {
            Hyperparameters.NEpochs(element.int)
        } else {
            error("unsupported n_epochs format: $element")
        }
    }

    override fun serialize(encoder: Encoder, value: Hyperparameters.NEpochs) {
        when (val nEpochs = value.value) {
            is String -> encoder.encodeString(nEpochs)
            is Int -> encoder.encodeInt(nEpochs)
        }
    }
}
