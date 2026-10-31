package com.aallam.openai.client

import com.aallam.openai.api.container.*
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileSource

/**
 * Manages hosted containers and their files. Container creation can incur hosted resource charges.
 * Default implementations preserve source compatibility for custom clients.
 */
public interface Containers {
    /** Creates a container with the requested files, skills and resource policy. */
    public suspend fun createContainer(request: ContainerRequest, requestOptions: RequestOptions? = null): Container =
        throw UnsupportedOperationException("Containers are not supported by this client.")

    /** Retrieves a container, or null if it does not exist. */
    public suspend fun retrieveContainer(id: ContainerId, requestOptions: RequestOptions? = null): Container? =
        throw UnsupportedOperationException("Containers are not supported by this client.")

    /** Lists containers while preserving pagination cursors and the has-more indicator. */
    public suspend fun containersPage(query: ContainerListQuery = ContainerListQuery(), requestOptions: RequestOptions? = null): PaginatedList<Container> =
        throw UnsupportedOperationException("Containers are not supported by this client.")

    /** Deletes a container, returning false if it no longer exists. */
    public suspend fun deleteContainer(id: ContainerId, requestOptions: RequestOptions? = null): Boolean =
        throw UnsupportedOperationException("Containers are not supported by this client.")

    /** Uploads raw bytes once, closing the source even on failure. Use a fresh source for each request; uploads are not automatically retried. */
    public suspend fun uploadContainerFile(id: ContainerId, file: FileSource, requestOptions: RequestOptions? = null): ContainerFile =
        throw UnsupportedOperationException("Containers are not supported by this client.")

    /** Copies a regular File resource into the container using a JSON file_id request. */
    public suspend fun copyContainerFile(id: ContainerId, fileId: FileId, requestOptions: RequestOptions? = null): ContainerFile =
        throw UnsupportedOperationException("Containers are not supported by this client.")

    /** Retrieves a container-file resource, or null if it no longer exists. */
    public suspend fun retrieveContainerFile(id: ContainerId, fileId: ContainerFileId, requestOptions: RequestOptions? = null): ContainerFile? =
        throw UnsupportedOperationException("Containers are not supported by this client.")

    /** Lists container files with pagination metadata. */
    public suspend fun containerFilesPage(id: ContainerId, query: ContainerFileListQuery = ContainerFileListQuery(), requestOptions: RequestOptions? = null): PaginatedList<ContainerFile> =
        throw UnsupportedOperationException("Containers are not supported by this client.")

    /** Deletes a container file, returning false if it no longer exists. */
    public suspend fun deleteContainerFile(id: ContainerId, fileId: ContainerFileId, requestOptions: RequestOptions? = null): Boolean =
        throw UnsupportedOperationException("Containers are not supported by this client.")

    /** Downloads exact file bytes; missing files and other API errors propagate. */
    public suspend fun downloadContainerFile(id: ContainerId, fileId: ContainerFileId, requestOptions: RequestOptions? = null): ByteArray =
        throw UnsupportedOperationException("Containers are not supported by this client.")
}
