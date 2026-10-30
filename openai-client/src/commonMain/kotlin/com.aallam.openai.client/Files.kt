package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.file.File
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileUpload

/**
 * Files contain data for Responses, vector stores, batch requests, and fine-tuning.
 */
public interface Files {

    /**
     * Upload a file that contains document(s) to be used across various endpoints/features.
     *
     * @param request file upload request.
     * @param requestOptions request options.
     */
    public suspend fun file(request: FileUpload, requestOptions: RequestOptions? = null): File

    /**
     * Returns a list of files that belong to the user's organization.
     *
     * @param requestOptions request options.
     */
    public suspend fun files(requestOptions: RequestOptions? = null): List<File>

    /** Lists a page of files while preserving pagination cursors. */
    public suspend fun filesPage(
        after: FileId? = null,
        limit: Int? = null,
        order: SortOrder? = null,
        purpose: Purpose? = null,
        requestOptions: RequestOptions? = null,
    ): PaginatedList<File> = throw UnsupportedOperationException("File pagination is not implemented by this client")

    /**
     * Returns information about a specific file.
     *
     * @param fileId the ID of the file to retrieve.
     * @param requestOptions request options.
     */
    public suspend fun file(fileId: FileId, requestOptions: RequestOptions? = null): File?

    /**
     * Delete a file. Only owners of organizations can delete files currently.
     *
     * @param fileId the ID of the file to delete.
     * @param requestOptions request options.
     */
    public suspend fun delete(fileId: FileId, requestOptions: RequestOptions? = null): Boolean

    /**
     * Returns the contents of the specified [fileId].
     *
     * @param fileId the ID of the file to download.
     * @param requestOptions request options.
     */
    public suspend fun download(fileId: FileId, requestOptions: RequestOptions? = null): ByteArray
}
