package com.aallam.openai.api.finetuning

import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A data class representing a fine-tuning request.
 */
@Serializable
public data class FineTuningRequest(

    /**
     * The ID of an uploaded file that contains training data.
     *
     * Your dataset must be formatted as a JSONL file, where each training
     * example is a JSON object with the keys "prompt" and "completion".
     */
    @SerialName("training_file")
    val trainingFile: FileId,

    /**
     * The ID of an uploaded file that contains validation data (Optional).
     *
     * Your dataset must be formatted as a JSONL file, where each validation
     * example is a JSON object with the keys "prompt" and "completion".
     */
    @SerialName("validation_file")
    val validationFile: FileId? = null,

    /**
     * The name of the base model to fine-tune (Optional).
     *
     * Can be one of "ada", "babbage", "curie", "davinci", or a fine-tuned model created after 2022-04-21 and before 2023-08-22.
     */
    @SerialName("model")
    val model: ModelId? = null,

    /**
     * The number of epochs to train the model for (Optional).
     *
     * An epoch refers to one full cycle through the training dataset.
     */
    @SerialName("n_epochs")
    val nEpochs: Int? = null,

    /**
     * The batch size to use for training (Optional).
     *
     * Refers to the number of training examples utilized in one iteration.
     */
    @SerialName("batch_size")
    val batchSize: Int? = null,

    /**
     * The learning rate multiplier to use for training (Optional).
     */
    @SerialName("learning_rate_multiplier")
    val learningRateMultiplier: Double? = null,

    /**
     * The weight to use for loss on the prompt tokens (Optional).
     */
    @SerialName("prompt_loss_weight")
    val promptLossWeight: Double? = null,

    /**
     * If set, calculates classification-specific metrics such as accuracy and F-1 score using the validation set (Optional).
     */
    @SerialName("compute_classification_metrics")
    val computeClassificationMetrics: Boolean? = null,

    /**
     * The number of classes in a classification task (Optional).
     */
    @SerialName("classification_n_classes")
    val classificationNClasses: Int? = null,

    /**
     * The positive class in binary classification (Optional).
     */
    @SerialName("classification_positive_class")
    val classificationPositiveClass: String? = null,

    /**
     * If provided, calculates F-beta scores at the specified beta values (Optional).
     */
    @SerialName("classification_betas")
    val classificationBetas: List<Double>? = null,

    /**
     * A string that will be added to your fine-tuned model name (Optional).
     */
    @SerialName("suffix")
    val suffix: String? = null
)
