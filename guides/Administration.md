# Administration

Create a separate client with an Admin API key. Ordinary project API keys do not grant organization administration access.

```kotlin
import com.aallam.openai.api.admin.usage.OrganizationUsageQuery
import com.aallam.openai.api.admin.usage.OrganizationUsageResult
import com.aallam.openai.client.OpenAIAdmin

val admin = OpenAIAdmin(adminApiKey)
try {
    val page = admin.organizationCosts(OrganizationUsageQuery(
        startTime = startUnixSeconds,
        endTime = endUnixSeconds,
        groupBy = listOf("project_id", "line_item"),
        projectIds = listOf(projectId),
    ))
    val costs = page.data.flatMap { it.results }.filterIsInstance<OrganizationUsageResult.Costs>()
    println(costs)
    // Request the next page using page.nextPage when page.hasMore is true.
} finally {
    admin.close()
}
```

`OpenAIAdmin(OpenAIConfig(...))` accepts the same engine, host, timeout, headers and logging configuration as the application client. Credentials are supplied explicitly; the SDK does not read environment variables or switch application credentials automatically.

## Usage and costs

The reporting client covers completions, Decisions, embeddings, moderations, images, audio speech/transcription, vector stores, code-interpreter sessions, file search, web search and costs. Results retain time buckets, typed metrics, grouping dimensions and the opaque `nextPage` cursor. Aggregated counts use `Long`.

Query times are Unix seconds with an inclusive start and exclusive end. `OrganizationUsageQuery` exposes bucket width, page/limit, grouping and each documented resource filter. Resource-specific filters are checked against the selected endpoint: image sizes/sources belong to image usage; vector-store IDs belong to file search; context levels belong to web search; exact line-item names belong to costs. Unsupported filters are rejected rather than silently dropped. Array query values are sent using bracketed keys.

Costs use the currency and amount reported by the API. Usage counters are measurements, not a conversion to a bill. Admin credential/permission errors propagate using the existing exception types. No administrative changes are performed by reading reports.

See the official [Administration reference](https://developers.openai.com/api/reference/administration/overview) and [Costs reference](https://developers.openai.com/api/reference/resources/admin/subresources/organization/subresources/usage/methods/costs).
