package com.aallam.openai.client

import com.aallam.openai.api.admin.*
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder

/**
 * Manages organization and project hard spend limits and notification alerts using an Admin API key.
 * Default operations throw [UnsupportedOperationException] so custom clients can omit this capability.
 */
public interface OrganizationSpending {
    /** Retrieves the organization's hard spend limit and enforcement state. */
    public suspend fun organizationSpendLimit(requestOptions: RequestOptions? = null): SpendLimit =
        throw UnsupportedOperationException("Organization spending is not supported by this client.")

    /** Creates or completely replaces the organization's hard spend limit. Amounts are in cents. */
    public suspend fun updateOrganizationSpendLimit(request: SpendLimitRequest, requestOptions: RequestOptions? = null): SpendLimit =
        throw UnsupportedOperationException("Organization spending is not supported by this client.")

    /** Deletes the organization's hard spend limit and returns the service confirmation. */
    public suspend fun deleteOrganizationSpendLimit(requestOptions: RequestOptions? = null): SpendLimitDeleted =
        throw UnsupportedOperationException("Organization spending is not supported by this client.")

    /** Retrieves a project's hard spend limit and enforcement state. */
    public suspend fun projectSpendLimit(projectId: String, requestOptions: RequestOptions? = null): SpendLimit =
        throw UnsupportedOperationException("Organization spending is not supported by this client.")

    /** Creates or completely replaces a project's hard spend limit. Amounts are in cents. */
    public suspend fun updateProjectSpendLimit(projectId: String, request: SpendLimitRequest, requestOptions: RequestOptions? = null): SpendLimit =
        throw UnsupportedOperationException("Organization spending is not supported by this client.")

    /** Deletes a project's hard spend limit and returns the service confirmation. */
    public suspend fun deleteProjectSpendLimit(projectId: String, requestOptions: RequestOptions? = null): SpendLimitDeleted =
        throw UnsupportedOperationException("Organization spending is not supported by this client.")

    /** Creates an organization-level spend notification alert. */
    public suspend fun createOrganizationSpendAlert(request: SpendAlertRequest, requestOptions: RequestOptions? = null): SpendAlert =
        throw UnsupportedOperationException("Organization spending is not supported by this client.")

    /** Retrieves one organization-level spend alert. */
    public suspend fun organizationSpendAlert(alertId: String, requestOptions: RequestOptions? = null): SpendAlert =
        throw UnsupportedOperationException("Organization spending is not supported by this client.")

    /** Replaces an organization's alert configuration, including its complete notification channel. */
    public suspend fun updateOrganizationSpendAlert(alertId: String, request: SpendAlertRequest, requestOptions: RequestOptions? = null): SpendAlert =
        throw UnsupportedOperationException("Organization spending is not supported by this client.")

    /** Deletes an organization-level alert and returns its ID and service confirmation. */
    public suspend fun deleteOrganizationSpendAlert(alertId: String, requestOptions: RequestOptions? = null): SpendAlertDeleted =
        throw UnsupportedOperationException("Organization spending is not supported by this client.")

    /** Lists organization alerts with forward or backward cursors and optional sort order. */
    public suspend fun organizationSpendAlerts(after: String? = null, before: String? = null, limit: Int? = null, order: SortOrder? = null, requestOptions: RequestOptions? = null): SpendAlertPage =
        throw UnsupportedOperationException("Organization spending is not supported by this client.")

    /** Creates a spend notification alert for a project. */
    public suspend fun createProjectSpendAlert(projectId: String, request: SpendAlertRequest, requestOptions: RequestOptions? = null): SpendAlert =
        throw UnsupportedOperationException("Organization spending is not supported by this client.")

    /** Retrieves one project's spend alert. */
    public suspend fun projectSpendAlert(projectId: String, alertId: String, requestOptions: RequestOptions? = null): SpendAlert =
        throw UnsupportedOperationException("Organization spending is not supported by this client.")

    /** Replaces a project's alert configuration, including its complete notification channel. */
    public suspend fun updateProjectSpendAlert(projectId: String, alertId: String, request: SpendAlertRequest, requestOptions: RequestOptions? = null): SpendAlert =
        throw UnsupportedOperationException("Organization spending is not supported by this client.")

    /** Deletes a project's alert and returns its ID and service confirmation. */
    public suspend fun deleteProjectSpendAlert(projectId: String, alertId: String, requestOptions: RequestOptions? = null): SpendAlertDeleted =
        throw UnsupportedOperationException("Organization spending is not supported by this client.")

    /** Lists project alerts with forward or backward cursors and optional sort order. */
    public suspend fun projectSpendAlerts(projectId: String, after: String? = null, before: String? = null, limit: Int? = null, order: SortOrder? = null, requestOptions: RequestOptions? = null): SpendAlertPage =
        throw UnsupportedOperationException("Organization spending is not supported by this client.")
}
