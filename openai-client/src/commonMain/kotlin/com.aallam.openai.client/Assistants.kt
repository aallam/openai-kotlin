package com.aallam.openai.client

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.Assistant
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.assistant.AssistantRequest
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.file.FileId

/**
 * Build assistants that can call models and use tools to perform tasks.
 */
@BetaOpenAI
public interface Assistants {

    /**
     * Create an assistant with a model and instructions.
     *
     * @param request the request to create an assistant.
     */
    @BetaOpenAI
    public suspend fun assistant(request: AssistantRequest, requestOptions: RequestOptions? = null): Assistant

    /**
     * Retrieves an assistant.
     *
     * @param id the ID of the assistant to retrieve.
     */
    @BetaOpenAI
    public suspend fun assistant(id: AssistantId, requestOptions: RequestOptions? = null): Assistant?

    /**
     * Update an assistant.
     *
     * @param id rhe ID of the assistant to modify.
     */
    @BetaOpenAI
    public suspend fun assistant(
        id: AssistantId,
        request: AssistantRequest,
        requestOptions: RequestOptions? = null
    ): Assistant

    /**
     * Delete an assistant.
     *
     * @param id ID of the assistant to delete.
     */
    @BetaOpenAI
    public suspend fun delete(id: AssistantId, requestOptions: RequestOptions? = null): Boolean

    /**
     * Returns a list of assistants.
     *
     * @param limit a limit on the number of objects to be returned. The Limit can range between 1 and 100, and the default is 20.
     * @param order sort order by the `createdAt` timestamp of the objects. [SortOrder.Ascending] for ascending order
     * and [SortOrder.Descending] for descending order.
     * @param after a cursor for use in pagination. `after` is an object ID that defines your place in the list.
     * For instance, if you make a list request and receive 100 objects, ending with obj_foo, your subsequent call can
     * include `after = AssistantId("obj_foo")` in order to fetch the next page of the list.
     * @param before a cursor for use in pagination. Before is an object ID that defines your place in the list.
     * For instance, if you make a list request and receive 100 objects, ending with `obj_foo`, your subsequent call can
     * include `before = AssistantId("obj_foo")` in order to fetch the previous page of the list.
     */
    @BetaOpenAI
    public suspend fun assistants(
        limit: Int? = null,
        order: SortOrder? = null,
        after: AssistantId? = null,
        before: AssistantId? = null,
        requestOptions: RequestOptions? = null
    ): List<Assistant>
}
