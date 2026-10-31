package com.aallam.openai.api.decision

import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Evaluates ordered classification or scoring questions against shared input. */
@Serializable
public data class DecisionRequest(
    /** Model supporting the Decisions API. */
    public val model: ModelId,
    /** Shared text or conversation evidence. */
    public val input: DecisionInput,
    /** One to 400 questions; answers retain this order. */
    public val questions: List<DecisionQuestion>,
    /** Optional caller-provided cache partition key. */
    @SerialName("prompt_cache_key") public val promptCacheKey: String? = null,
    /** Implicit or explicit prompt caching policy. */
    @SerialName("prompt_cache_options") public val promptCacheOptions: DecisionPromptCacheOptions? = null,
    /** Opaque end-user identifier, separate from the API credential. */
    @SerialName("safety_identifier") public val safetyIdentifier: String? = null,
) {
    init { require(questions.size in 1..400) { "Decisions requires between 1 and 400 questions" } }
}

/** Creates a Decisions request using the request builder. */
public fun decisionRequest(block: DecisionRequestBuilder.() -> Unit): DecisionRequest =
    DecisionRequestBuilder().apply(block).build()

/** Builder with the same fields as [DecisionRequest]. */
@OpenAIDsl
public class DecisionRequestBuilder {
    /** Model supporting the Decisions API. */
    public var model: ModelId? = null
    /** Shared text or conversation evidence. */
    public var input: DecisionInput? = null
    /** Questions in answer order. */
    public var questions: List<DecisionQuestion> = emptyList()
    /** Optional caller-provided cache partition key. */
    public var promptCacheKey: String? = null
    /** Implicit or explicit prompt caching policy. */
    public var promptCacheOptions: DecisionPromptCacheOptions? = null
    /** Opaque end-user identifier. */
    public var safetyIdentifier: String? = null

    /** Builds a request, requiring a model, input and at least one question. */
    public fun build(): DecisionRequest = DecisionRequest(
        model = requireNotNull(model) { "model is required" },
        input = requireNotNull(input) { "input is required" },
        questions = questions,
        promptCacheKey = promptCacheKey,
        promptCacheOptions = promptCacheOptions,
        safetyIdentifier = safetyIdentifier,
    )
}

/** Prompt-cache policy; omitted fields use server defaults. */
@Serializable
public data class DecisionPromptCacheOptions(
    /** `implicit` lets the API choose a breakpoint; `explicit` uses only supplied breakpoints. */
    public val mode: String? = null,
    /** Minimum cache lifetime, currently `30m`. */
    public val ttl: String? = null,
)
