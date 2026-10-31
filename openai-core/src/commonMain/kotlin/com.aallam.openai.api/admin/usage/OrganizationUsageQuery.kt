package com.aallam.openai.api.admin.usage

/** Filters for organization reporting. Unsupported endpoint-specific filters are rejected before sending. */
public data class OrganizationUsageQuery(
    /** Inclusive query start in Unix seconds. */
    public val startTime: Long,
    /** Exclusive query end in Unix seconds. */
    public val endTime: Long? = null,
    /** Time bucket width: 1m, 1h or 1d; costs supports only 1d. */
    public val bucketWidth: String? = null,
    /** Maximum number of buckets. */
    public val limit: Int? = null,
    /** Opaque cursor from the previous nextPage. */
    public val page: String? = null,
    /** Restrict results to these projects. */
    public val projectIds: List<String>? = null,
    /** Restrict results to these API keys, where supported. */
    public val apiKeyIds: List<String>? = null,
    /** Restrict results to these users, where supported. */
    public val userIds: List<String>? = null,
    /** Restrict model-based usage to these models. */
    public val models: List<String>? = null,
    /** Grouping dimensions accepted by the selected endpoint. */
    public val groupBy: List<String>? = null,
    /** Batch filter for completions and Decisions. */
    public val batch: Boolean? = null,
    /** Image sizes for image usage. */
    public val sizes: List<String>? = null,
    /** Generation/edit/variation sources for image usage. */
    public val sources: List<String>? = null,
    /** Vector store IDs for file search usage. */
    public val vectorStoreIds: List<String>? = null,
    /** Context levels for web search usage. */
    public val contextLevels: List<String>? = null,
    /** Exact line item names for cost queries. */
    public val lineItems: List<String>? = null,
)
