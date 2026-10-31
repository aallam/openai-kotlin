# Organization identity and project management

Use the administration client with an **Admin API key**. A normal project inference key or an organization ID in `OpenAIConfig` does not grant administration access.

```kotlin
import com.aallam.openai.api.admin.*
import com.aallam.openai.client.OpenAIAdmin
import com.aallam.openai.client.OpenAIConfig

val admin = OpenAIAdmin(OpenAIConfig(token = adminApiKey))
try {
    val users = admin.organizationUsers(limit = 20)
    users.forEach { println(it.id) }

    val project = admin.createOrganizationProject(OrganizationProjectRequest(name = "Development"))
    val membership = admin.createProjectUser(
        projectId = project.id,
        request = OrganizationProjectUserRequest(role = "member", userId = existingUserId),
    )
} finally {
    admin.close()
}
```

The client covers organization users and invites, groups and memberships, organization/project roles and assignments, projects, project users/groups, service accounts, and administrative/project API keys. Public methods have distinct names rather than adding overloads to existing inference methods. All operations accept `RequestOptions`.

## Pagination

`AdministrationPage<T>` retains `hasMore`, `firstId`, `lastId`, `next` and the object type. User/project/key endpoints use ID cursors; group/role endpoints return `next`. Pass the relevant returned cursor as `after` when `hasMore` is true. Filters such as user emails, archived projects, group category and key-owner project access are exposed by their owning list/retrieve operation.

Retrieval returns null only for HTTP 404. Authentication, permission and other API failures propagate through the SDK's existing exception types. Deletion returns `AdministrationDeletion`, retaining the deletion flag and any returned resource ID/object type.

## Omitted and nullable update fields

Nullable patch fields use `AdministrationValue` to distinguish omission from JSON null:

```kotlin
val request = OrganizationUserUpdateRequest(
    developerPersona = AdministrationValue.Clear,
    technicalLevel = AdministrationValue.Set("experienced"),
)
admin.updateOrganizationUser(userId, request)
```

The default `Unspecified` leaves the field absent. `Set` sends a value; `Clear` sends JSON null where the endpoint allows it. These states are retained when decoding and re-encoding a request. Ordinary optional fields, including key scopes and expiration, are omitted when unset.

## Keys and service accounts

Admin-key creation and service-account key creation return a one-time secret value. Retrieval and listing return redacted key metadata. Store new credentials through your application's secret-management path and avoid logging the creation response.

`OrganizationServiceAccountRequest.createServiceAccountOnly` can create an account without its initial key. `createProjectServiceAccountAPIKey` creates a later key with optional scopes/name/expiration. `updateProjectAPIKey` modifies scopes and/or expiration without returning a secret. An empty scopes list means all permissions subject to the owner's effective access, rather than no access; omitted scopes stay unchanged.

Role-management routes for projects use the documented `/projects/...` namespace, while most project resources use `/organization/projects/...`. The SDK handles this distinction internally.

See the official [Administration reference](https://developers.openai.com/api/reference/administration/overview) for credential requirements and current permission/lifecycle rules.
