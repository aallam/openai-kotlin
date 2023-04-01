package com.aallam.openai.api

/**
 * API marked with this annotation is internal, and it is not intended to be used outside OpenAI API client.
 * It could be modified or removed without any notice. Using it could cause undefined behaviour and/or any unexpected
 * effects.
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.TYPEALIAS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This API is internal in OpenAI API client and should not be used. It could be removed or changed without notice."
)
public annotation class InternalOpenAI
