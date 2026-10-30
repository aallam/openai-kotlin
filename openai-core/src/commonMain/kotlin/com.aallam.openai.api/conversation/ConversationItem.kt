package com.aallam.openai.api.conversation

import com.aallam.openai.api.core.Role
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * A conversation item. The complete JSON object is preserved so hosted-tool and future item
 * variants can be read and sent back without losing fields. Factories cover common input items.
 */
@Serializable(with = ConversationItem.Serializer::class)
public data class ConversationItem(public val value: JsonObject) {
    /** Server-assigned item identifier, absent for new input messages. */
    public val id: String? get() = value["id"]?.jsonPrimitive?.contentOrNull

    /** Item discriminator, such as message, function_call or function_call_output. */
    public val type: String? get() = value["type"]?.jsonPrimitive?.contentOrNull

    /** Message role, if this is a message item. */
    public val role: Role? get() = value["role"]?.jsonPrimitive?.contentOrNull?.let(::Role)

    /** Message content in its original text or content-part form. */
    public val content: JsonElement? get() = value["content"]

    /** Function call identifier shared by a call and its output. */
    public val callId: String? get() = value["call_id"]?.jsonPrimitive?.contentOrNull

    /** Function output in its original text or content-part form. */
    public val output: JsonElement? get() = value["output"]

    public companion object {
        /** Creates a message containing plain text. */
        public fun message(role: Role, text: String): ConversationItem = message(role, JsonPrimitive(text))

        /** Creates a message containing text or an array of multimodal content parts. */
        public fun message(role: Role, content: JsonElement): ConversationItem = ConversationItem(buildJsonObject {
            put("type", "message")
            put("role", role.role)
            put("content", content)
        })

        /** Supplies a function result for the corresponding call identifier. */
        public fun functionOutput(callId: String, output: String): ConversationItem = ConversationItem(buildJsonObject {
            put("type", "function_call_output")
            put("call_id", callId)
            put("output", output)
        })
    }

    /** Serializes the item as its JSON object rather than as a wrapper property. */
    public object Serializer : KSerializer<ConversationItem> {
        override val descriptor: SerialDescriptor = JsonObject.serializer().descriptor

        override fun serialize(encoder: Encoder, value: ConversationItem) {
            encoder.encodeSerializableValue(JsonObject.serializer(), value.value)
        }

        override fun deserialize(decoder: Decoder): ConversationItem =
            ConversationItem(decoder.decodeSerializableValue(JsonObject.serializer()))
    }
}
