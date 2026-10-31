package com.aallam.openai.api.finetuning

import com.aallam.openai.api.core.OrganizationId
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.jvm.JvmInline

/** Current job details, including nullable hyperparameters and paused states, without changing the legacy job type. */
@Serializable
public data class FineTuningJobDetails(
    /** Job identifier. */
    public val id: FineTuningId,
    /** Creation timestamp in Unix seconds. */
    @SerialName("created_at") public val createdAt: Long,
    /** Base model. */
    public val model: ModelId,
    /** Resource object type. */
    @SerialName("object") public val objectType: String,
    /** Owning organization. */
    @SerialName("organization_id") public val organizationId: OrganizationId,
    /** Current state, including pausing and paused. */
    public val status: Status,
    /** Training data file. */
    @SerialName("training_file") public val trainingFile: FileId,
    /** Files containing training results. */
    @SerialName("result_files") public val resultFiles: List<FileId>,
    /** Server error payload; the API permits arbitrary JSON as well as structured error objects. */
    public val error: JsonElement? = null,
    /** Resulting model when available. */
    @SerialName("fine_tuned_model") public val fineTunedModel: ModelId? = null,
    /** Completion timestamp in Unix seconds. */
    @SerialName("finished_at") public val finishedAt: Long? = null,
    /** Training random seed. */
    public val seed: Long? = null,
    /** Number of billable training tokens. */
    @SerialName("trained_tokens") public val trainedTokens: Long? = null,
    /** Validation data file. */
    @SerialName("validation_file") public val validationFile: FileId? = null,
    /** Estimated completion timestamp in Unix seconds. */
    @SerialName("estimated_finish") public val estimatedFinish: Long? = null,
    /** Hyperparameters may be absent or null in current job responses. */
    public val hyperparameters: FineTuningHyperparameters? = null,
    /** Training integrations configured for the job. */
    public val integrations: List<FineTuningWandbIntegration>? = null,
    /** Caller-provided job metadata. */
    public val metadata: Map<String, String>? = null,
    /** Server method configuration, preserving nested supervised, DPO and reinforcement settings. */
    public val method: JsonObject? = null,
)

/** Current numeric training hyperparameters. */
@Serializable
public data class FineTuningHyperparameters(
    /** Batch size, an automatic value, or null. */
    @SerialName("batch_size") public val batchSize: FineTuningNumber? = null,
    /** Learning-rate multiplier or automatic value. */
    @SerialName("learning_rate_multiplier") public val learningRateMultiplier: FineTuningNumber? = null,
    /** Epoch count or automatic value. */
    @SerialName("n_epochs") public val nEpochs: FineTuningNumber? = null,
)

/** A numeric hyperparameter or the server's automatic selection. */
@Serializable(with = FineTuningNumberSerializer::class)
public sealed interface FineTuningNumber {
    /** A finite numeric value, retaining fractional multipliers. */
    @Serializable @JvmInline
    public value class Number(public val value: Double) : FineTuningNumber {
        init { require(value.isFinite()) { "Fine-tuning numeric values must be finite" } }
    }

    /** The literal automatic selection accepted by the API. */
    @Serializable @JvmInline
    public value class Automatic(public val value: String) : FineTuningNumber {
        init { require(value == "auto") { "Expected auto" } }
    }
}

internal object FineTuningNumberSerializer : JsonContentPolymorphicSerializer<FineTuningNumber>(FineTuningNumber::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<FineTuningNumber> = when {
        element is JsonPrimitive && element.isString && element.content == "auto" -> FineTuningNumber.Automatic.serializer()
        element is JsonPrimitive && !element.isString && element.doubleOrNull != null -> FineTuningNumber.Number.serializer()
        else -> throw SerializationException("Expected a fine-tuning number or auto")
    }
}

/** A Weights and Biases training integration. */
@Serializable
public data class FineTuningWandbIntegration(
    /** Integration discriminator. */
    public val type: String,
    /** Weights and Biases configuration. */
    public val wandb: FineTuningWandbSettings,
)

/** Weights and Biases job configuration. */
@Serializable
public data class FineTuningWandbSettings(
    /** Destination project. */
    public val project: String,
    /** Optional entity. */
    public val entity: String? = null,
    /** Optional run name. */
    public val name: String? = null,
    /** Optional run tags. */
    public val tags: List<String>? = null,
)
