package com.aallam.openai.api.moderation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Moderation response.
 */
@Serializable
public class TextModeration(

    /**
     * Moderation response id.
     */
    @SerialName("id") public val id: String,

    /**
     * Model used for moderation response.
     */
    @SerialName("model") public val model: ModerationModel,

    /**
     * Moderation results.
     */
    @SerialName("results") public val results: List<ModerationResult>,
)

@Serializable
public class ModerationResult(
    /**
     * Per-category binary content policy violation flags.
     */
    @SerialName("categories") public val categories: Categories,
    /**
     * Per-category raw scores output by the model, denoting the model's confidence that the input violates the OpenAI's
     * policy for the category. The value is between 0 and 1, where higher values denote higher confidence. The scores
     * should not be interpreted as probabilities.
     */
    @SerialName("category_scores") public val categoryScores: CategoryScores,

    /**
     * Set to `true` if the model classifies the content as violating OpenAI's content policy, `false` otherwise.
     */
    @SerialName("flagged") public val flagged: Boolean,
)

@Serializable
public class Categories(

    /**
     * Content that expresses, incites, or promotes hate based on race, gender, ethnicity, religion, nationality, sexual
     * orientation, disability status, or caste.
     */
    @SerialName("hate") public val hate: Boolean,

    /**
     * Hateful content that also includes violence or serious harm towards the targeted group.
     */
    @SerialName("hate/threatening") public val hateThreatening: Boolean,

    /**
     * Content that promotes, encourages, or depicts acts of self-harm, such as suicide, cutting, and eating disorders.
     */
    @SerialName("self-harm") public val selfHarm: Boolean,

    /**
     * 	Content meant to arouse sexual excitement, such as the description of sexual activity, or that promotes sexual
     * 	services (excluding sex education and wellness).
     */
    @SerialName("sexual") public val sexual: Boolean,

    /**
     * Sexual content that includes an individual who is under 18 years old.
     */
    @SerialName("sexual/minors") public val sexualMinors: Boolean,

    /**
     * Content that promotes or glorifies violence or celebrates the suffering or humiliation of others.
     */
    @SerialName("violence") public val violence: Boolean,

    /**
     * Violent content that depicts death, violence, or serious physical injury in extreme graphic detail.
     */
    @SerialName("violence/graphic") public val violenceGraphic: Boolean,

    /**
     * Content that expresses, incites, or promotes harassing language towards any target.
     */
    @SerialName("harassment") public val harassment: Boolean,

    /**
     * Harassment content that also includes violence or serious harm towards any target.
     */
    @SerialName("harassment/threatening") public val harassmentThreatening: Boolean,

    /**
     * Content where the speaker expresses that they are engaging or intend to engage in acts of self-harm, such as
     * suicide, cutting, and eating disorders.
     */
    @SerialName("self-harm/intent") public val selfHarmIntent: Boolean,

    /**
     * Content that encourages performing acts of self-harm, such as suicide, cutting, and eating disorders, or that
     * gives instructions or advice on how to commit such acts.
     */
    @SerialName("self-harm/instructions") public val selfHarmInstructions: Boolean,
)

@Serializable
public class CategoryScores(
    /**
     * Content that expresses, incites, or promotes hate based on race, gender, ethnicity, religion, nationality, sexual
     * orientation, disability status, or caste.
     */
    @SerialName("hate") public val hate: Double,

    /**
     * Hateful content that also includes violence or serious harm towards the targeted group.
     */
    @SerialName("hate/threatening") public val hateThreatening: Double,

    /**
     * Content that promotes, encourages, or depicts acts of self-harm, such as suicide, cutting, and eating disorders.
     */
    @SerialName("self-harm") public val selfHarm: Double,

    /**
     * 	Content meant to arouse sexual excitement, such as the description of sexual activity, or that promotes sexual
     * 	services (excluding sex education and wellness).
     */
    @SerialName("sexual") public val sexual: Double,

    /**
     * Sexual content that includes an individual who is under 18 years old.
     */
    @SerialName("sexual/minors") public val sexualMinors: Double,

    /**
     * Content that promotes or glorifies violence or celebrates the suffering or humiliation of others.
     */
    @SerialName("violence") public val violence: Double,

    /**
     * Violent content that depicts death, violence, or serious physical injury in extreme graphic detail.
     */
    @SerialName("violence/graphic") public val violenceGraphic: Double,

    /**
     * The score for the category 'harassment'.
     */
    @SerialName("harassment") public val harassment: Double,

    /**
     * The score for the category 'harassment/threatening'.
     */
    @SerialName("harassment/threatening") public val harassmentThreatening: Double,

    /**
     * The score for the category 'self-harm/intent'.
     */
    @SerialName("self-harm/intent") public val selfHarmIntent: Double,

    /**
     * The score for the category 'self-harm/instructions'.
     */
    @SerialName("self-harm/instructions") public val selfHarmInstructions: Double,
)
