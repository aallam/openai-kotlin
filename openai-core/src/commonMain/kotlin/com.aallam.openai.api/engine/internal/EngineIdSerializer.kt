package com.aallam.openai.api.engine.internal

import com.aallam.openai.api.engine.EngineId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * [EngineId] serializer/deserializer.
 */
internal class EngineIdSerializer : KSerializer<EngineId> {

  override val descriptor: SerialDescriptor = String.serializer().descriptor

  override fun deserialize(decoder: Decoder): EngineId {
    return when (val value = decoder.decodeString()) {
      EngineId.Ada.id -> EngineId.Ada
      EngineId.Babbage.id -> EngineId.Babbage
      EngineId.Curie.id -> EngineId.Curie
      EngineId.Davinci.id -> EngineId.Davinci
      else -> EngineId.Custom(value)
    }
  }

  override fun serialize(encoder: Encoder, value: EngineId) {
    encoder.encodeString(value.id)
  }
}
