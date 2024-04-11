package com.aallam.openai.azure.api.filtering

import kotlinx.serialization.Serializable

/**
 * Ratings for the intensity and risk level of harmful content.
 */
@Serializable
@JvmInline
public value class ContentFilterSeverity(public val value: String) {
    public companion object {
        /**
         * Content may be related to violence, self-harm, sexual, or hate categories but the terms
         * are used in general, journalistic, scientific, medical, and similar professional contexts,
         * which are appropriate for most audiences.
         */
        public val SAFE: ContentFilterSeverity = ContentFilterSeverity("safe")

        /**
         * Content that expresses prejudiced, judgmental, or opinionated views, includes offensive
         * use of language, stereotyping, use cases exploring a fictional world (for example, gaming,
         * literature) and depictions at low intensity.
         */
        public val LOW: ContentFilterSeverity = ContentFilterSeverity("low")

        /**
         * Content that uses offensive, insulting, mocking, intimidating, or demeaning language
         * towards specific identity groups, includes depictions of seeking and executing harmful
         * instructions, fantasies, glorification, promotion of harm at medium intensity.
         */
        public val MEDIUM: ContentFilterSeverity = ContentFilterSeverity("medium")

        /**
         * Content that displays explicit and severe harmful instructions, actions,
         * damage, or abuse; includes endorsement, glorification, or promotion of severe
         * harmful acts, extreme or illegal forms of harm, radicalization, or non-consensual
         * power exchange or abuse.
         */
        public val HIGH: ContentFilterSeverity = ContentFilterSeverity("high")
    }
}