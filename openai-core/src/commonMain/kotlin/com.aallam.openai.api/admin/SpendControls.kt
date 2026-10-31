@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.aallam.openai.api.admin

import com.aallam.openai.api.OpenAIDsl
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Creates or replaces an organization or project hard spend limit. */
@Serializable
public data class SpendLimitRequest(
    /** Positive hard limit amount, in cents. */
    @SerialName("threshold_amount") public val thresholdAmount: Long,
    /** Threshold currency; currently only `USD` is supported. */
    @EncodeDefault public val currency: String = "USD",
    /** Evaluation interval; currently only `month` is supported. */
    @EncodeDefault public val interval: String = "month",
) {
    init { require(thresholdAmount > 0) { "A hard spend limit must be at least one cent" } }
}

/** A hard spend limit and its current enforcement state. */
@Serializable
public data class SpendLimit(
    /** Hard limit amount, in cents. */
    @SerialName("threshold_amount") public val thresholdAmount: Long,
    /** Threshold currency. */
    public val currency: String,
    /** Evaluation interval. */
    public val interval: String,
    /** Whether this hard spend limit currently enforces requests. */
    public val enforcement: SpendLimitEnforcement,
    /** Scope discriminator: `organization.spend_limit` or `project.spend_limit`. */
    @SerialName("object") public val objectType: String,
)

/** Enforcement state returned by the service, such as `inactive` or `enforcing`. */
@Serializable
public data class SpendLimitEnforcement(
    /** Current enforcement status; preserved as returned by the service. */
    public val status: String,
)

/** Confirmation that an organization or project hard spend limit was deleted. */
@Serializable
public data class SpendLimitDeleted(
    /** Whether the hard spend limit was deleted. */
    public val deleted: Boolean,
    /** Scoped deletion discriminator; the response does not contain a resource ID. */
    @SerialName("object") public val objectType: String,
)

/** Email notification settings for a spend alert. */
@Serializable
public data class SpendAlertEmailChannel(
    /** Addresses that receive this alert's notifications. */
    public val recipients: List<String>,
    /** Optional prefix for alert email subjects. */
    @SerialName("subject_prefix") public val subjectPrefix: String? = null,
    /** Channel discriminator; the current service supports `email`. */
    @EncodeDefault public val type: String = "email",
)

/** Creates or replaces an organization or project spend alert. */
@Serializable
public data class SpendAlertRequest(
    /** Nonnegative alert threshold, in cents. */
    @SerialName("threshold_amount") public val thresholdAmount: Long,
    /** Complete email notification configuration for this alert. */
    @SerialName("notification_channel") public val notificationChannel: SpendAlertEmailChannel,
    /** Threshold currency; currently only `USD` is supported. */
    @EncodeDefault public val currency: String = "USD",
    /** Evaluation interval; currently only `month` is supported. */
    @EncodeDefault public val interval: String = "month",
) {
    init { require(thresholdAmount >= 0) { "A spend alert threshold cannot be negative" } }
}

/** Organization or project spend alert metadata. */
@Serializable
public data class SpendAlert(
    /** Alert identifier. */
    public val id: String,
    /** Alert threshold, in cents. */
    @SerialName("threshold_amount") public val thresholdAmount: Long,
    /** Threshold currency. */
    public val currency: String,
    /** Evaluation interval. */
    public val interval: String,
    /** Email notification configuration, including any subject prefix. */
    @SerialName("notification_channel") public val notificationChannel: SpendAlertEmailChannel,
    /** Scope discriminator: `organization.spend_alert` or `project.spend_alert`. */
    @SerialName("object") public val objectType: String,
)

/** A complete cursor page of organization or project spend alerts. */
@Serializable
public data class SpendAlertPage(
    /** Alerts in this page. */
    public val data: List<SpendAlert>,
    /** Whether further alerts are available. */
    @SerialName("has_more") public val hasMore: Boolean,
    /** First alert ID; use as a `before` cursor for a previous page. */
    @SerialName("first_id") public val firstId: String? = null,
    /** Last alert ID; use as an `after` cursor for a subsequent page. */
    @SerialName("last_id") public val lastId: String? = null,
    /** API page discriminator. */
    @SerialName("object") public val objectType: String = "list",
) : List<SpendAlert> by data

/** Confirmation that an organization or project spend alert was deleted. */
@Serializable
public data class SpendAlertDeleted(
    /** Deleted alert identifier. */
    public val id: String,
    /** Whether the alert was deleted. */
    public val deleted: Boolean,
    /** Scoped alert deletion discriminator. */
    @SerialName("object") public val objectType: String,
)

/** Creates a complete hard spend limit replacement request. */
public fun spendLimitRequest(block: SpendLimitRequestBuilder.() -> Unit): SpendLimitRequest =
    SpendLimitRequestBuilder().apply(block).build()

/** Builder for a hard spend limit replacement. */
@OpenAIDsl
public class SpendLimitRequestBuilder {
    /** Positive hard limit amount, in cents. */
    public var thresholdAmount: Long? = null
    /** Threshold currency. */
    public var currency: String = "USD"
    /** Evaluation interval. */
    public var interval: String = "month"

    /** Builds a request, requiring the limit amount. */
    public fun build(): SpendLimitRequest = SpendLimitRequest(
        thresholdAmount = requireNotNull(thresholdAmount) { "thresholdAmount is required" },
        currency = currency,
        interval = interval,
    )
}

/** Creates a complete spend alert creation or replacement request. */
public fun spendAlertRequest(block: SpendAlertRequestBuilder.() -> Unit): SpendAlertRequest =
    SpendAlertRequestBuilder().apply(block).build()

/** Builder for a complete spend alert configuration. */
@OpenAIDsl
public class SpendAlertRequestBuilder {
    /** Nonnegative alert threshold, in cents. */
    public var thresholdAmount: Long? = null
    /** Complete email notification configuration. */
    public var notificationChannel: SpendAlertEmailChannel? = null
    /** Threshold currency. */
    public var currency: String = "USD"
    /** Evaluation interval. */
    public var interval: String = "month"

    /** Builds a request, requiring the amount and notification channel. */
    public fun build(): SpendAlertRequest = SpendAlertRequest(
        thresholdAmount = requireNotNull(thresholdAmount) { "thresholdAmount is required" },
        notificationChannel = requireNotNull(notificationChannel) { "notificationChannel is required" },
        currency = currency,
        interval = interval,
    )
}
