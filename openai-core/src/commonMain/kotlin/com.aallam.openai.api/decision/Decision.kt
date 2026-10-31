package com.aallam.openai.api.decision

import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Answers in request-question order, including individual refusals and request token usage. */
@Serializable
public data class Decision(
    /** Answers retain question order; one refused question does not invalidate other answers. */
    public val answers: List<DecisionAnswer>,
    /** Model that evaluated the request. */
    public val model: ModelId,
    /** Token accounting including caching and reasoning details. */
    public val usage: DecisionUsage,
)

/** Answer to an individual Decisions question. */
@Serializable
public sealed interface DecisionAnswer {
    /** Original question name, or null for an unnamed question. */
    public val name: String?

    /** Probability that the predicate is true. */
    @Serializable @SerialName("predicate")
    public data class Predicate(
        /** Estimated probability. */
        public val probability: Double,
        override val name: String? = null,
    ) : DecisionAnswer

    /** Selected categorical value and its probability distribution. */
    @Serializable @SerialName("choice")
    public data class Choice(
        /** Selected value, preserving whether it is a string or a boolean. */
        public val choice: DecisionValue,
        /** Confidence in the selected choice. */
        public val confidence: Double,
        /** Probabilities over the available values. */
        public val probabilities: List<DecisionChoiceProbability>,
        override val name: String? = null,
    ) : DecisionAnswer

    /** Probability-weighted score over the ordered rubric. */
    @Serializable @SerialName("score")
    public data class Score(
        /** Probability-weighted level value; may be fractional. */
        public val score: Double,
        /** Confidence in the scoring result. */
        public val confidence: Double,
        /** Distribution over rubric levels. */
        public val probabilities: List<DecisionScoreProbability>,
        override val name: String? = null,
    ) : DecisionAnswer

    /** The model declined this question; other answers can still be used. */
    @Serializable @SerialName("refusal")
    public data class Refusal(
        override val name: String? = null,
    ) : DecisionAnswer
}

/** Probability assigned to one typed choice value. */
@Serializable
public data class DecisionChoiceProbability(
    /** String or boolean option value. */
    public val value: DecisionValue,
    /** Probability of this option. */
    public val probability: Double,
)

/** Probability assigned to one scoring-rubric level. */
@Serializable
public data class DecisionScoreProbability(
    /** Rubric level label. */
    public val label: String,
    /** Numeric level value. */
    public val value: Long,
    /** Probability of this level. */
    public val probability: Double,
)

/** Token usage for a Decisions request. */
@Serializable
public data class DecisionUsage(
    /** Number of input tokens. */
    @SerialName("input_tokens") public val inputTokens: Int,
    /** Cache accounting for input tokens. */
    @SerialName("input_tokens_details") public val inputTokensDetails: DecisionInputTokensDetails,
    /** Number of output tokens. */
    @SerialName("output_tokens") public val outputTokens: Int,
    /** Reasoning accounting for output tokens. */
    @SerialName("output_tokens_details") public val outputTokensDetails: DecisionOutputTokensDetails,
    /** Total input and output tokens. */
    @SerialName("total_tokens") public val totalTokens: Int,
)

/** Input-token cache usage. */
@Serializable
public data class DecisionInputTokensDetails(
    /** Input tokens read from cache. */
    @SerialName("cached_tokens") public val cachedTokens: Int,
    /** Input tokens written to cache. */
    @SerialName("cache_write_tokens") public val cacheWriteTokens: Int,
)

/** Output-token reasoning usage. */
@Serializable
public data class DecisionOutputTokensDetails(
    /** Output tokens used for reasoning. */
    @SerialName("reasoning_tokens") public val reasoningTokens: Int,
)
