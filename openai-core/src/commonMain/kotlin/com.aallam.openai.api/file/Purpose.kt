package com.aallam.openai.api.file

import com.aallam.openai.api.file.internal.PurposeSerializer
import kotlinx.serialization.Serializable

@Serializable(PurposeSerializer::class)
public sealed class Purpose(public val raw: String) {

    /**
     * File for Searches.
     */
    public object Search : Purpose("search")

    /**
     * File for Answers.
     */
    public object Answers : Purpose("answers")

    /**
     * File for classifications.
     */
    public object Classifications : Purpose("classifications")

    /**
     * Provide a custom purpose.
     */
    public class Custom(raw: String) : Purpose(raw)

    override fun toString(): String {
        return raw
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Purpose) return false
        if (raw != other.raw) return false
        return true
    }

    override fun hashCode(): Int {
        return raw.hashCode()
    }
}
