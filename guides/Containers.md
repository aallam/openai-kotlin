# Containers

The API's regional and account availability applies to Containers management too. The separate live lifecycle task requires a project that supports Containers and may incur a hosted-container charge:

```sh
OPENAI_CONTAINER_LIVE_TESTS=1 ./gradlew :openai-client:containerLiveJvmTest
```

Supply the API key through `OPENAI_API_KEY`. The ordinary live JVM suite excludes this account-dependent fixture; offline tests cover every Containers operation.

Use the Containers client to provision hosted tool environments, copy or upload files, and retrieve their contents. Container creation can incur hosted resource charges even when no model request is made.

```kotlin
import com.aallam.openai.api.container.*

val container = openAI.createContainer(containerRequest {
    name = "report-workspace"
    memoryLimit = ContainerMemoryLimit.G1
    expiresAfter = ContainerExpirationPolicy(minutes = 20)
    networkPolicy = ContainerNetworkPolicy.Disabled
    skills = listOf(ContainerSkill.Reference("openai-spreadsheets", version = "latest"))
})

try {
    val current = openAI.retrieveContainer(container.id)
    val files = openAI.containerFilesPage(container.id)
    println(current?.status)
    println(files.map { it.path })
} finally {
    openAI.deleteContainer(container.id)
}
```

`ContainerNetworkPolicy.Allowlist` supports allowed domains and optional domain-scoped secrets. `ContainerSkill.Inline` accepts a base64 ZIP bundle through `ContainerInlineSkillSource`. Omitted options leave server defaults in effect. Request expiration requires a minute count; returned expiration fields are independently optional.

## Files

Upload a fresh `FileSource` to send multipart bytes, or copy an existing regular File resource with `copyContainerFile`:

```kotlin
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileSource
import kotlinx.io.files.Path

val uploaded = openAI.uploadContainerFile(container.id, FileSource(Path("report.csv")))
val copied = openAI.copyContainerFile(container.id, FileId("file_existing"))
val metadata = openAI.retrieveContainerFile(container.id, uploaded.id)
val bytes = openAI.downloadContainerFile(container.id, uploaded.id)
openAI.deleteContainerFile(container.id, uploaded.id)
```

Uploads consume and close their source even if request construction, authentication, transport or cancellation fails. Multipart uploads are sent once without automatic retry; use a fresh source for an explicit retry after checking the operation outcome. Container-file IDs are distinct from regular File IDs. Downloads preserve the exact binary bytes. The container path is returned in `ContainerFile.path`; a container-file download does not use the regular Files endpoint.

`containersPage(ContainerListQuery(...))` accepts a name filter, cursor, limit and sort order. `containerFilesPage` uses `ContainerFileListQuery`. Both return pagination metadata: pass `lastId` into the appropriate typed ID as the next query's `after` when `hasMore` is true.

Retrieval returns null for HTTP 404, and deletion returns false for HTTP 404. Other API errors propagate. Use `RequestOptions` for per-request headers, query parameters and timeouts. Custom clients can inherit the default unsupported operations until they implement this capability.

See the official [Containers reference](https://developers.openai.com/api/reference/resources/containers/methods/create) for current policies, limits and availability.
