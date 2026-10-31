package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.upload.*

/**
 * Upload files in parts, then assemble them into a regular file.
 * Default methods throw [UnsupportedOperationException] so custom [OpenAI] implementations
 * remain source compatible. Clients created by the SDK implement all operations.
 */
public interface Uploads {
    /** Creates an upload with its expected total length and resulting file's expiration policy. */
    public suspend fun upload(
        request: UploadRequest,
        requestOptions: RequestOptions? = null,
    ): Upload = throw UnsupportedOperationException("Uploads are not supported by this client.")

    /**
     * Uploads one part. Consumes and closes [data]'s source; use a new source for every call.
     * Parts may be uploaded concurrently; their final order is chosen by [completeUpload].
     */
    public suspend fun uploadPart(
        id: UploadId,
        data: FileSource,
        requestOptions: RequestOptions? = null,
    ): UploadPart = throw UnsupportedOperationException("Uploads are not supported by this client.")

    /** Assembles the declared part IDs in their supplied order and returns the resulting file. */
    public suspend fun completeUpload(
        id: UploadId,
        request: UploadCompletionRequest,
        requestOptions: RequestOptions? = null,
    ): Upload = throw UnsupportedOperationException("Uploads are not supported by this client.")

    /** Cancels an upload. Server errors, including invalid lifecycle transitions, propagate. */
    public suspend fun cancelUpload(
        id: UploadId,
        requestOptions: RequestOptions? = null,
    ): Upload = throw UnsupportedOperationException("Uploads are not supported by this client.")
}
