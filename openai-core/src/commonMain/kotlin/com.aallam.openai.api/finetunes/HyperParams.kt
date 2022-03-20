package com.aallam.openai.api.finetunes

import kotlinx.serialization.SerialName

/** Fine-Tune hyper parameters. */
public data class HyperParams(

    /**
     * The batch size to use for training. The batch size is the number of training examples used to train a single
     * forward and backward pass.
     */
    @SerialName("batch_size") val batchSize: Int,

    /**
     * The learning rate multiplier to use for training. The fine-tuning learning rate is the original learning rate
     * used for pretraining multiplied by this value.
     */
    @SerialName("learning_rate_multiplier") val learningRateMultiplier: Double,

    /** The number of epochs to train the model for. An epoch refers to one full cycle through the training dataset. */
    @SerialName("n_epochs") val nEpochs: Long,

    /**
     * The weight to use for loss on the prompt tokens. This controls how much the model tries to learn to generate the
     * prompt (as compared to the completion which always has a weight of 1.0), and can add a stabilizing effect to
     * training when completions are short.
     */
    @SerialName("prompt_loss_weight") val promptLossWeight: Double,
)
