package com.aallam.openai.api.admin.governance

import com.aallam.openai.api.core.SortOrder

/** Certificate-list pagination and ordering. */
public data class CertificateListQuery(
    /** Forward cursor. */
    public val after: String? = null,
    /** Maximum certificates in this page. */
    public val limit: Int? = null,
    /** Creation-time ordering. */
    public val order: SortOrder? = null,
)

/** External-key registration filters and pagination. */
public data class ExternalKeyListQuery(
    /** Forward cursor. */
    public val after: String? = null,
    /** Maximum registrations in this page. */
    public val limit: Int? = null,
    /** Creation-time ordering. */
    public val order: SortOrder? = null,
    /** `ekm` or `psp_decryption` purpose filter. */
    public val purpose: String? = null,
)

/** External-storage registration filters and pagination. */
public data class ExternalStorageListQuery(
    /** Forward cursor. */
    public val after: String? = null,
    /** Maximum registrations in this page. */
    public val limit: Int? = null,
    /** Creation-time ordering. */
    public val order: SortOrder? = null,
    /** Project identifier filter. */
    public val projectId: String? = null,
)

/** Project rate-limit cursor controls. */
public data class ProjectRateLimitQuery(
    /** Forward cursor. */
    public val after: String? = null,
    /** Backward cursor. */
    public val before: String? = null,
    /** Maximum rate limits in this page. */
    public val limit: Int? = null,
)
