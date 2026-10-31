package com.aallam.openai.api.container

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/** Identifier of a hosted container. */
@Serializable @JvmInline
public value class ContainerId(public val id: String)

/** Identifier of a file inside a hosted container. */
@Serializable @JvmInline
public value class ContainerFileId(public val id: String)

/** A hosted container and its configured resource policy. */
@Serializable
public data class Container(
    /** Container identifier. */
    public val id: ContainerId,
    /** Creation time in Unix seconds. */
    @SerialName("created_at") public val createdAt: Long,
    /** Caller-provided name. */
    public val name: String,
    /** Lifecycle state, such as `running` or `deleted`. */
    public val status: String,
    /** Resource kind. */
    @SerialName("object") public val objectType: String = "container",
    /** Returned expiration policy; either nested field may be omitted. */
    @SerialName("expires_after") public val expiresAfter: ContainerExpiration? = null,
    /** Last activity time in Unix seconds. */
    @SerialName("last_active_at") public val lastActiveAt: Long? = null,
    /** Configured memory limit. */
    @SerialName("memory_limit") public val memoryLimit: ContainerMemoryLimit? = null,
    /** Returned network policy, which does not expose domain secrets. */
    @SerialName("network_policy") public val networkPolicy: ContainerNetworkAccess? = null,
)

/** Expiration information returned for a container. */
@Serializable
public data class ContainerExpiration(
    /** Reference timestamp, currently `last_active_at`. */
    public val anchor: String? = null,
    /** Number of minutes after the reference timestamp. */
    public val minutes: Int? = null,
)

/** Network configuration returned by the API, without request-only secret values. */
@Serializable
public data class ContainerNetworkAccess(
    /** `disabled` or `allowlist`. */
    public val type: String,
    /** Domains permitted for an allowlist policy. */
    @SerialName("allowed_domains") public val allowedDomains: List<String>? = null,
)

/** File metadata inside a hosted container. */
@Serializable
public data class ContainerFile(
    /** Container-file identifier, distinct from a regular File ID. */
    public val id: ContainerFileId,
    /** File length in bytes. */
    public val bytes: Long,
    /** Owning container. */
    @SerialName("container_id") public val containerId: ContainerId,
    /** Creation time in Unix seconds. */
    @SerialName("created_at") public val createdAt: Long,
    /** Absolute path inside the container. */
    public val path: String,
    /** Origin of the file, such as `user` or `assistant`. */
    public val source: String,
    /** Resource kind. */
    @SerialName("object") public val objectType: String = "container.file",
)

/** String-backed container memory size, preserving future server-supported sizes. */
@Serializable @JvmInline
public value class ContainerMemoryLimit(public val value: String) {
    public companion object {
        /** One gigabyte, the server default. */
        public val G1: ContainerMemoryLimit = ContainerMemoryLimit("1g")
        /** Four gigabytes. */
        public val G4: ContainerMemoryLimit = ContainerMemoryLimit("4g")
        /** Sixteen gigabytes. */
        public val G16: ContainerMemoryLimit = ContainerMemoryLimit("16g")
        /** Sixty-four gigabytes. */
        public val G64: ContainerMemoryLimit = ContainerMemoryLimit("64g")
    }
}
