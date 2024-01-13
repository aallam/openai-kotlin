package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId

/**
 * List and describe the various models available in the API.
 * You can refer to the [Models](https://beta.openai.com/docs/models) documentation to understand what models are
 * available and the differences between them.
 */
public interface Models {

    /**
     * Lists the currently available models, and provides basic information about each one
     * such as the owner and availability.
     *
     * @param requestOptions request options.
     */
    public suspend fun models(requestOptions: RequestOptions? = null): List<Model>

    /**
     * Retrieves a model instance, providing basic information about the model such as the owner and permission.
     *
     * @param modelId the identifier of the model.
     * @param requestOptions request options.
     */
    public suspend fun model(modelId: ModelId, requestOptions: RequestOptions? = null): Model
}
