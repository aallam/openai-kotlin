package com.aallam.openai.api.file

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Expiration measured from the file creation timestamp, in seconds. */
@Serializable
public data class FileExpiration(
    /** Lifetime in seconds. */
    @SerialName("seconds") public val seconds: Int,
    /** Timestamp used to calculate expiration. */
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    @SerialName("anchor") public val anchor: String = "created_at",
)
