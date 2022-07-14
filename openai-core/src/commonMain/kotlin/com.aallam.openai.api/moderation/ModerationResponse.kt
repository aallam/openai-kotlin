package com.aallam.openai.api.moderation

import com.aallam.openai.api.moderation.internal.BooleanIntSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Moderation response.
 */
@Serializable
public class ModerationResponse(

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
    @SerialName("flagged")
    @Serializable(BooleanIntSerializer::class)
    public val flagged: Boolean,
)

@Serializable
public class Categories(

    /**
     * Content that expresses, incites, or promotes hate based on race, gender, ethnicity, religion, nationality, sexual
     * orientation, disability status, or caste.
     */
    @SerialName("hate")
    @Serializable(BooleanIntSerializer::class)
    public val hate: Boolean,

    /**
     * Hateful content that also includes violence or serious harm towards the targeted group.
     */
    @SerialName("hate/threatening")
    @Serializable(BooleanIntSerializer::class)
    public val hateThreatening: Boolean,

    /**
     * Content that promotes, encourages, or depicts acts of self-harm, such as suicide, cutting, and eating disorders.
     */
    @SerialName("self-harm")
    @Serializable(BooleanIntSerializer::class)
    public val selfHarm: Boolean,

    /**
     * 	Content meant to arouse sexual excitement, such as the description of sexual activity, or that promotes sexual
     * 	services (excluding sex education and wellness).
     */
    @SerialName("sexual")
    @Serializable(BooleanIntSerializer::class)
    public val sexual: Boolean,

    /**
     * Sexual content that includes an individual who is under 18 years old.
     */
    @SerialName("sexual/minors")
    @Serializable(BooleanIntSerializer::class)
    public val sexualMinors: Boolean,

    /**
     * Content that promotes or glorifies violence or celebrates the suffering or humiliation of others.
     */
    @SerialName("violence")
    @Serializable(BooleanIntSerializer::class)
    public val violence: Boolean,

    /**
     * Violent content that depicts death, violence, or serious physical injury in extreme graphic detail.
     */
    @SerialName("violence/graphic")
    @Serializable(BooleanIntSerializer::class)
    public val violenceGraphic: Boolean,
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
)
