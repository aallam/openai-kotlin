package com.aallam.openai.client

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.file.File
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileRequest
import com.aallam.openai.api.file.FileCreate

/**
 * Files are used to upload documents that can be used across features like [Answers], [Searches], and [Classifications]
 */
public interface Files {

    /**
     * Upload a file that contains document(s) to be used across various endpoints/features.
     * Currently, the size of all the files uploaded by one organization can be up to 1 GB.
     */
    public suspend fun file(request: FileRequest): File

    /**
     * Upload a file that contains document(s) to be used across various endpoints/features.
     * Currently, the size of all the files uploaded by one organization can be up to 1 GB.
     */
    @ExperimentalOpenAI
    public suspend fun file(request: FileCreate): File

    /**
     * Returns a list of files that belong to the user's organization.
     */
    public suspend fun files(): List<File>

    /**
     * Returns information about a specific file.
     */
    public suspend fun file(fileId: FileId): File?

    /**
     * Delete a file. Only owners of organizations can delete files currently.
     */
    public suspend fun delete(fileId: FileId): Boolean
}
