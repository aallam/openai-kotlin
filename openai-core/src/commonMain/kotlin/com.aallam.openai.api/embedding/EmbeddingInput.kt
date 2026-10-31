package com.aallam.openai.api.embedding

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.jvm.JvmInline

/** Text or pre-tokenized input for an embedding request. */
@Serializable(with = EmbeddingInputSerializer::class)
public sealed interface EmbeddingInput {
    /** A single text input. */
    @Serializable @JvmInline
    public value class Text(public val value: String) : EmbeddingInput {
        init { require(value.isNotEmpty()) { "Embedding input cannot be empty" } }
    }

    /** A batch of text inputs. */
    @Serializable @JvmInline
    public value class Texts(public val value: List<String>) : EmbeddingInput {
        init { require(value.isNotEmpty() && value.all { it.isNotEmpty() }) { "Embedding texts cannot be empty" } }
    }

    /** A single pre-tokenized input. */
    @Serializable @JvmInline
    public value class Tokens(public val value: List<Int>) : EmbeddingInput {
        init { require(value.isNotEmpty()) { "Embedding tokens cannot be empty" } }
    }

    /** A batch of pre-tokenized inputs. */
    @Serializable @JvmInline
    public value class TokenBatch(public val value: List<List<Int>>) : EmbeddingInput {
        init { require(value.isNotEmpty() && value.all { it.isNotEmpty() }) { "Embedding token batches cannot be empty" } }
    }
}

internal object EmbeddingInputSerializer : JsonContentPolymorphicSerializer<EmbeddingInput>(EmbeddingInput::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<EmbeddingInput> = when (element) {
        is JsonPrimitive -> {
            if (!element.isString) throw SerializationException("Embedding text input must be a string")
            EmbeddingInput.Text.serializer()
        }
        is JsonArray -> when (val first = element.firstOrNull()) {
            is JsonArray -> EmbeddingInput.TokenBatch.serializer()
            is JsonPrimitive -> if (first.isString) EmbeddingInput.Texts.serializer() else EmbeddingInput.Tokens.serializer()
            null -> EmbeddingInput.Texts.serializer()
            else -> throw SerializationException("Unsupported embedding input")
        }
        else -> throw SerializationException("Unsupported embedding input")
    }
}
