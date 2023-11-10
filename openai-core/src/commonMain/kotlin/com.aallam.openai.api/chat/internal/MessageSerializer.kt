package com.aallam.openai.api.chat.internal

import com.aallam.openai.api.chat.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class MessageSerializer : KSerializer<Message> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Message")

    override fun deserialize(decoder: Decoder): Message {
        require(decoder is JsonDecoder) { "This decoder is not a JsonDecoder. Cannot deserialize `Message`" }
        val json = decoder.decodeJsonElement() as JsonObject
        val role = json["role"]?.jsonPrimitive?.content
        val serializer = when (role) {
            ChatRole.Assistant.role -> AssistantMessage.serializer()
            ChatRole.System.role -> SystemMessage.serializer()
            ChatRole.Tool.role -> ToolMessage.serializer()
            ChatRole.User.role -> UserMessage.serializer()
            ChatRole.Function.role -> FunctionMessage.serializer()
            else -> ChatMessage.serializer()
        }
        return serializer.deserialize(decoder)
    }

    override fun serialize(encoder: Encoder, value: Message) {
        when (value) {
            is AssistantMessage -> AssistantMessage.serializer().serialize(encoder, value)
            is ChatMessage -> ChatMessage.serializer().serialize(encoder, value)
            is SystemMessage -> SystemMessage.serializer().serialize(encoder, value)
            is ToolMessage -> ToolMessage.serializer().serialize(encoder, value)
            is UserMessage -> UserMessage.serializer().serialize(encoder, value)
            is FunctionMessage -> FunctionMessage.serializer().serialize(encoder, value)
        }
    }
}