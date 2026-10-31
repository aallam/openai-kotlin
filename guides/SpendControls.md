# Organization and project spending

Spend controls use an Admin API key through `OpenAIAdmin`. A hard spend limit is distinct from a notification alert: limits affect request enforcement, while alerts configure email notifications. Amounts are integer cents, not dollars.

## Hard spend limits

```kotlin
import com.aallam.openai.api.admin.*
import com.aallam.openai.client.OpenAIAdmin

val admin = OpenAIAdmin(adminKey)
val existing = admin.organizationSpendLimit()
println(existing.enforcement.status)

val limit = spendLimitRequest {
    thresholdAmount = 100_000L // USD 1,000.00
    currency = "USD"
    interval = "month"
}
val organization = admin.updateOrganizationSpendLimit(limit)
val project = admin.updateProjectSpendLimit("proj_123", limit)
```

Updates create or completely replace the selected hard spend limit using POST. Currency and interval are sent with every request; currently the service supports `USD` and `month`. A hard limit must be at least one cent. Response models retain the current enforcement status without assuming a closed set of status strings.

`projectSpendLimit(projectId)` retrieves a project's limit. `deleteOrganizationSpendLimit()` and `deleteProjectSpendLimit(projectId)` return `SpendLimitDeleted`, preserving the scoped discriminator and `deleted` flag. Limit deletion responses do not contain a resource ID.

## Spend alerts

```kotlin
val request = spendAlertRequest {
    thresholdAmount = 75_000L
    notificationChannel = SpendAlertEmailChannel(
        recipients = listOf("finance@example.com"),
        subjectPrefix = "API spending",
    )
}
val alert = admin.createOrganizationSpendAlert(request)
val updated = admin.updateOrganizationSpendAlert(alert.id, request.copy(thresholdAmount = 90_000L))
val page = admin.organizationSpendAlerts(limit = 20)
val next = if (page.hasMore) admin.organizationSpendAlerts(after = page.lastId, limit = 20) else null
```

Alert creation and updates require the full threshold/currency/interval/notification configuration. Alert thresholds may be zero. The typed notification channel retains recipients, its type and an optional subject prefix. Updates are complete POST replacements, rather than partial patches.

Project equivalents are `createProjectSpendAlert`, `projectSpendAlert`, `updateProjectSpendAlert`, `projectSpendAlerts` and `deleteProjectSpendAlert`; each additionally takes the project ID. Organization equivalents omit that ID. List operations support `after`, `before`, `limit` and `order`, and `SpendAlertPage` retains `hasMore`, `firstId`, `lastId` and the list discriminator. Alert deletion returns `SpendAlertDeleted` with its ID and service confirmation.

All operations accept `RequestOptions`, and non-success API responses keep their normal exception types. The SDK does not turn permission or missing-resource failures into successful deletions. No live settings changes or notification emails are required by the offline tests.

See the official [organization spend-limit reference](https://developers.openai.com/api/reference/resources/admin/subresources/organization/subresources/spend_limit/methods/update) and [spend-alert reference](https://developers.openai.com/api/reference/resources/admin/subresources/organization/subresources/spend_alerts/methods/create).
