package com.aallam.openai.api

import kotlin.annotation.AnnotationTarget.*

/**
 * This annotation marks a library API as beta.
 *
 * Any usage of a declaration annotated with `@BetaOpenAI` must be accepted either by annotating that
 * usage with the [OptIn] annotation, e.g. `@OptIn(BetaOpenAI::class)`, or by using the compiler
 * argument `-Xopt-in=com.aallam.openai.api.BetaOpenAI`.
 */
@Target(
    CLASS,
    ANNOTATION_CLASS,
    PROPERTY,
    FIELD,
    LOCAL_VARIABLE,
    VALUE_PARAMETER,
    CONSTRUCTOR,
    FUNCTION,
    PROPERTY_GETTER,
    PROPERTY_SETTER,
    TYPEALIAS
)
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(message = "This API is marked legacy by OpenAI.")
public annotation class LegacyOpenAI
