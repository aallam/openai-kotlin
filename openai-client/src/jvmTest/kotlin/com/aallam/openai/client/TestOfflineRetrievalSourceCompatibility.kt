package com.aallam.openai.client

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.batch.BatchId
import com.aallam.openai.api.core.*
import com.aallam.openai.api.file.File
import com.aallam.openai.api.file.*
import com.aallam.openai.api.vectorstore.*
import kotlinx.coroutines.test.runTest
import kotlinx.io.Buffer
import kotlin.test.*


@OptIn(BetaOpenAI::class)
class TestOfflineRetrievalSourceCompatibility {
    @Test
    fun legacyConstructorsBuildersAndGettersRemainUsable() {
        val ids: List<FileId> = listOf(FileId("file-1"))
        val request = FileBatchRequest(ids)
        val oldGetter: List<FileId> = request.fileIds
        assertEquals(ids, oldGetter)
        assertEquals(ids, request.copy(fileIds = ids).component1())
        val source = FileSource("data.jsonl", Buffer())
        assertEquals("batch", FileUpload(source, Purpose("batch")).purpose.raw)
        val upload = fileUpload {
            file = source
            purpose = Purpose("batch")
            expiresAfter = FileExpiration(3600)
        }
        assertEquals(3600, upload.expiresAfter?.seconds)
        val create = VectorStoreRequest(ids, "name", null, mapOf("key" to "value"))
        assertEquals("name", create.name)
        assertEquals("file-1", VectorStoreFileRequest(FileId("file-1")).fileId?.id)
    }

    @Test
    fun legacyInferredMethodReferencesRemainUnambiguous() {
        val client: VectorStores = LegacyVectorStores()
        val update = client::updateVectorStore
        val createBatch = client::createVectorStoreFilesBatch
        assertEquals("updateVectorStore", update.name)
        assertEquals("createVectorStoreFilesBatch", createBatch.name)
    }

    @Test
    fun oldCustomImplementationsCompileAndNewMethodsFailExplicitly() = runTest {
        assertFailsWith<UnsupportedOperationException> { LegacyFiles().filesPage() }
        assertFailsWith<UnsupportedOperationException> {
            LegacyVectorStores().vectorStoreFile(VectorStoreId("vs-1"), FileId("file-1"))
        }
        assertFailsWith<UnsupportedOperationException> {
            LegacyVectorStores().searchVectorStore(VectorStoreId("vs-1"), VectorStoreSearchRequest(listOf("query")))
        }
    }
}

@OptIn(BetaOpenAI::class)
private class LegacyFiles : Files {
    override suspend fun file(request: FileUpload, requestOptions: RequestOptions?): File = throw UnsupportedOperationException()
    override suspend fun files(requestOptions: RequestOptions?): List<File> = throw UnsupportedOperationException()
    override suspend fun file(fileId: FileId, requestOptions: RequestOptions?): File? = throw UnsupportedOperationException()
    override suspend fun delete(fileId: FileId, requestOptions: RequestOptions?): Boolean = throw UnsupportedOperationException()
    override suspend fun download(fileId: FileId, requestOptions: RequestOptions?): ByteArray = throw UnsupportedOperationException()
}

@OptIn(BetaOpenAI::class)
private class LegacyVectorStores : VectorStores {
    override suspend fun createVectorStore(
        request: VectorStoreRequest?,
        requestOptions: RequestOptions?,
    ): VectorStore = throw UnsupportedOperationException()
    override suspend fun vectorStores(
        limit: Int?,
        order: SortOrder?,
        after: VectorStoreId?,
        before: VectorStoreId?,
        requestOptions: RequestOptions?,
    ): List<VectorStore> = throw UnsupportedOperationException()
    override suspend fun vectorStore(id: VectorStoreId, requestOptions: RequestOptions?): VectorStore? = throw UnsupportedOperationException()
    override suspend fun updateVectorStore(
        id: VectorStoreId,
        request: VectorStoreRequest,
        requestOptions: RequestOptions?
    ): VectorStore = throw UnsupportedOperationException()
    override suspend fun delete(id: VectorStoreId, requestOptions: RequestOptions?): Boolean = throw UnsupportedOperationException()
    override suspend fun createVectorStoreFile(
        id: VectorStoreId,
        request: VectorStoreFileRequest,
        requestOptions: RequestOptions?,
    ): VectorStoreFile = throw UnsupportedOperationException()
    override suspend fun vectorStoreFiles(
        id: VectorStoreId,
        limit: Int?,
        order: SortOrder?,
        after: VectorStoreId?,
        before: VectorStoreId?,
        filter: Status?,
        requestOptions: RequestOptions?,
    ): List<VectorStoreFile> = throw UnsupportedOperationException()
    override suspend fun delete(id: VectorStoreId, fileId: FileId, requestOptions: RequestOptions?): Boolean = throw UnsupportedOperationException()
    override suspend fun createVectorStoreFilesBatch(
        id: VectorStoreId,
        request: FileBatchRequest,
        requestOptions: RequestOptions?,
    ): FilesBatch = throw UnsupportedOperationException()
    override suspend fun vectorStoreFileBatch(
        vectorStoreId: VectorStoreId,
        batchId: BatchId,
        requestOptions: RequestOptions?,
    ): FilesBatch? = throw UnsupportedOperationException()
    override suspend fun cancel(
        vectorStoreId: VectorStoreId,
        batchId: BatchId,
        requestOptions: RequestOptions?,
    ): FilesBatch? = throw UnsupportedOperationException()
    override suspend fun vectorStoreFilesBatches(
        vectorStoreId: VectorStoreId,
        batchId: BatchId,
        limit: Int?,
        order: SortOrder?,
        after: VectorStoreId?,
        before: VectorStoreId?,
        filter: Status?,
        requestOptions: RequestOptions?,
    ): List<VectorStoreFile> = throw UnsupportedOperationException()
}
