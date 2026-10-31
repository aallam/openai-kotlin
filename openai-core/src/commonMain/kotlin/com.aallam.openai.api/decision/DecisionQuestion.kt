package com.aallam.openai.api.decision

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.jvm.JvmInline

/** A predicate, categorical choice or ordered score to evaluate against shared evidence. */
@Serializable
public sealed interface DecisionQuestion {
    /** Instructions expressed as text or input text/image/JSON parts without cache breakpoints. */
    public val instructions: DecisionContent
    /** Optional question name returned with its answer. */
    public val name: String?

    /** Estimates the probability that a condition is true. */
    @Serializable @SerialName("predicate")
    public data class Predicate(
        override val instructions: DecisionContent,
        override val name: String? = null,
    ) : DecisionQuestion {
        init { instructions.requireInstructions() }
    }

    /** Selects among distinct string or boolean values. */
    @Serializable @SerialName("choice")
    public data class Choice(
        override val instructions: DecisionContent,
        /** Between two and 255 options; string and boolean values remain distinct. */
        public val choices: List<DecisionChoice>,
        override val name: String? = null,
    ) : DecisionQuestion {
        init {
            instructions.requireInstructions()
            require(choices.size in 2..255) { "Decision choice questions require between 2 and 255 choices" }
            require(choices.map { it.value }.distinct().size == choices.size) { "Decision choice values must be unique" }
        }
    }

    /** Scores evidence against ordered rubric levels. */
    @Serializable @SerialName("score")
    public data class Score(
        override val instructions: DecisionContent,
        /** Between two and ten levels, ordered from lowest to highest. */
        public val levels: List<DecisionScoreLevel>,
        override val name: String? = null,
    ) : DecisionQuestion {
        init {
            instructions.requireInstructions()
            require(levels.size in 2..10) { "Decision score questions require between 2 and 10 levels" }
        }
    }
}

/** An option in a categorical choice question. */
@Serializable
public data class DecisionChoice(
    /** Typed option value. */
    public val value: DecisionValue,
    /** Optional description of when to choose this option. */
    public val description: String? = null,
)

/** One level in an ordered scoring rubric. */
@Serializable
public data class DecisionScoreLevel(
    /** Name of this level. */
    public val label: String,
    /** Optional description of this level's criteria. */
    public val description: String? = null,
)

/** A categorical value; the string `"true"` is distinct from the boolean `true`. */
@Serializable(with = DecisionValueSerializer::class)
public sealed interface DecisionValue {
    /** String option value. */
    @Serializable @JvmInline
    public value class Text(public val value: String) : DecisionValue
    /** Boolean option value. */
    @Serializable @JvmInline
    public value class Boolean(public val value: kotlin.Boolean) : DecisionValue
}

private fun DecisionContent.requireInstructions() {
    requireParts("question instructions") {
        when (it) {
            is DecisionContentPart.InputText -> it.promptCacheBreakpoint == null
            is DecisionContentPart.InputImage -> it.promptCacheBreakpoint == null
            is DecisionContentPart.InputJson -> true
            else -> false
        }
    }
    if (this is DecisionContent.Parts) require(value.isNotEmpty()) { "Decision question instruction parts cannot be empty" }
}

internal object DecisionValueSerializer : JsonContentPolymorphicSerializer<DecisionValue>(DecisionValue::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<DecisionValue> = when {
        element is JsonPrimitive && element.isString -> DecisionValue.Text.serializer()
        element is JsonPrimitive && element.booleanOrNull != null -> DecisionValue.Boolean.serializer()
        else -> throw SerializationException("Decision choice values must be strings or booleans")
    }
}
