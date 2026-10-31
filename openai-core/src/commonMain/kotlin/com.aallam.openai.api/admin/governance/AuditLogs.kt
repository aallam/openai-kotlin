package com.aallam.openai.api.admin.governance

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/** One audit event with typed common fields and its complete original JSON payload. */
@Serializable(with = AuditLogSerializer::class)
public data class AuditLog(
    /** Audit-log identifier. */
    public val id: String,
    /** Event time in Unix seconds. */
    public val effectiveAt: Long,
    /** Event discriminator, including tenant-scoped event names. */
    public val type: String,
    /** Session or API-key actor, when supplied. */
    public val actor: AuditLogActor? = null,
    /** Project associated with the event, when supplied. */
    public val project: AuditLogProject? = null,
    /** Original JSON, including evolving per-event detail and unknown fields. */
    public val rawData: JsonObject = JsonObject(emptyMap()),
) {
    /** Event-specific detail stored under its type or the tenant-event details field. */
    public val details: JsonObject? get() = (rawData[type] ?: rawData["details"]) as? JsonObject
}

/** Actor information common to audit event variants. */
@Serializable
public data class AuditLogActor(
    /** `session` or `api_key` actor type. */
    public val type: String? = null,
    /** API-key tracking information, not the secret key. */
    @SerialName("api_key") public val apiKey: AuditLogApiKeyActor? = null,
    /** User-session information. */
    public val session: AuditLogSessionActor? = null,
)

/** API-key actor metadata. */
@Serializable
public data class AuditLogApiKeyActor(
    /** API-key tracking identifier. */
    public val id: String? = null,
    /** `user` or `service_account`. */
    public val type: String? = null,
    /** User associated with the key. */
    public val user: AuditLogUser? = null,
    /** Service account associated with the key. */
    @SerialName("service_account") public val serviceAccount: AuditLogServiceAccount? = null,
)

/** User-session actor metadata. */
@Serializable
public data class AuditLogSessionActor(
    /** Source IP address. */
    @SerialName("ip_address") public val ipAddress: String? = null,
    /** Session user. */
    public val user: AuditLogUser? = null,
)

/** User details returned with an audit actor. */
@Serializable
public data class AuditLogUser(
    /** User identifier. */
    public val id: String? = null,
    /** User email address. */
    public val email: String? = null,
)

/** Service-account details returned with an audit actor. */
@Serializable
public data class AuditLogServiceAccount(
    /** Service-account identifier. */
    public val id: String? = null,
)

/** Project details associated with an audit event. */
@Serializable
public data class AuditLogProject(
    /** Project identifier. */
    public val id: String? = null,
    /** Project name. */
    public val name: String? = null,
)

/** Inclusive or exclusive event-time boundaries, in Unix seconds. */
public data class AuditLogTimeRange(
    /** Strictly later than this time. */
    public val gt: Long? = null,
    /** At or later than this time. */
    public val gte: Long? = null,
    /** Strictly earlier than this time. */
    public val lt: Long? = null,
    /** At or earlier than this time. */
    public val lte: Long? = null,
)

/** Audit-log filters and cursor controls. Null fields leave the server's defaults unchanged. */
public data class AuditLogQuery(
    /** Actor email addresses. */
    public val actorEmails: List<String>? = null,
    /** User, service-account or API-key tracking identifiers. */
    public val actorIds: List<String>? = null,
    /** Forward cursor. */
    public val after: String? = null,
    /** Backward cursor. */
    public val before: String? = null,
    /** Event-time range. */
    public val effectiveAt: AuditLogTimeRange? = null,
    /** Event type names. */
    public val eventTypes: List<String>? = null,
    /** Maximum number of events in this page. */
    public val limit: Int? = null,
    /** Project identifiers. */
    public val projectIds: List<String>? = null,
    /** Target resource identifiers. */
    public val resourceIds: List<String>? = null,
    /** Limit results to tenant-scoped events. */
    public val tenantOnly: Boolean? = null,
)

internal object AuditLogSerializer : KSerializer<AuditLog> {
    override val descriptor: SerialDescriptor = JsonObject.serializer().descriptor

    override fun deserialize(decoder: Decoder): AuditLog {
        require(decoder is JsonDecoder)
        val obj = decoder.decodeJsonElement().jsonObject
        return AuditLog(
            id = obj.getValue("id").jsonPrimitive.content,
            effectiveAt = obj.getValue("effective_at").jsonPrimitive.long,
            type = obj.getValue("type").jsonPrimitive.content,
            actor = obj["actor"]?.takeUnless { it is JsonNull }?.let { decoder.json.decodeFromJsonElement<AuditLogActor>(it) },
            project = obj["project"]?.takeUnless { it is JsonNull }?.let { decoder.json.decodeFromJsonElement<AuditLogProject>(it) },
            rawData = obj,
        )
    }

    override fun serialize(encoder: Encoder, value: AuditLog) {
        require(encoder is JsonEncoder)
        encoder.encodeJsonElement(buildJsonObject {
            value.rawData.forEach { (key, element) -> put(key, element) }
            put("id", value.id)
            put("effective_at", value.effectiveAt)
            put("type", value.type)
            value.actor?.let { put("actor", encoder.json.encodeToJsonElement(it)) }
            value.project?.let { put("project", encoder.json.encodeToJsonElement(it)) }
        })
    }
}
