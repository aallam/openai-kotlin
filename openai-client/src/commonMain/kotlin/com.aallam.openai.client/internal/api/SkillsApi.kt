package com.aallam.openai.client.internal.api

import com.aallam.openai.api.file.FileSource
import com.aallam.openai.client.internal.SingleUseRequest
import com.aallam.openai.api.core.DeleteResponse
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.skill.*
import com.aallam.openai.client.Skills
import com.aallam.openai.client.internal.extension.appendFileSource
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.plugins.retry
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.io.buffered

internal class SkillsApi(private val requester: HttpRequester) : Skills {
    override suspend fun createSkill(request: SkillUploadRequest, requestOptions: RequestOptions?): Skill =
        upload({ ApiPath.Skills }, request.files, null, requestOptions)

    override suspend fun retrieveSkill(id: SkillId, requestOptions: RequestOptions?): Skill? = getOrNull(skillPath(id), requestOptions)

    override suspend fun updateSkillDefaultVersion(id: SkillId, request: SkillDefaultVersionRequest, requestOptions: RequestOptions?): Skill = requester.perform {
        it.post {
            url(path = skillPath(id))
            contentType(ContentType.Application.Json)
            setBody(request)
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun skillsPage(query: SkillListQuery, requestOptions: RequestOptions?): PaginatedList<Skill> = requester.perform {
        it.get {
            url(path = ApiPath.Skills) {
                query.after?.let { parameter("after", it.id) }
                query.limit?.let { parameter("limit", it) }
                query.order?.let { parameter("order", it.order) }
            }
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun deleteSkill(id: SkillId, requestOptions: RequestOptions?): Boolean = delete(skillPath(id), requestOptions)

    override suspend fun downloadSkill(id: SkillId, requestOptions: RequestOptions?): ByteArray = download("${skillPath(id)}/content", requestOptions)

    override suspend fun createSkillVersion(id: SkillId, request: SkillVersionUploadRequest, requestOptions: RequestOptions?): SkillVersion =
        upload({ "${skillPath(id)}/versions" }, request.files, request.setAsDefault, requestOptions)

    override suspend fun retrieveSkillVersion(id: SkillId, version: String, requestOptions: RequestOptions?): SkillVersion? =
        getOrNull(versionPath(id, version), requestOptions)

    override suspend fun skillVersionsPage(id: SkillId, query: SkillVersionListQuery, requestOptions: RequestOptions?): PaginatedList<SkillVersion> = requester.perform {
        it.get {
            url(path = "${skillPath(id)}/versions") {
                query.after?.let { parameter("after", it.id) }
                query.limit?.let { parameter("limit", it) }
                query.order?.let { parameter("order", it.order) }
            }
            requestOptions(requestOptions)
        }.body()
    }

    override suspend fun deleteSkillVersion(id: SkillId, version: String, requestOptions: RequestOptions?): Boolean = delete(versionPath(id, version), requestOptions)

    override suspend fun downloadSkillVersion(id: SkillId, version: String, requestOptions: RequestOptions?): ByteArray =
        download("${versionPath(id, version)}/content", requestOptions)

    private suspend inline fun <reified T : Any> upload(crossinline path: () -> String, files: SkillFiles, default: Boolean?, options: RequestOptions?): T {
        val originals = when (files) {
            is SkillFiles.Directory -> files.files
            is SkillFiles.Zip -> listOf(files.file)
        }
        val sources = originals.map { FileSource(it.name, it.source.buffered()) }
        var primary: Throwable? = null
        try {
            return requester.perform {
                it.submitFormWithBinaryData(url = path(), formData = formData {
                    when (files) {
                        is SkillFiles.Directory -> sources.forEach { appendFileSource("files[]", it) }
                        is SkillFiles.Zip -> appendFileSource("files", sources.single(), ContentType.Application.Zip)
                    }
                    default?.let { append("default", it.toString()) }
                }) {
                    attributes.put(SingleUseRequest, Unit)
                    retry { maxRetries = 0 }
                    requestOptions(options)
                }.body()
            }
        } catch (cause: Throwable) {
            primary = cause
            throw cause
        } finally {
            var closeFailure: Throwable? = primary
            for (file in sources.asReversed()) {
                try { file.source.close() } catch (cause: Throwable) {
                    val previous = closeFailure
                    if (previous == null) closeFailure = cause else if (previous !== cause) previous.addSuppressed(cause)
                }
            }
            if (primary == null) closeFailure?.let { throw it }
        }
    }

    private suspend inline fun <reified T : Any> getOrNull(path: String, options: RequestOptions?): T? = try {
        requester.perform<T> { it.get { url(path = path); requestOptions(options) } }
    } catch (error: OpenAIAPIException) {
        if (error.statusCode == HttpStatusCode.NotFound.value) null else throw error
    }

    private suspend fun delete(path: String, options: RequestOptions?): Boolean = try {
        requester.perform<DeleteResponse> { it.delete { url(path = path); requestOptions(options) } }.deleted
    } catch (error: OpenAIAPIException) {
        if (error.statusCode == HttpStatusCode.NotFound.value) false else throw error
    }

    private suspend fun download(path: String, options: RequestOptions?): ByteArray = requester.perform {
        it.get { url(path = path); requestOptions(options) }.body()
    }

    private fun skillPath(id: SkillId): String = "${ApiPath.Skills}/${segment(id.id)}"
    private fun versionPath(id: SkillId, version: String): String = "${skillPath(id)}/versions/${segment(version)}"
    private fun segment(value: String): String {
        require(value.isNotBlank() && value != "." && value != ".." && value.none { it in "/\\?#%" || it.isISOControl() }) { "Invalid skill resource identifier" }
        return value
    }
}
