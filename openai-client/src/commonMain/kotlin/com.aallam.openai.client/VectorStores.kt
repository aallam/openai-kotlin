package com.aallam.openai.client

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.batch.BatchId
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.vectorstore.*

/**
 * Vector stores are used to store files for use by the file_search tool.
 */
public interface VectorStores {

    /**
     * Create a new vector store.
     *
     * @param request The request to create a vector store.
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun createVectorStore(
        request: VectorStoreRequest? = null,
        requestOptions: RequestOptions? = null,
    ): VectorStore

    /**
     * List all vector stores.
     *
     * @param limit A limit on the number of objects to be returned. The Limit can range between 1 and 100, and the default is 20.
     * @param order Sort order by the created_at timestamp of the objects.
     * @param after A cursor for use in pagination. After is an object ID that defines your place in the list.
     * For instance, if you make a list request and receive 100 objects, ending with obj_foo, your later call can
     * include after=obj_foo to fetch the next page of the list.
     * @param before A cursor for use in pagination. Before is an object ID that defines your place in the list.
     * For instance, if you make a list request and receive 100 objects, ending with "obj_foo", your later call can
     * include before = "obj_foo" to fetch the previous page of the list.
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun vectorStores(
        limit: Int? = null,
        order: SortOrder? = null,
        after: VectorStoreId? = null,
        before: VectorStoreId? = null,
        requestOptions: RequestOptions? = null,
    ): List<VectorStore>

    /**
     * Retrieve a vector store.
     *
     * @param id The ID of the vector store to retrieve.
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun vectorStore(id: VectorStoreId, requestOptions: RequestOptions? = null): VectorStore?


    /**
     * Update a vector store.
     *
     * @param id The ID of the vector store to update.
     * @param request The request to update a vector store.
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun updateVectorStore(
        id: VectorStoreId,
        request: VectorStoreRequest,
        requestOptions: RequestOptions? = null
    ): VectorStore

    /**
     * Delete a vector store.
     *
     * @param id The ID of the vector store to delete.
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun delete(id: VectorStoreId, requestOptions: RequestOptions? = null): Boolean

    /**
     * Create a vector store file by attaching a File to a vector store.
     *
     * @param id The ID of the vector store that the file should be attached to.
     * @param request The request to create a vector store file.
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun createVectorStoreFile(
        id: VectorStoreId,
        request: VectorStoreFileRequest,
        requestOptions: RequestOptions? = null,
    ): VectorStoreFile

    /**
     * Returns a list of vector store files.
     *
     * @param id The ID of the vector store that the files belong to.
     * @param limit A limit on the number of objects to be returned. The Limit can range between 1 and 100, and the
     * default is 20.
     * @param order Sort order by the created_at timestamp of the objects.
     * @param after A cursor for use in pagination. After is an object ID that defines your place in the list.
     * For instance, if you make a list request and receive 100 objects, ending with obj_foo, your later call can
     * include after=obj_foo to fetch the next page of the list.
     * @param before A cursor for use in pagination. Before is an object ID that defines your place in the list.
     * For instance, if you make a list request and receive 100 objects, ending with obj_foo, your later call can
     * include before=obj_foo to fetch the previous page of the list.
     * @param filter Filter by file status. One of [Status.InProgress], [Status.Completed], [Status.Failed],
     * [Status.Cancelled].
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun vectorStoreFiles(
        id: VectorStoreId,
        limit: Int? = null,
        order: SortOrder? = null,
        after: VectorStoreId? = null,
        before: VectorStoreId? = null,
        filter: Status? = null,
        requestOptions: RequestOptions? = null,
    ): List<VectorStoreFile>

    /**
     * Delete a vector store file.
     * This will remove the file from the vector store, but the file itself will not be deleted.
     * To delete the file, `OpenAI.delete(fileId)`.
     *
     * @param id The ID of the vector store that the file belongs to.
     * @param fileId The ID of the file to delete.
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun delete(id: VectorStoreId, fileId: FileId, requestOptions: RequestOptions? = null): Boolean

    /**
     * Create a batch of vector store files.
     *
     * @param id The ID of the vector store for which to create a File Batch.
     * @param request The request to create a vector store files batch.
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun createVectorStoreFilesBatch(
        id: VectorStoreId,
        request: FileBatchRequest,
        requestOptions: RequestOptions? = null,
    ): FilesBatch

    /**
     * Retrieves a vector store file batch.
     *
     * @param vectorStoreId The ID of the vector store file batch to retrieve.
     * @param batchId The ID of the vector store file batch to retrieve.
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun vectorStoreFileBatch(
        vectorStoreId: VectorStoreId,
        batchId: BatchId,
        requestOptions: RequestOptions? = null,
    ): FilesBatch?

    /**
     * Cancel a vector store file batch.
     * This attempts to cancel the processing of files in this batch as soon as possible.
     *
     * @param vectorStoreId The ID of the vector store file batch to cancel.
     * @param batchId The ID of the vector store file batch to cancel.
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun cancel(
        vectorStoreId: VectorStoreId,
        batchId: BatchId,
        requestOptions: RequestOptions? = null,
    ): FilesBatch?

    /**
     * Returns a list of vector store files in a batch.
     *
     * @param id The ID of the vector store that the files belong to.
     * @param limit A limit on the number of objects to be returned. The Limit can range between 1 and 100, and the
     * default is 20.
     * @param order Sort order by the created_at timestamp of the objects.
     * @param after A cursor for use in pagination. After is an object ID that defines your place in the list.
     * For instance, if you make a list request and receive 100 objects, ending with obj_foo, your later call can
     * include after=obj_foo to fetch the next page of the list.
     * @param before A cursor for use in pagination. Before is an object ID that defines your place in the list.
     * For instance, if you make a list request and receive 100 objects, ending with obj_foo, your later call can
     * include before=obj_foo to fetch the previous page of the list.
     * @param filter Filter by file status. One of [Status.InProgress], [Status.Completed], [Status.Failed],
     * [Status.Cancelled].
     * @param requestOptions request options.
     */
    @BetaOpenAI
    public suspend fun vectorStoreFilesBatches(
        vectorStoreId: VectorStoreId,
        batchId: BatchId,
        limit: Int? = null,
        order: SortOrder? = null,
        after: VectorStoreId? = null,
        before: VectorStoreId? = null,
        filter: Status? = null,
        requestOptions: RequestOptions? = null,
    ): List<VectorStoreFile>
}
