# Skills

The Skills client uploads versioned bundles and manages their default version pointers. These operations store and retrieve files; they do not execute the skill. Hosted execution is configured separately through a container, Live session or other supported tool environment.

## Directory and ZIP uploads

A directory upload preserves relative filenames inside one top-level folder. Create fresh sources for each upload; the request consumes and closes them.

```kotlin
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.skill.*
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

val skill = openAI.createSkill(skillUploadRequest {
    files = SkillFiles.Directory(listOf(
        FileSource("arithmetic/SKILL.md", SystemFileSystem.source(Path("arithmetic/SKILL.md"))),
        FileSource("arithmetic/references/example.txt", SystemFileSystem.source(Path("arithmetic/references/example.txt"))),
    ))
})

// A ZIP must contain a single top-level skill folder and its SKILL.md manifest.
val zipped = openAI.createSkill(SkillUploadRequest(SkillFiles.Zip(FileSource(Path("arithmetic.zip")))))
```

Directory files are sent as repeated `files[]` multipart parts, preserving paths such as `arithmetic/SKILL.md`. ZIP bundles use the single `files` part with `application/zip`. The API derives the skill name and description from its manifest.

## Immutable versions and defaults

```kotlin
val version = openAI.createSkillVersion(skill.id, skillVersionUploadRequest {
    files = SkillFiles.Zip(FileSource(Path("arithmetic-v2.zip")))
    setAsDefault = false
})

val selected = openAI.updateSkillDefaultVersion(
    skill.id,
    SkillDefaultVersionRequest(version.version),
)
println(selected.defaultVersion)
println(selected.latestVersion)
```

`SkillVersion.id` identifies the version resource and serves as its pagination cursor. `SkillVersion.version` is the version string used in retrieve/delete/download operations and hosted skill references. `defaultVersion` and `latestVersion` can differ.

A newly uploaded version can briefly return HTTP 404. `retrieveSkillVersion` returns null in that case; callers that immediately attach it to a container or Live session should wait with bounded backoff until retrieval succeeds, as described in the official guide. Other errors propagate. Multipart uploads are sent once, without automatic retry. All supplied sources are closed on failure or cancellation, including files whose bodies were not consumed. Check whether creation succeeded before explicitly retrying with fresh sources.

## Listing, downloads and deletion

```kotlin
val skills = openAI.skillsPage(SkillListQuery(limit = 20))
val versions = openAI.skillVersionsPage(skill.id, SkillVersionListQuery(limit = 20))
val metadata = openAI.retrieveSkill(skill.id)
val pinned = openAI.retrieveSkillVersion(skill.id, version.version)
val bundle = openAI.downloadSkill(skill.id)
val pinnedBundle = openAI.downloadSkillVersion(skill.id, version.version)

openAI.deleteSkill(skill.id)
```

Both list operations preserve `firstId`, `lastId` and `hasMore`. For the next page, wrap `lastId` in `SkillId` or `SkillVersionId` and supply it as `after`. ZIP downloads return exact bytes.

Retrieval returns null for HTTP 404 and deletion returns false for HTTP 404. The API rejects deleting a selected default version; select another version first. Deleting a skill cascades to its versions. Use `RequestOptions` for per-request headers, query parameters or timeouts.

See the official [Skills guide](https://developers.openai.com/api/docs/guides/tools-skills) and [create reference](https://developers.openai.com/api/reference/resources/skills/methods/create) for bundle rules, current limits and version availability.
