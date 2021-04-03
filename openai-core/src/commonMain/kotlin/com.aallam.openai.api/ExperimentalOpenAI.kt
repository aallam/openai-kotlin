package com.aallam.openai.api

import kotlin.annotation.AnnotationTarget.*

/**
 * This annotation marks a library API as experimental.
 *
 * Any usage of a declaration annotated with `@ExperimentalOpenAI` must be accepted either by annotating that
 * usage with the [OptIn] annotation, e.g. `@OptIn(ExperimentalOpenAI::class)`, or by using the compiler
 * argument `-Xopt-in=com.aallam.openai.api.ExperimentalOpenAI`.
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
@RequiresOptIn(message = "This OpenAI API is experimental, It can be incompatibly changed in the future.")
public annotation class ExperimentalOpenAI