package com.aallam.openai.client.internal.api

import com.aallam.openai.api.batch.BatchId
import com.aallam.openai.api.core.*
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.vectorstore.*
import com.aallam.openai.client.VectorStores
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class VectorStoresApi(private val requester: HttpRequester) : VectorStores {

    override suspend fun createVectorStore(
        request: VectorStoreRequest?,
        requestOptions: RequestOptions?
    ): VectorStore {
        return requester.perform {
            it.post {
                url(path = ApiPath.VectorStores)
                request?.let { req ->
                    setBody(req)
                    contentType(ContentType.Application.Json)
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun vectorStores(
        limit: Int?,
        order: SortOrder?,
        after: VectorStoreId?,
        before: VectorStoreId?,
        requestOptions: RequestOptions?
    ): PaginatedList<VectorStore> {
        return requester.perform {
            it.get {
                url {
                    path(ApiPath.VectorStores)
                    limit?.let { parameter("limit", it) }
                    order?.let { parameter("order", it.order) }
                    after?.let { parameter("after", it.id) }
                    before?.let { parameter("before", it.id) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun vectorStore(id: VectorStoreId, requestOptions: RequestOptions?): VectorStore? {
        try {
            return requester.perform<HttpResponse> {
                it.get {
                    url(path = "${ApiPath.VectorStores}/${id.id}")
                    requestOptions(requestOptions)
                }
            }.body()
        } catch (e: OpenAIAPIException) {
            if (e.statusCode == HttpStatusCode.NotFound.value) return null
            throw e
        }
    }

    override suspend fun updateVectorStore(
        id: VectorStoreId,
        request: VectorStoreRequest,
        requestOptions: RequestOptions?
    ): VectorStore {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.VectorStores}/${id.id}")
                setBody(VectorStoreUpdateRequest(request.name, request.metadata, request.expiresAfter))
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun delete(id: VectorStoreId, requestOptions: RequestOptions?): Boolean {
        val response = requester.perform<HttpResponse> {
            it.delete {
                url(path = "${ApiPath.VectorStores}/${id.id}")
                requestOptions(requestOptions)
            }
        }
        return when (response.status) {
            HttpStatusCode.NotFound -> false
            else -> response.body<DeleteResponse>().deleted
        }
    }

    override suspend fun createVectorStoreFile(
        id: VectorStoreId,
        request: VectorStoreFileRequest,
        requestOptions: RequestOptions?
    ): VectorStoreFile {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.VectorStores}/${id.id}/files")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun vectorStoreFiles(
        id: VectorStoreId,
        limit: Int?,
        order: SortOrder?,
        after: VectorStoreId?,
        before: VectorStoreId?,
        filter: Status?,
        requestOptions: RequestOptions?
    ): PaginatedList<VectorStoreFile> {
        return requester.perform {
            it.get {
                url {
                    path("${ApiPath.VectorStores}/${id.id}/files")
                    limit?.let { parameter("limit", it) }
                    order?.let { parameter("order", it.order) }
                    after?.let { parameter("after", it.id) }
                    before?.let { parameter("before", it.id) }
                    filter?.let { parameter("filter", it.value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun delete(id: VectorStoreId, fileId: FileId, requestOptions: RequestOptions?): Boolean {
        val response = requester.perform<HttpResponse> {
            it.delete {
                url(path = "${ApiPath.VectorStores}/${id.id}/files/${fileId.id}")
                requestOptions(requestOptions)
            }
        }
        return when (response.status) {
            HttpStatusCode.NotFound -> false
            else -> response.body<DeleteResponse>().deleted
        }
    }

    override suspend fun createVectorStoreFilesBatch(
        id: VectorStoreId,
        request: FileBatchRequest,
        requestOptions: RequestOptions?
    ): FilesBatch {
        return requester.perform {
            it.post {
                url(path = "${ApiPath.VectorStores}/${id.id}/file_batches")
                setBody(request)
                contentType(ContentType.Application.Json)
                requestOptions(requestOptions)
            }.body()
        }
    }

    override suspend fun vectorStoreFileBatch(
        vectorStoreId: VectorStoreId,
        batchId: BatchId,
        requestOptions: RequestOptions?
    ): FilesBatch? {
        try {
            return requester.perform<HttpResponse> {
                it.get {
                    url(path = "${ApiPath.VectorStores}/${vectorStoreId.id}/file_batches/${batchId.id}")
                    requestOptions(requestOptions)
                }
            }.body()
        } catch (e: OpenAIAPIException) {
            if (e.statusCode == HttpStatusCode.NotFound.value) return null
            throw e
        }
    }

    override suspend fun cancel(
        vectorStoreId: VectorStoreId,
        batchId: BatchId,
        requestOptions: RequestOptions?
    ): FilesBatch? {
        val response = requester.perform<HttpResponse> {
            it.post {
                url(path = "${ApiPath.VectorStores}/${vectorStoreId.id}/file_batches/${batchId.id}/cancel")
                requestOptions(requestOptions)
            }
        }
        return if (response.status == HttpStatusCode.NotFound) null else response.body()
    }

    override suspend fun vectorStoreFilesBatches(
        vectorStoreId: VectorStoreId,
        batchId: BatchId,
        limit: Int?,
        order: SortOrder?,
        after: VectorStoreId?,
        before: VectorStoreId?,
        filter: Status?,
        requestOptions: RequestOptions?
    ): PaginatedList<VectorStoreFile> {
        return requester.perform {
            it.get {
                url {
                    path("${ApiPath.VectorStores}/${vectorStoreId.id}/file_batches/${batchId.id}/files")
                    limit?.let { parameter("limit", it) }
                    order?.let { parameter("order", it.order) }
                    after?.let { parameter("after", it.id) }
                    before?.let { parameter("before", it.id) }
                    filter?.let { parameter("filter", it.value) }
                }
                requestOptions(requestOptions)
            }.body()
        }
    }
    override suspend fun updateVectorStore(
        id: VectorStoreId, request: VectorStoreUpdateRequest, requestOptions: RequestOptions?
    ): VectorStore = requester.perform {
        it.post {
            url(path = "${ApiPath.VectorStores}/${id.id}")
            contentType(ContentType.Application.Json)
            setBody(request)
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun vectorStoreFile(
        id: VectorStoreId, fileId: FileId, requestOptions: RequestOptions?
    ): VectorStoreFile? = try {
        requester.perform<HttpResponse> {
            it.get {
                url(path = "${ApiPath.VectorStores}/${id.id}/files/${fileId.id}")
                requestOptions(requestOptions)
            }
        }.body()
    } catch (error: OpenAIAPIException) {
        if (error.statusCode == HttpStatusCode.NotFound.value) null else throw error
    }

    override suspend fun updateVectorStoreFile(
        id: VectorStoreId, fileId: FileId,
        attributes: Map<String, kotlinx.serialization.json.JsonPrimitive>?, requestOptions: RequestOptions?
    ): VectorStoreFile = requester.perform {
        it.post {
            url(path = "${ApiPath.VectorStores}/${id.id}/files/${fileId.id}")
            contentType(ContentType.Application.Json)
            setBody(kotlinx.serialization.json.buildJsonObject {
                put("attributes", attributes?.let { values -> kotlinx.serialization.json.JsonObject(values) }
                    ?: kotlinx.serialization.json.JsonNull)
            })
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun vectorStoreFileContent(
        id: VectorStoreId, fileId: FileId, requestOptions: RequestOptions?
    ): VectorStoreContentPage = requester.perform {
        it.get {
            url(path = "${ApiPath.VectorStores}/${id.id}/files/${fileId.id}/content")
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun searchVectorStore(
        id: VectorStoreId, request: VectorStoreSearchRequest, requestOptions: RequestOptions?
    ): VectorStoreSearchPage = requester.perform {
        it.post {
            url(path = "${ApiPath.VectorStores}/${id.id}/search")
            contentType(ContentType.Application.Json)
            setBody(request)
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun vectorStoreFilesPage(
        id: VectorStoreId, limit: Int?, order: SortOrder?, after: FileId?, before: FileId?,
        filter: Status?, requestOptions: RequestOptions?
    ): PaginatedList<VectorStoreFile> = indexedFilesPage(
        "${ApiPath.VectorStores}/${id.id}/files", limit, order, after, before, filter, requestOptions
    )

    override suspend fun vectorStoreBatchFilesPage(
        id: VectorStoreId, batchId: BatchId, limit: Int?, order: SortOrder?, after: FileId?, before: FileId?,
        filter: Status?, requestOptions: RequestOptions?
    ): PaginatedList<VectorStoreFile> = indexedFilesPage(
        "${ApiPath.VectorStores}/${id.id}/file_batches/${batchId.id}/files",
        limit, order, after, before, filter, requestOptions
    )

    private suspend fun indexedFilesPage(
        path: String, limit: Int?, order: SortOrder?, after: FileId?, before: FileId?,
        filter: Status?, requestOptions: RequestOptions?
    ): PaginatedList<VectorStoreFile> = requester.perform {
        it.get {
            url(path = path) {
                limit?.let { parameter("limit", it) }
                order?.let { parameter("order", it.order) }
                after?.let { parameter("after", it.id) }
                before?.let { parameter("before", it.id) }
                filter?.let { parameter("filter", it.value) }
            }
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun createVectorStoreFilesBatch(
        id: VectorStoreId, request: VectorStoreFileBatchRequest, requestOptions: RequestOptions?
    ): FilesBatch = requester.perform {
        it.post {
            url(path = "${ApiPath.VectorStores}/${id.id}/file_batches")
            setBody(request)
            contentType(ContentType.Application.Json)
            requestOptions(requestOptions)
        }.body()
    }

}
