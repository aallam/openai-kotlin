package com.aallam.openai.api.admin.governance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Provider-specific customer-managed key registration. */
@Serializable
public sealed interface ExternalKeyRequest {
    /** AWS KMS configuration. */
    @Serializable @SerialName("aws")
    public data class Aws(
        /** Organization or project ID supplied as the AssumeRole external ID. */
        @SerialName("external_id") public val externalId: String,
        /** Master-key ARN. */
        @SerialName("kms_arn") public val kmsArn: String,
        /** Descriptive registration name. */
        public val name: String,
        /** IAM role OpenAI assumes to access KMS. */
        @SerialName("role_arn") public val roleArn: String,
        /** `ekm` or `psp_decryption`; omitted uses the server default. */
        public val purpose: String? = null,
    ) : ExternalKeyRequest

    /** Google Cloud KMS configuration. */
    @Serializable @SerialName("gcp")
    public data class Gcp(
        /** Organization/project audience in the identity token. */
        public val audience: String,
        /** KMS key region. */
        @SerialName("kms_key_location") public val kmsKeyLocation: String,
        /** KMS master-key name. */
        @SerialName("kms_key_name") public val kmsKeyName: String,
        /** KMS key-ring name. */
        @SerialName("kms_key_ring_name") public val kmsKeyRingName: String,
        /** Project containing KMS. */
        @SerialName("kms_project_id") public val kmsProjectId: String,
        /** Descriptive registration name. */
        public val name: String,
        /** Workload identity pool registered for OpenAI. */
        @SerialName("workload_identity_pool_id") public val workloadIdentityPoolId: String,
        /** Workload identity project number. */
        @SerialName("workload_identity_project_number") public val workloadIdentityProjectNumber: String,
        /** Workload identity provider registered for OpenAI. */
        @SerialName("workload_identity_provider_id") public val workloadIdentityProviderId: String,
        /** Exact key version, required for PSP decryption and not accepted for EKM. */
        @SerialName("kms_key_version") public val kmsKeyVersion: String? = null,
        /** `ekm` or `psp_decryption`. */
        public val purpose: String? = null,
    ) : ExternalKeyRequest

    /** Azure Key Vault configuration. */
    @Serializable @SerialName("azure")
    public data class Azure(
        /** Key Vault master-key name. */
        @SerialName("key_name") public val keyName: String,
        /** Descriptive registration name. */
        public val name: String,
        /** Azure tenant UUID. */
        @SerialName("tenant_id") public val tenantId: String,
        /** HTTPS URI of the Key Vault. */
        @SerialName("vault_uri") public val vaultUri: String,
        /** Exact version, required for PSP decryption and not accepted for EKM. */
        @SerialName("key_version") public val keyVersion: String? = null,
        /** `ekm` or `psp_decryption`. */
        public val purpose: String? = null,
    ) : ExternalKeyRequest
}

/** Registered customer-managed key configuration and provider metadata. */
@Serializable
public data class ExternalKey(
    /** Registration identifier. */
    public val id: String,
    /** Projects associated with this key. */
    @SerialName("api_project_ids") public val apiProjectIds: List<String>,
    /** Creation time in Unix seconds. */
    @SerialName("created_at") public val createdAt: Long,
    /** Descriptive registration name. */
    public val name: String,
    /** Response discriminator. */
    @SerialName("object") public val objectType: String,
    /** Cloud provider identifier. */
    public val type: String,
    /** Owning workspace identifier. */
    @SerialName("workspace_id") public val workspaceId: String,
    /** Google identity-token audience. */
    public val audience: String? = null,
    /** AWS AssumeRole external ID. */
    @SerialName("external_id") public val externalId: String? = null,
    /** Azure key name. */
    @SerialName("key_name") public val keyName: String? = null,
    /** Azure exact key version. */
    @SerialName("key_version") public val keyVersion: String? = null,
    /** AWS KMS master-key ARN. */
    @SerialName("kms_arn") public val kmsArn: String? = null,
    /** Google KMS key region. */
    @SerialName("kms_key_location") public val kmsKeyLocation: String? = null,
    /** Google KMS key name. */
    @SerialName("kms_key_name") public val kmsKeyName: String? = null,
    /** Google KMS key-ring name. */
    @SerialName("kms_key_ring_name") public val kmsKeyRingName: String? = null,
    /** Google exact key version. */
    @SerialName("kms_key_version") public val kmsKeyVersion: String? = null,
    /** Google KMS project. */
    @SerialName("kms_project_id") public val kmsProjectId: String? = null,
    /** Registered purpose. */
    public val purpose: String? = null,
    /** AWS role used to access KMS. */
    @SerialName("role_arn") public val roleArn: String? = null,
    /** Azure tenant UUID. */
    @SerialName("tenant_id") public val tenantId: String? = null,
    /** Azure Key Vault URI. */
    @SerialName("vault_uri") public val vaultUri: String? = null,
    /** Google workload identity pool. */
    @SerialName("workload_identity_pool_id") public val workloadIdentityPoolId: String? = null,
    /** Google workload identity project number. */
    @SerialName("workload_identity_project_number") public val workloadIdentityProjectNumber: String? = null,
    /** Google workload identity provider. */
    @SerialName("workload_identity_provider_id") public val workloadIdentityProviderId: String? = null,
)

/** Validation status returned by an external-key validation request. */
@Serializable
public data class ExternalKeyValidation(
    /** Provider/configuration validation status. */
    public val status: String,
)
