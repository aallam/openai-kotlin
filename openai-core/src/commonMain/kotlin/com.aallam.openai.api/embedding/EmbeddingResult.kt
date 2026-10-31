package com.aallam.openai.api.embedding

import com.aallam.openai.api.core.Usage
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.jvm.JvmInline

/** Embedding response retaining its model and object metadata. */
@Serializable
public data class EmbeddingResult(
    /** Generated embedding vectors. */
    @SerialName("data") public val embeddings: List<EmbeddingData>,
    /** Model used by the server. */
    public val model: ModelId,
    /** Response object kind. */
    @SerialName("object") public val objectType: String,
    /** Request token usage. */
    public val usage: Usage,
)

/** One numeric or encoded embedding and its input index. */
@Serializable
public data class EmbeddingData(
    /** Numeric or base64-encoded vector. */
    public val embedding: EmbeddingVector,
    /** Position in the request input batch. */
    public val index: Int,
    /** Object kind returned by the server. */
    @SerialName("object") public val objectType: String,
)

/** Vector representation returned by the API. */
@Serializable(with = EmbeddingVectorSerializer::class)
public sealed interface EmbeddingVector {
    /** Numeric JSON vector. */
    @Serializable @JvmInline
    public value class Numeric(public val value: List<Double>) : EmbeddingVector

    /** Base64 representation of little-endian IEEE 754 float32 values. */
    @Serializable @JvmInline
    public value class Encoded(public val value: String) : EmbeddingVector

    /**
     * Read vector coordinates, decoding base64 float32 data when necessary.
     * Throws [IllegalArgumentException] for invalid base64 or an incomplete float32 value.
     */
    @OptIn(ExperimentalEncodingApi::class)
    public fun values(): List<Double> = when (this) {
        is Numeric -> value
        is Encoded -> {
            val bytes = Base64.Default.decode(value)
            require(bytes.size % 4 == 0) { "Embedding byte length must be divisible by four" }
            List(bytes.size / 4) { index ->
                val offset = index * 4
                val bits = (bytes[offset].toInt() and 0xff) or
                    ((bytes[offset + 1].toInt() and 0xff) shl 8) or
                    ((bytes[offset + 2].toInt() and 0xff) shl 16) or
                    ((bytes[offset + 3].toInt() and 0xff) shl 24)
                Float.fromBits(bits).toDouble()
            }
        }
    }
}

internal object EmbeddingVectorSerializer : JsonContentPolymorphicSerializer<EmbeddingVector>(EmbeddingVector::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<EmbeddingVector> = when (element) {
        is JsonArray -> EmbeddingVector.Numeric.serializer()
        is JsonPrimitive -> if (element.isString) EmbeddingVector.Encoded.serializer() else throw SerializationException("Expected an embedding array or encoded string")
        else -> throw SerializationException("Expected an embedding array or encoded string")
    }
}
