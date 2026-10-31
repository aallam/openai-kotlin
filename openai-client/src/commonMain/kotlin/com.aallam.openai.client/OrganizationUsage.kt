package com.aallam.openai.client

import com.aallam.openai.api.admin.usage.OrganizationUsagePage
import com.aallam.openai.api.admin.usage.OrganizationUsageQuery
import com.aallam.openai.api.core.RequestOptions

/** Reads organization usage and cost reports using an Admin API key. */
public interface OrganizationUsage {
    /** Retrieves audio speeches buckets with their pagination cursor. */
    public suspend fun organizationAudioSpeechesUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions? = null): OrganizationUsagePage =
        throw UnsupportedOperationException("Organization usage is not supported by this client.")

    /** Retrieves audio transcriptions buckets with their pagination cursor. */
    public suspend fun organizationAudioTranscriptionsUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions? = null): OrganizationUsagePage =
        throw UnsupportedOperationException("Organization usage is not supported by this client.")

    /** Retrieves code interpreter sessions buckets with their pagination cursor. */
    public suspend fun organizationCodeInterpreterSessionsUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions? = null): OrganizationUsagePage =
        throw UnsupportedOperationException("Organization usage is not supported by this client.")

    /** Retrieves completions buckets with their pagination cursor. */
    public suspend fun organizationCompletionsUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions? = null): OrganizationUsagePage =
        throw UnsupportedOperationException("Organization usage is not supported by this client.")

    /** Retrieves costs buckets with their pagination cursor. */
    public suspend fun organizationCosts(query: OrganizationUsageQuery, requestOptions: RequestOptions? = null): OrganizationUsagePage =
        throw UnsupportedOperationException("Organization usage is not supported by this client.")

    /** Retrieves decisions buckets with their pagination cursor. */
    public suspend fun organizationDecisionsUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions? = null): OrganizationUsagePage =
        throw UnsupportedOperationException("Organization usage is not supported by this client.")

    /** Retrieves embeddings buckets with their pagination cursor. */
    public suspend fun organizationEmbeddingsUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions? = null): OrganizationUsagePage =
        throw UnsupportedOperationException("Organization usage is not supported by this client.")

    /** Retrieves file search calls buckets with their pagination cursor. */
    public suspend fun organizationFileSearchCallsUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions? = null): OrganizationUsagePage =
        throw UnsupportedOperationException("Organization usage is not supported by this client.")

    /** Retrieves images buckets with their pagination cursor. */
    public suspend fun organizationImagesUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions? = null): OrganizationUsagePage =
        throw UnsupportedOperationException("Organization usage is not supported by this client.")

    /** Retrieves moderations buckets with their pagination cursor. */
    public suspend fun organizationModerationsUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions? = null): OrganizationUsagePage =
        throw UnsupportedOperationException("Organization usage is not supported by this client.")

    /** Retrieves vector stores buckets with their pagination cursor. */
    public suspend fun organizationVectorStoresUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions? = null): OrganizationUsagePage =
        throw UnsupportedOperationException("Organization usage is not supported by this client.")

    /** Retrieves web search calls buckets with their pagination cursor. */
    public suspend fun organizationWebSearchCallsUsage(query: OrganizationUsageQuery, requestOptions: RequestOptions? = null): OrganizationUsagePage =
        throw UnsupportedOperationException("Organization usage is not supported by this client.")

}
