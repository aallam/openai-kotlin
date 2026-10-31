@file:OptIn(com.aallam.openai.api.BetaOpenAI::class)

package com.aallam.openai.api.vectorstore

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/** Updates only supplied fields. Use [clearName], [clearMetadata] or [clearExpiresAfter] to send explicit nulls. */
@Serializable(with = VectorStoreUpdateRequest.Serializer::class)
public data class VectorStoreUpdateRequest(
    /** Replacement name; null leaves the name unchanged. */
    public val name: String? = null,
    /** Replacement metadata; null leaves metadata unchanged. */
    public val metadata: Map<String, String>? = null,
    /** Replacement expiration policy; null leaves the policy unchanged. */
    public val expiresAfter: ExpirationPolicy? = null,
    /** Clear the name rather than leaving it unchanged. */
    public val clearName: Boolean = false,
    /** Clear metadata rather than leaving it unchanged. */
    public val clearMetadata: Boolean = false,
    /** Clear expiration rather than leaving it unchanged. */
    public val clearExpiresAfter: Boolean = false,
) {
    init {
        require(!clearName || name == null) { "Cannot set and clear name together" }
        require(!clearMetadata || metadata == null) { "Cannot set and clear metadata together" }
        require(!clearExpiresAfter || expiresAfter == null) { "Cannot set and clear expiration together" }
    }

    internal object Serializer : KSerializer<VectorStoreUpdateRequest> {
        override val descriptor: SerialDescriptor = JsonObject.serializer().descriptor
        override fun serialize(encoder: Encoder, value: VectorStoreUpdateRequest) {
            require(encoder is JsonEncoder)
            encoder.encodeJsonElement(buildJsonObject {
                if (value.clearName) put("name", JsonNull) else value.name?.let { put("name", it) }
                if (value.clearMetadata) put("metadata", JsonNull) else value.metadata?.let {
                    put("metadata", JsonObject(it.mapValues { entry -> JsonPrimitive(entry.value) }))
                }
                if (value.clearExpiresAfter) put("expires_after", JsonNull) else value.expiresAfter?.let {
                    put("expires_after", encoder.json.encodeToJsonElement(ExpirationPolicy.serializer(), it))
                }
            })
        }
        override fun deserialize(decoder: Decoder): VectorStoreUpdateRequest {
            require(decoder is JsonDecoder)
            val obj = decoder.decodeJsonElement().jsonObject
            return VectorStoreUpdateRequest(
                name = obj["name"]?.takeUnless { it is JsonNull }?.jsonPrimitive?.content,
                metadata = obj["metadata"]?.takeUnless { it is JsonNull }?.jsonObject?.mapValues { it.value.jsonPrimitive.content },
                expiresAfter = obj["expires_after"]?.takeUnless { it is JsonNull }?.let {
                    decoder.json.decodeFromJsonElement(ExpirationPolicy.serializer(), it)
                },
                clearName = obj["name"] is JsonNull,
                clearMetadata = obj["metadata"] is JsonNull,
                clearExpiresAfter = obj["expires_after"] is JsonNull,
            )
        }
    }
}
