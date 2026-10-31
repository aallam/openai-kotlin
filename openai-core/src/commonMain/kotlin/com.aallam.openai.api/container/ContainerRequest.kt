@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.aallam.openai.api.container

import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.file.FileId
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Creates a hosted container, optionally copying files and mounting skills. */
@Serializable
public data class ContainerRequest(
    /** Name of the container. */
    public val name: String,
    /** Expiration relative to the last activity timestamp. */
    @SerialName("expires_after") public val expiresAfter: ContainerExpirationPolicy? = null,
    /** Regular File IDs to copy into the container. */
    @SerialName("file_ids") public val fileIds: List<FileId>? = null,
    /** Memory size; omitted selects the server default. */
    @SerialName("memory_limit") public val memoryLimit: ContainerMemoryLimit? = null,
    /** Outbound network policy. */
    @SerialName("network_policy") public val networkPolicy: ContainerNetworkPolicy? = null,
    /** Hosted skill references or inline ZIP bundles. */
    public val skills: List<ContainerSkill>? = null,
)

/** Builds a container creation request. */
public fun containerRequest(block: ContainerRequestBuilder.() -> Unit): ContainerRequest =
    ContainerRequestBuilder().apply(block).build()

/** Builder exposing every container creation option. */
@OpenAIDsl
public class ContainerRequestBuilder {
    /** Name of the container. */
    public var name: String? = null
    /** Expiration relative to the last activity timestamp. */
    public var expiresAfter: ContainerExpirationPolicy? = null
    /** Regular File IDs to copy. */
    public var fileIds: List<FileId>? = null
    /** Requested memory size. */
    public var memoryLimit: ContainerMemoryLimit? = null
    /** Requested network policy. */
    public var networkPolicy: ContainerNetworkPolicy? = null
    /** Hosted skill references or inline ZIP bundles. */
    public var skills: List<ContainerSkill>? = null

    /** Requires a name and creates the request. */
    public fun build(): ContainerRequest = ContainerRequest(
        name = requireNotNull(name) { "name is required" },
        expiresAfter = expiresAfter, fileIds = fileIds, memoryLimit = memoryLimit,
        networkPolicy = networkPolicy, skills = skills,
    )
}

/** Requested inactivity expiration policy. */
@Serializable
public data class ContainerExpirationPolicy(
    /** Minutes after the anchor timestamp. */
    public val minutes: Int,
    /** Supported reference timestamp. */
    @EncodeDefault public val anchor: String = "last_active_at",
)

/** Pagination and optional name filter for containers. */
public data class ContainerListQuery(
    /** Continue after this container ID. */
    public val after: ContainerId? = null,
    /** Requested page size, currently one to 100. */
    public val limit: Int? = null,
    /** Filter by container name. */
    public val name: String? = null,
    /** Sort by creation time. */
    public val order: SortOrder? = null,
)

/** Pagination controls for files in a container. */
public data class ContainerFileListQuery(
    /** Continue after this container-file ID. */
    public val after: ContainerFileId? = null,
    /** Requested page size, currently one to 100. */
    public val limit: Int? = null,
    /** Sort by creation time. */
    public val order: SortOrder? = null,
)

/** Outbound access configuration for a hosted container. */
@Serializable
public sealed interface ContainerNetworkPolicy {
    /** Disables outbound network access. */
    @Serializable @SerialName("disabled")
    public data object Disabled : ContainerNetworkPolicy

    /** Allows only the supplied domains and optionally injects domain-scoped secrets. */
    @Serializable @SerialName("allowlist")
    public data class Allowlist(
        /** Allowed outbound domains. */
        @SerialName("allowed_domains") public val allowedDomains: List<String>,
        /** Secrets scoped to permitted domains. */
        @SerialName("domain_secrets") public val domainSecrets: List<ContainerDomainSecret>? = null,
    ) : ContainerNetworkPolicy
}

/** A domain-scoped secret whose value is redacted from diagnostic strings. */
@Serializable
public data class ContainerDomainSecret(
    /** Domain associated with the secret. */
    public val domain: String,
    /** Name of the secret to inject. */
    public val name: String,
    /** Secret value. */
    public val value: String,
) {
    override fun toString(): String = "ContainerDomainSecret(domain=$domain, name=$name, value=[REDACTED])"
}

/** Skill mounted into a hosted container. */
@Serializable
public sealed interface ContainerSkill {
    /** A skill managed through the Skills API. */
    @Serializable @SerialName("skill_reference")
    public data class Reference(
        /** Hosted or curated skill ID. */
        @SerialName("skill_id") public val skillId: String,
        /** Positive integer version or `latest`; omitted selects the default version. */
        public val version: String? = null,
    ) : ContainerSkill

    /** A ZIP bundle supplied directly in the container request. */
    @Serializable @SerialName("inline")
    public data class Inline(
        /** Skill name. */
        public val name: String,
        /** Skill description. */
        public val description: String,
        /** Base64-encoded ZIP payload. */
        public val source: ContainerInlineSkillSource,
    ) : ContainerSkill
}

/** Inline base64 ZIP skill source. */
@Serializable
public data class ContainerInlineSkillSource(
    /** Base64-encoded ZIP data. */
    public val data: String,
    /** The supported payload media type. */
    @SerialName("media_type") @EncodeDefault public val mediaType: String = "application/zip",
    /** The supported source encoding. */
    @EncodeDefault public val type: String = "base64",
)
