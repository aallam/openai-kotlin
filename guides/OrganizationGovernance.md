# Organization governance

Use the administrative client with an Admin API credential for organization governance. Organization IDs passed to the ordinary inference client select request context; they do not grant administrative permissions. Keep the admin client separate from inference clients.

`OrganizationGovernance` supplies these resource operations:

| Resource | Operations |
| --- | --- |
| Audit logs | Filtered event listing with actor/project/resource arrays, time ranges and forward/backward cursors |
| Certificates | Upload, retrieve with optional PEM content, update, delete, organization/project listing and scoped activation/deactivation |
| Data retention | Retrieve and update organization/project policies |
| External keys | Register AWS/GCP/Azure configurations, retrieve/list/remove registrations and validate |
| External storage | Register AWS/Azure/GCP/OCI configurations, retrieve/list/delete and validate |
| Model permissions | Retrieve, replace or delete a project allowlist/denylist |
| Hosted-tool permissions | Retrieve and update individual tool enablement states |
| Rate limits | List and update per-model project limits |

```kotlin
import com.aallam.openai.api.admin.governance.*

val logs = admin.auditLogs(AuditLogQuery(
    projectIds = listOf("proj_example"),
    eventTypes = listOf("project.updated"),
    effectiveAt = AuditLogTimeRange(gte = 1700000000),
    limit = 20,
))
for (event in logs) {
    println("${event.type}: ${event.project?.name}")
    val original = event.rawData
    val variantDetails = event.details
}
// Pass logs.lastId as the next query's after cursor when logs.hasMore is true.
```

Audit events expose typed common actor/project fields. `rawData` retains the complete original event, including new event variants and unknown detail fields; `details` reads the payload stored under the event type or the tenant-event `details` field. Use the original snapshot when exporting the complete event.

## Certificates and cloud configuration

Uploading a certificate does not activate it. Activation/deactivation changes a particular organization/project scope and returns an envelope with the resulting certificate states. Certificate deletion returns its ID and object type, not a boolean. External-key removal returns the removed key configuration; external-storage validation returns an updated storage configuration.

```kotlin
val certificate = admin.uploadOrganizationCertificate(CertificateUploadRequest(
    certificate = pemText,
    name = "Client certificate",
))
val result = admin.activateOrganizationCertificates(
    CertificateActivationRequest(listOf(certificate.id)),
)
```

Provider request/result models preserve cloud-specific fields, including exact key versions for eligible PSP-decryption configurations. They configure the API; these SDK methods do not retrieve credentials or change cloud-provider accounts themselves.

## Partial updates

Unset update fields are omitted. A false enablement value is still sent. Hosted-tool updates also support the API's nullable fields explicitly:

```kotlin
admin.updateProjectHostedToolPermissions("proj_example", ProjectHostedToolPermissionsRequest(
    mcp = HostedToolPermission(false),
    nullFields = setOf(HostedTool.WebSearch),
))
```

The example sends `mcp: {enabled: false}` and `web_search: null` while omitting other tools. The API defines the meaning of the explicit null; it is distinct from omission. Supplying both a value and explicit null for the same field is rejected locally.

Resource-ID retrieval methods return null on HTTP 404. Other errors, including permission failures, retain the existing SDK exception behavior. `RequestOptions` supports per-request headers and query parameters. Default methods on `OrganizationGovernance` allow custom clients to adopt capabilities incrementally.

See the official [Administration overview](https://developers.openai.com/api/reference/administration/overview) and [resource reference](https://developers.openai.com/api/reference/overview) for current permissions, availability and limits. Offline fixtures validate this SDK surface without changing organization settings or registering real cloud keys/storage.
