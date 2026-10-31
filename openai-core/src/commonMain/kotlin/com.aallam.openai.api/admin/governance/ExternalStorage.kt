package com.aallam.openai.api.admin.governance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Registers a customer-managed storage configuration for a project. */
@Serializable
public data class ExternalStorageRequest(
    /** Owning project. */
    @SerialName("project_id") public val projectId: String,
    /** Provider-specific connection configuration. */
    public val provider: ExternalStorageProviderRequest,
)

/** Provider-specific registration parameters. */
@Serializable
public sealed interface ExternalStorageProviderRequest {
    /** Aws storage configuration. */
    @Serializable @SerialName("aws")
    public data class Aws(
        /** Bucket name. */
        public val bucket: String,
        /** AWS role ARN used to access the bucket. */
        @SerialName("role_arn") public val roleArn: String,
    ) : ExternalStorageProviderRequest

    /** Azure storage configuration. */
    @Serializable @SerialName("azure")
    public data class Azure(
        /** Azure storage account. */
        @SerialName("account_name") public val accountName: String,
        /** Blob container. */
        public val container: String,
        /** Azure resource group. */
        @SerialName("resource_group") public val resourceGroup: String,
        /** Azure subscription. */
        @SerialName("subscription_id") public val subscriptionId: String,
        /** Azure tenant. */
        @SerialName("tenant_id") public val tenantId: String,
    ) : ExternalStorageProviderRequest

    /** Gcp storage configuration. */
    @Serializable @SerialName("gcp")
    public data class Gcp(
        /** Bucket name. */
        public val bucket: String,
        /** Workload identity pool. */
        @SerialName("workload_identity_pool_id") public val workloadIdentityPoolId: String,
        /** Workload identity project number. */
        @SerialName("workload_identity_project_number") public val workloadIdentityProjectNumber: String,
        /** Workload identity provider. */
        @SerialName("workload_identity_provider_id") public val workloadIdentityProviderId: String,
    ) : ExternalStorageProviderRequest

    /** Oci storage configuration. */
    @Serializable @SerialName("oci")
    public data class Oci(
        /** Bucket name. */
        public val bucket: String,
        /** Object-storage region. */
        public val region: String,
        /** OCI tenancy OCID. */
        @SerialName("tenancy_ocid") public val tenancyOcid: String,
    ) : ExternalStorageProviderRequest
}

/** Registered external storage with validation state and provider metadata. */
@Serializable
public data class ExternalStorageConfiguration(
    /** Configuration identifier. */
    public val id: String,
    /** Creation time in Unix seconds. */
    @SerialName("created_at") public val createdAt: Long,
    /** Storage geography. */
    public val geography: String,
    /** Owning project. */
    @SerialName("project_id") public val projectId: String,
    /** Resolved provider configuration. */
    public val provider: ExternalStorageProvider,
    /** Validation state: pending, validated, degraded or unhealthy. */
    public val status: String,
    /** Response discriminator. */
    @SerialName("object") public val objectType: String = "organization.external_storage",
)

/** Resolved provider metadata returned by the API. */
@Serializable
public sealed interface ExternalStorageProvider {
    /** Aws storage configuration. */
    @Serializable @SerialName("aws")
    public data class Aws(
        /** AWS account identifier. */
        @SerialName("account_id") public val accountId: String,
        /** Bucket name. */
        public val bucket: String,
        /** AssumeRole external ID. */
        @SerialName("external_id") public val externalId: String,
        /** Bucket region. */
        public val region: String,
        /** AWS role ARN. */
        @SerialName("role_arn") public val roleArn: String,
    ) : ExternalStorageProvider

    /** Azure storage configuration. */
    @Serializable @SerialName("azure")
    public data class Azure(
        /** Azure storage account. */
        @SerialName("account_name") public val accountName: String,
        /** Blob container. */
        public val container: String,
        /** Storage region. */
        public val region: String,
        /** Azure resource group. */
        @SerialName("resource_group") public val resourceGroup: String,
        /** Azure subscription. */
        @SerialName("subscription_id") public val subscriptionId: String,
        /** Azure tenant. */
        @SerialName("tenant_id") public val tenantId: String,
    ) : ExternalStorageProvider

    /** Gcp storage configuration. */
    @Serializable @SerialName("gcp")
    public data class Gcp(
        /** Identity-token audience. */
        public val audience: String,
        /** Bucket name. */
        public val bucket: String,
        /** Bucket region. */
        public val region: String,
        /** Workload identity pool. */
        @SerialName("workload_identity_pool_id") public val workloadIdentityPoolId: String,
        /** Workload identity project number. */
        @SerialName("workload_identity_project_number") public val workloadIdentityProjectNumber: String,
        /** Workload identity provider. */
        @SerialName("workload_identity_provider_id") public val workloadIdentityProviderId: String,
    ) : ExternalStorageProvider

    /** Oci storage configuration. */
    @Serializable @SerialName("oci")
    public data class Oci(
        /** Bucket name. */
        public val bucket: String,
        /** Object-storage region. */
        public val region: String,
        /** OCI tenancy OCID. */
        @SerialName("tenancy_ocid") public val tenancyOcid: String,
    ) : ExternalStorageProvider
}

/** External-storage deletion confirmation. */
@Serializable
public data class ExternalStorageDeletion(
    /** Deleted configuration identifier. */
    public val id: String,
    /** Whether the configuration was deleted. */
    public val deleted: Boolean,
    /** Response discriminator. */
    @SerialName("object") public val objectType: String = "organization.external_storage.deleted",
)
