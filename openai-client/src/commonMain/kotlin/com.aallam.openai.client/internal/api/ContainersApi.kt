package com.aallam.openai.client.internal.api

import com.aallam.openai.client.internal.SingleUseRequest
import com.aallam.openai.api.container.*
import com.aallam.openai.api.core.DeleteResponse
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.client.Containers
import com.aallam.openai.client.internal.extension.appendFileSource
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.plugins.retry
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.io.buffered

internal class ContainersApi(private val requester: HttpRequester) : Containers {
    override suspend fun createContainer(request: ContainerRequest, requestOptions: RequestOptions?): Container = requester.perform {
        it.post {
            url(path = ApiPath.Containers)
            contentType(ContentType.Application.Json)
            setBody(request)
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun retrieveContainer(id: ContainerId, requestOptions: RequestOptions?): Container? =
        getOrNull(containerPath(id), requestOptions)

    override suspend fun containersPage(query: ContainerListQuery, requestOptions: RequestOptions?): PaginatedList<Container> = requester.perform {
        it.get {
            url(path = ApiPath.Containers) {
                query.after?.let { parameter("after", it.id) }
                query.limit?.let { parameter("limit", it) }
                query.name?.let { parameter("name", it) }
                query.order?.let { parameter("order", it.order) }
            }
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun deleteContainer(id: ContainerId, requestOptions: RequestOptions?): Boolean =
        delete(containerPath(id), requestOptions)

    override suspend fun uploadContainerFile(id: ContainerId, file: FileSource, requestOptions: RequestOptions?): ContainerFile =
        file.source.buffered().use { source ->
            requester.perform {
                it.submitFormWithBinaryData(url = "${containerPath(id)}/files", formData = formData {
                    appendFileSource("file", FileSource(file.name, source))
                }) {
                    attributes.put(SingleUseRequest, Unit)
                    retry { maxRetries = 0 }
                    requestOptions(requestOptions)
                }.body()
            }
        }

    override suspend fun copyContainerFile(id: ContainerId, fileId: FileId, requestOptions: RequestOptions?): ContainerFile = requester.perform {
        it.post {
            url(path = "${containerPath(id)}/files")
            contentType(ContentType.Application.Json)
            setBody(buildJsonObject { put("file_id", fileId.id) })
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun retrieveContainerFile(id: ContainerId, fileId: ContainerFileId, requestOptions: RequestOptions?): ContainerFile? =
        getOrNull(filePath(id, fileId), requestOptions)

    override suspend fun containerFilesPage(id: ContainerId, query: ContainerFileListQuery, requestOptions: RequestOptions?): PaginatedList<ContainerFile> = requester.perform {
        it.get {
            url(path = "${containerPath(id)}/files") {
                query.after?.let { parameter("after", it.id) }
                query.limit?.let { parameter("limit", it) }
                query.order?.let { parameter("order", it.order) }
            }
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun deleteContainerFile(id: ContainerId, fileId: ContainerFileId, requestOptions: RequestOptions?): Boolean =
        delete(filePath(id, fileId), requestOptions)

    override suspend fun downloadContainerFile(id: ContainerId, fileId: ContainerFileId, requestOptions: RequestOptions?): ByteArray = requester.perform {
        it.get {
            url(path = "${filePath(id, fileId)}/content")
            requestOptions(requestOptions)
        }.body()
    }

    private suspend inline fun <reified T : Any> getOrNull(path: String, options: RequestOptions?): T? = try {
        requester.perform<T> { it.get { url(path = path); requestOptions(options) } }
    } catch (error: OpenAIAPIException) {
        if (error.statusCode == HttpStatusCode.NotFound.value) null else throw error
    }

    private suspend fun delete(path: String, options: RequestOptions?): Boolean = try {
        requester.perform<DeleteResponse> { it.delete { url(path = path); requestOptions(options) } }.deleted
    } catch (error: OpenAIAPIException) {
        if (error.statusCode == HttpStatusCode.NotFound.value) false else throw error
    }

    private fun containerPath(id: ContainerId): String = "${ApiPath.Containers}/${segment(id.id)}"
    private fun filePath(id: ContainerId, fileId: ContainerFileId): String = "${containerPath(id)}/files/${segment(fileId.id)}"
    private fun segment(value: String): String {
        require(value.isNotBlank() && value != "." && value != ".." && value.none { it in "/\\?#%" || it.isISOControl() }) { "Invalid resource ID" }
        return value
    }
}
