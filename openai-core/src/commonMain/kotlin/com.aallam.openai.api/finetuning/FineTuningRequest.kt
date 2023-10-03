package com.aallam.openai.api.finetuning

import com.aallam.openai.api.OpenAIDsl
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
     * See [upload file](/docs/api-reference/files/upload) for how to upload a file.
     *
     * Your dataset must be formatted as a JSONL file. Additionally, you must upload your file with the purpose `fine-tune`.
     * See the [fine-tuning guide](/docs/guides/fine-tuning) for more details.
     */
    @SerialName("training_file")
    val trainingFile: FileId,

    /**
     * The  name of the model to fine-tune. You can select one of the
     * [supported models](/docs/guides/fine-tuning/what-models-can-be-fine-tuned).
     */
    @SerialName("model")
    val model: ModelId,

    /**
     * The ID of an uploaded file that contains validation data.
     *
     * If you provide this file, the data is used to generate validation
     * metrics periodically during fine-tuning. These metrics can be viewed in
     * the fine-tuning results file.
     * The same data should not be present in both train and validation files.
     *
     * Your dataset must be formatted as a JSONL file. You must upload your file with the purpose `fine-tune`.
     * See the [fine-tuning guide](/docs/guides/fine-tuning) for more details.
     */
    @SerialName("validation_file")
    val validationFile: FileId? = null,

    /**
     * The hyperparameters used for the fine-tuning job.
     */
    @SerialName("hyperparameters")
    val hyperparameters: Hyperparameters? = null,

    /**
     * A string of up to 18 characters that will be added to your fine-tuned model name.
     * For example, a `suffix` of "custom-model-name" would produce a model name like
     * `ft:gpt-3.5-turbo:openai:custom-model-name:7p4lURel`.
     */
    @SerialName("suffix")
    val suffix: String? = null
)

/**
 * Create a Fine-Tuning request.
 */
public fun fineTuningRequest(block: FineTuningRequestBuilder.() -> Unit): FineTuningRequest =
    FineTuningRequestBuilder().apply(block).build()

/**
 * Builder of [FineTuningRequest] instances.
 */
@OpenAIDsl
public class FineTuningRequestBuilder {

    /**
     * The ID of an uploaded file that contains training data.
     * See [upload file](/docs/api-reference/files/upload) for how to upload a file.
     */
    public var trainingFile: FileId? = null

    /**
     * The name of the model to fine-tune.
     * See [supported models](/docs/guides/fine-tuning/what-models-can-be-fine-tuned) for more details.
     */
    public var model: ModelId? = null

    /**
     * The ID of an uploaded file that contains validation data (Optional).
     * The same data should not be present in both train and validation files.
     */
    public var validationFile: FileId? = null

    /**
     * The hyperparameters used for the fine-tuning job (Optional).
     */
    public var hyperparameters: Hyperparameters? = null

    /**
     * A string of up to 18 characters that will be added to your fine-tuned model name (Optional).
     */
    public var suffix: String? = null

    /**
     * Create a new instance of [FineTuningRequest].
     */
    public fun build(): FineTuningRequest = FineTuningRequest(
        trainingFile = requireNotNull(trainingFile) { "trainingFile is required" },
        model = requireNotNull(model) { "model is required" },
        validationFile = validationFile,
        hyperparameters = hyperparameters,
        suffix = suffix,
    )
}
