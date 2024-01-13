package com.aallam.openai.client

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.thread.Thread
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.api.thread.ThreadRequest

/**
 * Create threads that assistants can interact with.
 */
@BetaOpenAI
public interface Threads {

    /**
     * Create a thread.
     *
     * @param request thread creation request.
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun thread(request: ThreadRequest? = null, requestOptions: RequestOptions? = null): Thread

    /**
     * Retrieve a thread.
     *
     * @param id the identifier of the thread.
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun thread(id: ThreadId, requestOptions: RequestOptions? = null): Thread?

    /**
     * Modify a thread.
     *
     * @param id the identifier of the thread.
     * @param metadata set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun thread(
        id: ThreadId,
        metadata: Map<String, String>,
        requestOptions: RequestOptions? = null
    ): Thread

    /**
     * Delete a thread.
     *
     * @param id the identifier of the thread.
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun delete(id: ThreadId, requestOptions: RequestOptions? = null): Boolean
}
