package com.aallam.openai.client

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.skill.*

/**
 * Manages hosted skills and immutable versions without executing their contents.
 * Default implementations preserve source compatibility for custom clients.
 */
public interface Skills {
    /** Uploads a skill directory or ZIP once. Every source is closed even on failure; uploads are not automatically retried. */
    public suspend fun createSkill(request: SkillUploadRequest, requestOptions: RequestOptions? = null): Skill =
        throw UnsupportedOperationException("Skills are not supported by this client.")

    /** Retrieves skill metadata, or null for HTTP 404. */
    public suspend fun retrieveSkill(id: SkillId, requestOptions: RequestOptions? = null): Skill? =
        throw UnsupportedOperationException("Skills are not supported by this client.")

    /** Changes the default version pointer to an existing version string. */
    public suspend fun updateSkillDefaultVersion(id: SkillId, request: SkillDefaultVersionRequest, requestOptions: RequestOptions? = null): Skill =
        throw UnsupportedOperationException("Skills are not supported by this client.")

    /** Lists project skills with cursor metadata. */
    public suspend fun skillsPage(query: SkillListQuery = SkillListQuery(), requestOptions: RequestOptions? = null): PaginatedList<Skill> =
        throw UnsupportedOperationException("Skills are not supported by this client.")

    /** Deletes the skill and all its versions; returns false for HTTP 404. */
    public suspend fun deleteSkill(id: SkillId, requestOptions: RequestOptions? = null): Boolean =
        throw UnsupportedOperationException("Skills are not supported by this client.")

    /** Downloads the skill ZIP bundle as exact bytes. HTTP errors propagate. */
    public suspend fun downloadSkill(id: SkillId, requestOptions: RequestOptions? = null): ByteArray =
        throw UnsupportedOperationException("Skills are not supported by this client.")

    /** Uploads an immutable version once, closing all sources even on failure. Newly uploaded versions may briefly return 404 on retrieval. */
    public suspend fun createSkillVersion(id: SkillId, request: SkillVersionUploadRequest, requestOptions: RequestOptions? = null): SkillVersion =
        throw UnsupportedOperationException("Skills are not supported by this client.")

    /** Retrieves a version by its version string, or null for HTTP 404. */
    public suspend fun retrieveSkillVersion(id: SkillId, version: String, requestOptions: RequestOptions? = null): SkillVersion? =
        throw UnsupportedOperationException("Skills are not supported by this client.")

    /** Lists immutable versions with version-resource cursor IDs. */
    public suspend fun skillVersionsPage(id: SkillId, query: SkillVersionListQuery = SkillVersionListQuery(), requestOptions: RequestOptions? = null): PaginatedList<SkillVersion> =
        throw UnsupportedOperationException("Skills are not supported by this client.")

    /** Deletes a version by its version string; the API rejects deletion of a selected default. */
    public suspend fun deleteSkillVersion(id: SkillId, version: String, requestOptions: RequestOptions? = null): Boolean =
        throw UnsupportedOperationException("Skills are not supported by this client.")

    /** Downloads the specified version's ZIP as exact bytes. HTTP errors propagate. */
    public suspend fun downloadSkillVersion(id: SkillId, version: String, requestOptions: RequestOptions? = null): ByteArray =
        throw UnsupportedOperationException("Skills are not supported by this client.")
}
