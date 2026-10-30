package com.aallam.openai.client

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.batch.BatchRequest
import com.aallam.openai.api.batch.CompletionWindow
import com.aallam.openai.api.core.Endpoint
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.file.File
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.file.FileUpload
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.vectorstore.*
import kotlinx.io.Buffer
import kotlin.test.Test
import kotlin.test.assertEquals

/** These references compile from a consumer module without serialization's synthetic constructors. */
@OptIn(BetaOpenAI::class)
class TestOfflineRetrievalReferences {
    @Test
    fun originalBatchConstructorAndCopyAritiesRemainUsable() {
        val make = ::BatchRequest
        val file = FileId("file-1")
        val window = CompletionWindow.TwentyFourHours
        val batch = make(file, Endpoint.Completions, window, null)
        val bound = batch::copy
        val unbound = BatchRequest::copy
        assertEquals(batch, bound(file, Endpoint.Completions, window, null))
        assertEquals(batch, unbound(batch, file, Endpoint.Completions, window, null))
    }

    @Test
    fun originalFileAndUploadConstructorAritiesRemainUsable() {
        val make = ::File
        val id = FileId("file-1")
        val purpose = Purpose("batch")
        val file = make(id, 4, 0L, "file.jsonl", purpose, null, null, null)
        val bound = file::copy
        val unbound = File::copy
        assertEquals(file, bound(id, 4, 0L, "file.jsonl", purpose, null, null, null))
        assertEquals(file, unbound(file, id, 4, 0L, "file.jsonl", purpose, null, null, null))
        val upload = ::FileUpload
        assertEquals(purpose, upload(FileSource("file.jsonl", Buffer()), purpose).purpose)
    }

    @Test
    fun originalVectorStoreConstructorAndCopyAritiesRemainUsable() {
        val make = ::VectorStore
        val id = VectorStoreId("vs-1")
        val counts = FileCounts(0L, 0L, 0L, 0L, 0L)
        val store = make(id, 0L, "name", 0L, counts, Status.Completed, null, null, null, null)
        val bound = store::copy
        val unbound = VectorStore::copy
        assertEquals(store, bound(id, 0L, "name", 0L, counts, Status.Completed, null, null, null, null))
        assertEquals(store, unbound(store, id, 0L, "name", 0L, counts, Status.Completed, null, null, null, null))
        val request = ::VectorStoreRequest
        assertEquals("name", request(null, "name", null, null).name)
    }

    @Test
    fun originalAttachmentAndBatchReferencesKeepTheirArities() {
        val file = FileId("file-1")
        val store = VectorStoreId("vs-1")
        val make = ::VectorStoreFile
        val attachment = make(file, 1L, 0L, store, Status.Completed, null)
        val bound = attachment::copy
        val unbound = VectorStoreFile::copy
        assertEquals(attachment, bound(file, 1L, 0L, store, Status.Completed, null))
        assertEquals(attachment, unbound(attachment, file, 1L, 0L, store, Status.Completed, null))
        val request = ::VectorStoreFileRequest
        assertEquals(file, request(file).fileId)
        val batch = ::FileBatchRequest
        val ids = listOf(file)
        val result = batch(ids)
        val boundBatch = result::copy
        val unboundBatch = FileBatchRequest::copy
        assertEquals(result, boundBatch(ids))
        assertEquals(result, unboundBatch(result, ids))
    }
}
