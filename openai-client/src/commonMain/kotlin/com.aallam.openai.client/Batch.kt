package com.aallam.openai.client

import com.aallam.openai.api.batch.Batch
import com.aallam.openai.api.batch.BatchId
import com.aallam.openai.api.batch.BatchRequest
import com.aallam.openai.api.core.RequestOptions

/**
 * Create large batches of API requests for asynchronous processing.
 * The Batch API returns completions within 24 hours for a 50% discount.
 */
public interface Batch {

    /**
     * Creates and executes a batch from an uploaded file of requests.
     */
    public suspend fun batch(request: BatchRequest, requestOptions: RequestOptions? = null): Batch

    /**
     * Retrieves a batch.
     */
    public suspend fun batch(id: BatchId, requestOptions: RequestOptions? = null): Batch?

    /**
     * Cancels an in-progress batch.
     */
    public suspend fun cancel(id: BatchId, requestOptions: RequestOptions? = null): Batch?

    /**
     * List your organization's batches.
     *
     * @param after A cursor for use in pagination. After is an object ID that defines your place in the list.
     * For instance, if you make a list request and receive 100 objects, ending with obj_foo, your later call can
     * include after=obj_foo to fetch the next page of the list.
     * @param limit A limit on the number of objects to be returned. Limit can range between 1 and 100, and the default
     * is 20.
     */
    public suspend fun batches(
        after: BatchId? = null,
        limit: Int? = null,
        requestOptions: RequestOptions? = null
    ): List<Batch>
}
