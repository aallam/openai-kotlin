# Files and vector search

Use `filesPage` when listing files to retain cursor metadata. The existing `files()` method remains available for callers that only need the returned list.

```kotlin
val page = openAI.filesPage(limit = 20, purpose = Purpose("batch"))
val next = if (page.hasMore == true) {
    openAI.filesPage(after = page.lastId?.let(::FileId), limit = 20)
} else null
```

Use `uploadFile(FileUploadRequest(...))` with `expiresAfter = FileExpiration(seconds = 3600)` for expiring uploads; `retrieveFile` and `filesPage` return `FileResource` including expiry metadata. `createBatch(BatchCreateRequest(...))` accepts the same policy through `outputExpiresAfter` for its output and error files. Legacy upload and batch request models retain their original constructor and copy shapes.

## Vector store updates

Use `createVectorStoreResource(VectorStoreCreateRequest(...))` for `description` and `chunkingStrategy`; it returns `VectorStoreResource`. Update requests have a separate shape so create-only file IDs are not sent to the update endpoint. Omitted fields remain unchanged; explicit clear flags send JSON null.

```kotlin
openAI.updateVectorStoreFields(
    id = VectorStoreId("vs_123"),
    request = VectorStoreUpdateRequest(name = "Product documentation", clearExpiresAfter = true),
)
```

Use `attachVectorStoreFile` with `VectorStoreFileCreateRequest` for attributes and chunking configuration. Current attachment retrieval/update/page methods return `VectorStoreFileResource`. The legacy `FileBatchRequest` remains available for IDs alone. With `createVectorStoreFileBatch`, choose exactly one mode in `VectorStoreFileBatchRequest`: `fileIds` with shared `attributes`/`chunkingStrategy`, or `files` with configuration on each file. Shared settings are rejected with `files` because the API ignores them in that mode. Both modes require between 1 and 2000 files. Use `vectorStoreFilesPage` and `vectorStoreBatchFilesPage` for file listing with `FileId` cursors; these methods retain pagination metadata. The older methods with `VectorStoreId` file cursors are deprecated.

## Search and parsed content

```kotlin
val results = openAI.searchVectorStore(
    id = VectorStoreId("vs_123"),
    request = VectorStoreSearchRequest(
        query = listOf("How does billing work?"),
        maxNumResults = 5,
        rankingOptions = VectorStoreRankingOptions(scoreThreshold = 0.5),
    ),
)
for (result in results.data) {
    println("${result.filename}: ${result.content.mapNotNull { it.text }}")
}
```

Search supports comparison and compound attribute filters through `VectorStoreFilter`. `vectorStoreFile` retrieves an attachment, `updateVectorStoreFile` replaces its attributes (or clears them when passed null), and `vectorStoreFileContent` returns parsed text. Deleting an attachment leaves the original uploaded file intact.

Vector Store requests no longer send an Assistants beta header by default. A service requiring a version header can supply it with `RequestOptions(headers = mapOf("OpenAI-Beta" to "..."))`.
