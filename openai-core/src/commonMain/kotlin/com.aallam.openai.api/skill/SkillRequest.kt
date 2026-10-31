package com.aallam.openai.api.skill

import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.file.FileSource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Files uploaded to create a skill or an immutable version. Sources are consumed and closed. */
public sealed interface SkillFiles {
    /**
     * Directory upload, preserving each source's relative filename such as `arithmetic/SKILL.md`.
     * Include a single top-level directory and its SKILL.md manifest.
     */
    public data class Directory(public val files: List<FileSource>) : SkillFiles {
        init { require(files.isNotEmpty()) { "Skill directory upload requires at least one file" } }
    }

    /** A ZIP containing one top-level skill directory. */
    public data class Zip(public val file: FileSource) : SkillFiles
}

/** Uploads a skill directory or ZIP bundle. */
public data class SkillUploadRequest(
    /** Directory files or a single ZIP. */
    public val files: SkillFiles,
)

/** Creates a skill upload request. */
public fun skillUploadRequest(block: SkillUploadRequestBuilder.() -> Unit): SkillUploadRequest =
    SkillUploadRequestBuilder().apply(block).build()

/** Builder for a skill upload request. */
@OpenAIDsl
public class SkillUploadRequestBuilder {
    /** Directory files or a single ZIP. */
    public var files: SkillFiles? = null

    /** Requires files and creates the request. */
    public fun build(): SkillUploadRequest = SkillUploadRequest(requireNotNull(files) { "files is required" })
}

/** Uploads a new immutable version for an existing skill. */
public data class SkillVersionUploadRequest(
    /** Directory files or a single ZIP. */
    public val files: SkillFiles,
    /** Whether to move the skill's default pointer to the new version; omitted uses the server default. */
    public val setAsDefault: Boolean? = null,
)

/** Creates an immutable skill-version upload request. */
public fun skillVersionUploadRequest(block: SkillVersionUploadRequestBuilder.() -> Unit): SkillVersionUploadRequest =
    SkillVersionUploadRequestBuilder().apply(block).build()

/** Builder exposing all version upload options. */
@OpenAIDsl
public class SkillVersionUploadRequestBuilder {
    /** Directory files or a single ZIP. */
    public var files: SkillFiles? = null
    /** Whether to select the new version by default. */
    public var setAsDefault: Boolean? = null

    /** Requires files and creates the version request. */
    public fun build(): SkillVersionUploadRequest = SkillVersionUploadRequest(
        requireNotNull(files) { "files is required" }, setAsDefault,
    )
}

/** Changes the version selected when consumers omit an explicit version. */
@Serializable
public data class SkillDefaultVersionRequest(
    /** Existing version string to select. */
    @SerialName("default_version") public val defaultVersion: String,
)

/** Cursor pagination for project skills. */
public data class SkillListQuery(
    /** Last skill ID from the previous page. */
    public val after: SkillId? = null,
    /** Requested number of results, currently at most 100. */
    public val limit: Int? = null,
    /** Sort order by creation timestamp. */
    public val order: SortOrder? = null,
)

/** Cursor pagination for immutable versions. */
public data class SkillVersionListQuery(
    /** Last version-resource ID from the previous page, not its version string. */
    public val after: SkillVersionId? = null,
    /** Requested number of results, currently at most 100. */
    public val limit: Int? = null,
    /** Sort order by version number. */
    public val order: SortOrder? = null,
)
