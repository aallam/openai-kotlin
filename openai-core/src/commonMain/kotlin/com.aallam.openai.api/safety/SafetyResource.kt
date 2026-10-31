package com.aallam.openai.api.safety

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A project alert about potentially unintended data access, transfer or destructive activity. */
@Serializable
public data class SafetyAlert(
    /** Alert identifier. */
    public val id: String,
    /** Creation time in Unix seconds. */
    @SerialName("created_at") public val createdAt: Long,
    /** Category such as potentially_unintended_data_access or other. */
    @SerialName("error_type") public val errorType: String,
    /** Model associated with the alert. */
    public val model: String,
    /** Request identifier. */
    @SerialName("request_id") public val requestId: String,
    /** Successful block registration; this does not confirm response execution stopped. */
    @SerialName("request_paused") public val requestPaused: Boolean,
    /** Response identifier. */
    @SerialName("response_id") public val responseId: String,
    /** Customer-safe reason, or null for zero data retention requests. */
    public val reason: String? = null,
    /** Explanation available temporarily for eligible alerts. */
    @SerialName("detailed_explanation") public val detailedExplanation: String? = null,
    /** Resource discriminator. */
    @SerialName("object") public val objectType: String = "safety.alert",
)

/** A safety case associated with an entity identifier. */
@Serializable
public data class SafetyCase(
    /** Case identifier. */
    public val id: String,
    /** Creation time in Unix seconds. */
    @SerialName("created_at") public val createdAt: Long,
    /** Affected entity identifier. */
    @SerialName("entity_identifier") public val entityIdentifier: String,
    /** Warning or deactivation notice. */
    public val notice: SafetyNotice,
    /** Case reason, when available. */
    public val reason: String? = null,
    /** Resource discriminator. */
    @SerialName("object") public val objectType: String = "safety.case",
)

/** Safety notice category. */
@Serializable
public data class SafetyNotice(
    /** Notice type, currently warning or deactivation. */
    public val type: String,
)
