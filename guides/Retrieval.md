# Files and vector search

Use `filesPage` when listing files to retain cursor metadata. The existing `files()` method remains available for callers that only need the returned list.

```kotlin
val page = openAI.filesPage(limit = 20, purpose = Purpose("batch"))
val next = if (page.hasMore == true) {
    openAI.filesPage(after = page.lastId?.let(::FileId), limit = 20)
} else null
```

File uploads can specify `expiresAfter = FileExpiration(seconds = 3600)`. Batch requests accept the same policy through `outputExpiresAfter` for their output and error files.

## Vector store updates

Create requests accept `description` and `chunkingStrategy`. Update requests have a separate shape so create-only file IDs are not sent to the update endpoint. Omitted fields remain unchanged; explicit clear flags send JSON null.

```kotlin
openAI.updateVectorStore(
    id = VectorStoreId("vs_123"),
    request = VectorStoreUpdateRequest(name = "Product documentation", clearExpiresAfter = true),
)
```

File attachment and batch requests accept attributes and chunking configuration. Batches can use `FileBatchRequest` with `fileIds`, or `VectorStoreFileBatchRequest` with per-file `files` configurations. Use `vectorStoreFilesPage` and `vectorStoreBatchFilesPage` for file listing with `FileId` cursors; these methods retain pagination metadata. The older methods with `VectorStoreId` file cursors are deprecated.

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
