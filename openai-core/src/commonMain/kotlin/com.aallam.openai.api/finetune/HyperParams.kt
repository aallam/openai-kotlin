package com.aallam.openai.api.finetune

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Fine-Tune hyper parameters. */
@Serializable
public data class HyperParams(

    /**
     * The batch size to use for training. The batch size is the number of training examples used to train a single
     * forward and backward pass.
     */
    @SerialName("batch_size") val batchSize: Long,

    /**
     * The learning rate multiplier to use for training. The fine-tuning learning rate is the original learning rate
     * used for pretraining multiplied by this value.
     */
    @SerialName("learning_rate_multiplier") val learningRateMultiplier: Double,

    /**
     * The number of epochs to train the model for. An epoch refers to one full cycle through the training dataset.
     */
    @SerialName("n_epochs") val nEpochs: Long,

    /**
     * The weight to use for loss on the prompt tokens. This controls how much the model tries to learn to generate the
     * prompt (as compared to the completion which always has a weight of 1.0), and can add a stabilizing effect to
     * training when completions are short.
     */
    @SerialName("prompt_loss_weight") val promptLossWeight: Double,


    /**
     * If set, we calculate classification-specific metrics such as accuracy and F-1 score using the validation set at
     * the end of every epoch. These metrics can be viewed in the results file.
     *
     * In order to compute classification metrics, you must provide a `validation_file`. Additionally, you must specify
     * [classificationNClasses] for multiclass classification or [classificationPositiveClass] for binary classification.
     */
    @SerialName("compute_classification_metrics") val computeClassificationMetrics: Boolean? = null,

    /**
     * The number of classes in a classification task.
     */
    @SerialName("classification_n_classes") val classificationNClasses: Int? = null,

    /**
     * The positive class in binary classification.
     * This parameter is needed to generate precision, recall, and F1 metrics when doing binary classification.
     */
    @SerialName("classification_positive_class") val classificationPositiveClass : String? = null,
)
