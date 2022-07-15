package com.aallam.openai.api.finetune

import com.aallam.openai.api.core.Status
import com.aallam.openai.api.file.File
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Fine-tune of a specified model from a given dataset */
@Serializable
public data class FineTune(

    /** A unique id assigned to this fine-tune. */
    @SerialName("id") val id: FineTuneId,

    /** The name of the base model to fine-tune. */
    @SerialName("model") val model: ModelId,

    /** Creation date. */
    @SerialName("created_at") val createdAt: Long,

    /** List of [FineTuneEvent]s. */
    @SerialName("events") val events: List<FineTuneEvent>,

    /** Fine-tuned model. */
    @SerialName("fine_tuned_model") val fineTunedModel: FineTuneModel? = null,

    /** Hyper parameters. */
    @SerialName("hyperparams") val hyperParams: HyperParams? = null,

    /** Organization ID. */
    @SerialName("organization_id") val organizationId: String?,

    /** Result [File]s. */
    @SerialName("result_files") val resultFiles: List<File>,

    /** Fine-Tune status. */
    @SerialName("status") val status: Status,

    /** List of validation [File]s. */
    @SerialName("validation_files") val validationFiles: List<File>,

    /** List of training [File]s. */
    @SerialName("training_files") val trainingFiles: List<File>,

    /** Fine-Tune update date. */
    @SerialName("updated_at") val updatedAt: Long,
)