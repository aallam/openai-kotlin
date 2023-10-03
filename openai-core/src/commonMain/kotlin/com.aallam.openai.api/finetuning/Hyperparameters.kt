package com.aallam.openai.api.finetuning

import com.aallam.openai.api.finetuning.Hyperparameters.NEpochs
import com.aallam.openai.api.finetuning.internal.NEpochsSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * A data class representing hyperparameters used during the fine-tuning of a model.
 *
 * This class holds configuration options that guide the training process,
 * and it supports serialization to allow for easy storage and retrieval of the settings.
 */
@Serializable
public data class Hyperparameters(

    /**
     * The number of training epochs.
     *
     * This parameter can either be a specific number or the string "auto",
     * where "auto" implies that the number of epochs will be determined automatically.
     * It uses a sealed interface [NEpochs] to accept either an integer or a string value.
     */
    @SerialName("n_epochs")
    val nEpochs: NEpochs
) {

    public constructor(nEpochs: Int) : this(NEpochs(nEpochs))
    public constructor(nEpochs: String) : this(NEpochs(nEpochs))

    /**
     * A sealed interface representing a flexible parameter for the number of epochs.
     *
     * This interface allows the number of epochs to be either a specific [Int]
     * or a [String] representing an automatic value selection ("auto").
     */
    @Serializable(NEpochsSerializer::class)
    public sealed interface NEpochs {

        /**
         * A value which can be either an [Int] or a [String] representing the number of epochs.
         */
        public val value: Any

        public companion object {
            /**
             * Creates an [NEpochs] instance holding an [Int] value.
             */
            public operator fun invoke(value: Int): NEpochs = NEpochsInt(value)

            /**
             * Creates an [NEpochs] instance holding a [String] value.
             */
            public operator fun invoke(value: String): NEpochs = NEpochsString(value)

            /**
             * A predefined [NEpochs] instance which indicates automatic determination of epochs.
             */
            public val Auto: NEpochs = NEpochsString("auto")
        }
    }
}

/**
 * An [Hyperparameters.NEpochs] implementation that holds an integer value representing a specific number of epochs.
 */
@JvmInline
internal value class NEpochsInt(override val value: Int) : NEpochs

/**
 * An [Hyperparameters.NEpochs] implementation that holds a string value which can be used to specify automatic determination of epochs.
 */
@JvmInline
internal value class NEpochsString(override val value: String) : NEpochs
