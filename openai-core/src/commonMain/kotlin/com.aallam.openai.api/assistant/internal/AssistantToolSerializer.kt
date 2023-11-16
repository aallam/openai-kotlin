package com.aallam.openai.api.assistant.internal

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantTool
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

@OptIn(BetaOpenAI::class)
internal class AssistantToolSerializer : KSerializer<AssistantTool> {
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor("AssistantTool", PolymorphicKind.SEALED)

    override fun deserialize(decoder: Decoder): AssistantTool {
        require(decoder is JsonDecoder) { "This decoder is not a JsonDecoder. Cannot deserialize `AssistantTool`" }
        val json = decoder.decodeJsonElement() as JsonObject
        val type = json["type"]?.jsonPrimitive?.content
        return when (type) {
            "code_interpreter" -> AssistantTool.CodeInterpreter
            "retrieval" -> AssistantTool.RetrievalTool
            "function" -> decoder.json.decodeFromJsonElement(AssistantTool.FunctionTool.serializer(), json)
            else -> throw UnsupportedOperationException("Cannot deserialize AssistantTool. Unsupported type $type.")
        }
    }

    override fun serialize(encoder: Encoder, value: AssistantTool) {
        when (value) {
            is AssistantTool.CodeInterpreter -> AssistantTool.CodeInterpreter.serializer().serialize(encoder, value)
            is AssistantTool.RetrievalTool -> AssistantTool.RetrievalTool.serializer().serialize(encoder, value)
            is AssistantTool.FunctionTool -> AssistantTool.FunctionTool.serializer().serialize(encoder, value)
        }
    }
}
