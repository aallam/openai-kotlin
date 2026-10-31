# Multipart uploads

Uploads assemble a large file from independently uploaded parts. The server creates a regular
file after completion. Use its `FileId` with downloads, batches and other file-consuming APIs.

```kotlin
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.upload.UploadCompletionRequest
import com.aallam.openai.api.upload.UploadExpirationPolicy
import com.aallam.openai.api.upload.UploadRequest
import kotlinx.io.files.Path

val upload = openAI.upload(
    UploadRequest(
        bytes = totalBytes,
        filename = "dataset.jsonl",
        mimeType = "application/jsonl",
        purpose = Purpose("batch"),
        expiresAfter = UploadExpirationPolicy(seconds = 86_400),
    )
)
val first = openAI.uploadPart(upload.id, FileSource(Path("dataset-part-1.jsonl")))
val second = openAI.uploadPart(upload.id, FileSource(Path("dataset-part-2.jsonl")))
val completed = openAI.completeUpload(
    upload.id,
    UploadCompletionRequest(partIds = listOf(first.id, second.id)),
)
val fileId = requireNotNull(completed.file).id
val contents = openAI.download(fileId)
```

The order of `partIds` determines the assembled file order, even when parts are uploaded
concurrently. Supply the optional `md5` to verify the assembled contents. Total uploaded bytes
must match the declared length. Each part currently accepts up to 64 MB and an upload up to
8 GB. Intermediate uploads expire after one hour; `expiresAfter` controls the resulting file's
retention separately. Parts are not automatically split or retried with fresh data sources.
Each call consumes and closes its `FileSource`; supply a fresh source for every call.

Call `openAI.cancelUpload(upload.id)` to abandon an upload. Server errors for invalid lifecycle
transitions propagate through the existing API exception types. All operations accept
`RequestOptions` for headers, query options and timeouts.

`Upload.file` is an `UploadedFile` rather than the older `File` model: completed uploads may
return null byte counts or counts larger than a signed 32-bit integer. Its ID remains a normal
`FileId`. Existing file types and method signatures are unchanged.

See the [official Uploads API reference](https://developers.openai.com/api/reference/resources/uploads).
