package com.aallam.openai.client.internal.api

import com.aallam.openai.api.admin.usage.*
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.client.OrganizationUsage
import com.aallam.openai.client.internal.extension.requestOptions
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class OrganizationUsageApi(private val requester: HttpRequester) : OrganizationUsage {
    override suspend fun organizationAudioSpeechesUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions?): OrganizationUsagePage =
        get("organization/usage/audio_speeches", query, setOf("start_time", "api_key_ids", "bucket_width", "end_time", "group_by", "limit", "models", "page", "project_ids", "user_ids"), requestOptions)

    override suspend fun organizationAudioTranscriptionsUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions?): OrganizationUsagePage =
        get("organization/usage/audio_transcriptions", query, setOf("start_time", "api_key_ids", "bucket_width", "end_time", "group_by", "limit", "models", "page", "project_ids", "user_ids"), requestOptions)

    override suspend fun organizationCodeInterpreterSessionsUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions?): OrganizationUsagePage =
        get("organization/usage/code_interpreter_sessions", query, setOf("start_time", "bucket_width", "end_time", "group_by", "limit", "page", "project_ids"), requestOptions)

    override suspend fun organizationCompletionsUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions?): OrganizationUsagePage =
        get("organization/usage/completions", query, setOf("start_time", "api_key_ids", "batch", "bucket_width", "end_time", "group_by", "limit", "models", "page", "project_ids", "user_ids"), requestOptions)

    override suspend fun organizationCosts(query: OrganizationUsageQuery, requestOptions: RequestOptions?): OrganizationUsagePage =
        get("organization/costs", query, setOf("start_time", "api_key_ids", "bucket_width", "end_time", "group_by", "limit", "line_items", "page", "project_ids"), requestOptions)

    override suspend fun organizationDecisionsUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions?): OrganizationUsagePage =
        get("organization/usage/decisions", query, setOf("start_time", "api_key_ids", "batch", "bucket_width", "end_time", "group_by", "limit", "models", "page", "project_ids", "user_ids"), requestOptions)

    override suspend fun organizationEmbeddingsUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions?): OrganizationUsagePage =
        get("organization/usage/embeddings", query, setOf("start_time", "api_key_ids", "bucket_width", "end_time", "group_by", "limit", "models", "page", "project_ids", "user_ids"), requestOptions)

    override suspend fun organizationFileSearchCallsUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions?): OrganizationUsagePage =
        get("organization/usage/file_search_calls", query, setOf("start_time", "api_key_ids", "bucket_width", "end_time", "group_by", "limit", "page", "project_ids", "user_ids", "vector_store_ids"), requestOptions)

    override suspend fun organizationImagesUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions?): OrganizationUsagePage =
        get("organization/usage/images", query, setOf("start_time", "api_key_ids", "bucket_width", "end_time", "group_by", "limit", "models", "page", "project_ids", "sizes", "sources", "user_ids"), requestOptions)

    override suspend fun organizationModerationsUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions?): OrganizationUsagePage =
        get("organization/usage/moderations", query, setOf("start_time", "api_key_ids", "bucket_width", "end_time", "group_by", "limit", "models", "page", "project_ids", "user_ids"), requestOptions)

    override suspend fun organizationVectorStoresUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions?): OrganizationUsagePage =
        get("organization/usage/vector_stores", query, setOf("start_time", "bucket_width", "end_time", "group_by", "limit", "page", "project_ids"), requestOptions)

    override suspend fun organizationWebSearchCallsUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions?): OrganizationUsagePage =
        get("organization/usage/web_search_calls", query, setOf("start_time", "api_key_ids", "bucket_width", "context_levels", "end_time", "group_by", "limit", "models", "page", "project_ids", "user_ids"), requestOptions)

    private suspend fun get(path: String, query: OrganizationUsageQuery, allowed: Set<String>, options: RequestOptions?): OrganizationUsagePage {
        val filters = Parameters.build {
            append("start_time", query.startTime.toString())
            query.endTime?.let { append("end_time", it.toString()) }
            query.bucketWidth?.let { append("bucket_width", it.toString()) }
            query.limit?.let { append("limit", it.toString()) }
            query.page?.let { append("page", it.toString()) }
            query.projectIds?.forEach { append("project_ids[]", it) }
            query.apiKeyIds?.forEach { append("api_key_ids[]", it) }
            query.userIds?.forEach { append("user_ids[]", it) }
            query.models?.forEach { append("models[]", it) }
            query.groupBy?.forEach { append("group_by[]", it) }
            query.batch?.let { append("batch", it.toString()) }
            query.sizes?.forEach { append("sizes[]", it) }
            query.sources?.forEach { append("sources[]", it) }
            query.vectorStoreIds?.forEach { append("vector_store_ids[]", it) }
            query.contextLevels?.forEach { append("context_levels[]", it) }
            query.lineItems?.forEach { append("line_items[]", it) }
        }
        val names = filters.names().map { it.removeSuffix("[]") }.toSet()
        require(names.all { it in allowed }) { "Unsupported filter for $path: ${names - allowed}" }
        return requester.perform {
            it.get {
                url(path = path) {
                    parameters.appendAll(filters)
                }
                requestOptions(options)
            }.body()
        }
    }
}
