package com.aallam.openai.api.skill

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/** Identifier of a hosted skill. */
@Serializable @JvmInline
public value class SkillId(public val id: String)

/** Identifier of a skill-version resource, used as the versions-list cursor. */
@Serializable @JvmInline
public value class SkillVersionId(public val id: String)

/** Hosted skill metadata; versions are selected using their version strings. */
@Serializable
public data class Skill(
    /** Hosted skill identifier. */
    public val id: SkillId,
    /** Creation time in Unix seconds. */
    @SerialName("created_at") public val createdAt: Long,
    /** Version used when a consumer omits an explicit version. */
    @SerialName("default_version") public val defaultVersion: String,
    /** Description read from the skill manifest. */
    public val description: String,
    /** Most recently uploaded version. */
    @SerialName("latest_version") public val latestVersion: String,
    /** Name read from the skill manifest. */
    public val name: String,
    /** Resource kind. */
    @SerialName("object") public val objectType: String = "skill",
)

/** Immutable skill version metadata. */
@Serializable
public data class SkillVersion(
    /** Version-resource identifier, distinct from the version number. */
    public val id: SkillVersionId,
    /** Creation time in Unix seconds. */
    @SerialName("created_at") public val createdAt: Long,
    /** Description of this immutable version. */
    public val description: String,
    /** Name of this immutable version. */
    public val name: String,
    /** Owning skill. */
    @SerialName("skill_id") public val skillId: SkillId,
    /** Version string accepted by version operations and hosted-tool references. */
    public val version: String,
    /** Resource kind. */
    @SerialName("object") public val objectType: String = "skill.version",
)
