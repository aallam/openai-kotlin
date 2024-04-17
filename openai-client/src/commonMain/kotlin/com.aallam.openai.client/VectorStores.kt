package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.vectorstore.VectorStore
import com.aallam.openai.api.vectorstore.VectorStoreId
import com.aallam.openai.api.vectorstore.VectorStoreRequest

/**
 * Vector stores are used to store files for use by the file_search tool.
 */
public interface VectorStores {

    /**
     * Create a new vector store.
     */
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
     */
    public suspend fun vectorStores(
        limit: Int? = null,
        order: SortOrder? = null,
        after: VectorStoreId? = null,
        before: VectorStoreId? = null,
        requestOptions: RequestOptions? = null,
    ): List<VectorStore>

    /**
     * Retrieve a vector store.
     */
    public suspend fun vectorStore(id: VectorStoreId, requestOptions: RequestOptions? = null): VectorStore?


    /**
     * Update a vector store.
     */
    public suspend fun updateVectorStore(
        id: VectorStoreId,
        request: VectorStoreRequest,
        requestOptions: RequestOptions? = null
    ): VectorStore

    /**
     * Delete a vector store.
     */
    public suspend fun delete(id: VectorStoreId, requestOptions: RequestOptions? = null): Boolean
}