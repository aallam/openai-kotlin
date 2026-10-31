package com.aallam.openai.client

import com.aallam.openai.api.admin.governance.*
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.RequestOptions

/** Organization governance operations requiring an Admin API credential. Default methods support custom clients. */
public interface OrganizationGovernance {
    /** Lists audit events while preserving evolving event details. */
    public suspend fun auditLogs(
        query: AuditLogQuery = AuditLogQuery(),
        requestOptions: RequestOptions? = null,
    ): PaginatedList<AuditLog> = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Uploads PEM content without activating it. */
    public suspend fun uploadOrganizationCertificate(
        request: CertificateUploadRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationCertificate = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Retrieves a certificate, optionally including content; returns null on HTTP 404. */
    public suspend fun organizationCertificate(
        id: String,
        include: List<String>? = null,
        requestOptions: RequestOptions? = null,
    ): OrganizationCertificate? = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Updates the supplied certificate name. */
    public suspend fun updateOrganizationCertificate(
        id: String,
        request: CertificateUpdateRequest,
        requestOptions: RequestOptions? = null,
    ): OrganizationCertificate = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Deletes an inactive certificate and preserves its deletion envelope. */
    public suspend fun deleteOrganizationCertificate(
        id: String,
        requestOptions: RequestOptions? = null,
    ): CertificateDeletion = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Lists organization certificates and their scoped activation states. */
    public suspend fun organizationCertificates(
        query: CertificateListQuery = CertificateListQuery(),
        requestOptions: RequestOptions? = null,
    ): PaginatedList<OrganizationCertificate> = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Atomically activates one to ten organization certificates. */
    public suspend fun activateOrganizationCertificates(
        request: CertificateActivationRequest,
        requestOptions: RequestOptions? = null,
    ): CertificateActivationResult = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Atomically deactivates one to ten organization certificates. */
    public suspend fun deactivateOrganizationCertificates(
        request: CertificateActivationRequest,
        requestOptions: RequestOptions? = null,
    ): CertificateActivationResult = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Lists certificates and activation states for a project. */
    public suspend fun projectCertificates(
        projectId: String,
        query: CertificateListQuery = CertificateListQuery(),
        requestOptions: RequestOptions? = null,
    ): PaginatedList<OrganizationCertificate> = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Atomically activates one to ten certificates for a project. */
    public suspend fun activateProjectCertificates(
        projectId: String,
        request: CertificateActivationRequest,
        requestOptions: RequestOptions? = null,
    ): CertificateActivationResult = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Atomically deactivates one to ten certificates for a project. */
    public suspend fun deactivateProjectCertificates(
        projectId: String,
        request: CertificateActivationRequest,
        requestOptions: RequestOptions? = null,
    ): CertificateActivationResult = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Retrieves organization data-retention controls. */
    public suspend fun organizationDataRetention(
        requestOptions: RequestOptions? = null,
    ): DataRetentionSetting = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Updates organization data-retention controls. */
    public suspend fun updateOrganizationDataRetention(
        request: OrganizationRetentionRequest,
        requestOptions: RequestOptions? = null,
    ): DataRetentionSetting = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Retrieves project data-retention controls. */
    public suspend fun projectDataRetention(
        projectId: String,
        requestOptions: RequestOptions? = null,
    ): DataRetentionSetting = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Updates project data-retention controls. */
    public suspend fun updateProjectDataRetention(
        projectId: String,
        request: ProjectRetentionRequest,
        requestOptions: RequestOptions? = null,
    ): DataRetentionSetting = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Registers customer-managed key configuration for the organization. */
    public suspend fun registerExternalKey(
        request: ExternalKeyRequest,
        requestOptions: RequestOptions? = null,
    ): ExternalKey = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Retrieves an external-key registration; returns null on HTTP 404. */
    public suspend fun externalKey(
        id: String,
        requestOptions: RequestOptions? = null,
    ): ExternalKey? = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Lists external-key registrations and preserves cursor metadata. */
    public suspend fun externalKeys(
        query: ExternalKeyListQuery = ExternalKeyListQuery(),
        requestOptions: RequestOptions? = null,
    ): PaginatedList<ExternalKey> = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Removes a registration and returns the removed configuration, not a deletion boolean. */
    public suspend fun deleteExternalKey(
        id: String,
        requestOptions: RequestOptions? = null,
    ): ExternalKey = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Validates a registered external-key configuration. */
    public suspend fun validateExternalKey(
        id: String,
        requestOptions: RequestOptions? = null,
    ): ExternalKeyValidation = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Registers customer-managed storage for a project. */
    public suspend fun registerExternalStorage(
        request: ExternalStorageRequest,
        requestOptions: RequestOptions? = null,
    ): ExternalStorageConfiguration = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Retrieves external-storage registration; returns null on HTTP 404. */
    public suspend fun externalStorage(
        id: String,
        requestOptions: RequestOptions? = null,
    ): ExternalStorageConfiguration? = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Lists storage registrations with project filtering and cursor metadata. */
    public suspend fun externalStorageConfigurations(
        query: ExternalStorageListQuery = ExternalStorageListQuery(),
        requestOptions: RequestOptions? = null,
    ): PaginatedList<ExternalStorageConfiguration> = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Removes an external-storage registration. */
    public suspend fun deleteExternalStorage(
        id: String,
        requestOptions: RequestOptions? = null,
    ): ExternalStorageDeletion = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Validates an external-storage registration and returns its updated state. */
    public suspend fun validateExternalStorage(
        id: String,
        requestOptions: RequestOptions? = null,
    ): ExternalStorageConfiguration = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Retrieves an explicit project model policy; returns null on HTTP 404. */
    public suspend fun projectModelPermissions(
        projectId: String,
        requestOptions: RequestOptions? = null,
    ): ProjectModelPermissions? = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Replaces a project model allowlist or denylist. */
    public suspend fun updateProjectModelPermissions(
        projectId: String,
        request: ProjectModelPermissionsRequest,
        requestOptions: RequestOptions? = null,
    ): ProjectModelPermissions = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Deletes a project model permission override. */
    public suspend fun deleteProjectModelPermissions(
        projectId: String,
        requestOptions: RequestOptions? = null,
    ): ProjectModelPermissionsDeletion = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Retrieves project hosted-tool enablement states. */
    public suspend fun projectHostedToolPermissions(
        projectId: String,
        requestOptions: RequestOptions? = null,
    ): ProjectHostedToolPermissions = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Updates only supplied tool permissions, preserving explicit JSON null selections. */
    public suspend fun updateProjectHostedToolPermissions(
        projectId: String,
        request: ProjectHostedToolPermissionsRequest,
        requestOptions: RequestOptions? = null,
    ): ProjectHostedToolPermissions = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Lists per-model rate limits with forward or backward cursor metadata. */
    public suspend fun projectRateLimits(
        projectId: String,
        query: ProjectRateLimitQuery = ProjectRateLimitQuery(),
        requestOptions: RequestOptions? = null,
    ): PaginatedList<ProjectRateLimit> = throw UnsupportedOperationException("Organization governance is not supported by this client.")

    /** Updates only supplied fields of a project rate limit. */
    public suspend fun updateProjectRateLimit(
        projectId: String,
        id: String,
        request: ProjectRateLimitRequest,
        requestOptions: RequestOptions? = null,
    ): ProjectRateLimit = throw UnsupportedOperationException("Organization governance is not supported by this client.")

}
