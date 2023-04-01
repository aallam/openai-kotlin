-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
# Serializable models
-keep,includedescriptorclasses com.aallam.openai.api.**$$serializer { *; }
-keepclassmembers class com.aallam.openai.api.** {
    *** Companion;
}
-keepclasseswithmembers com.aallam.openai.api.** {
    kotlinx.serialization.KSerializer serializer(...);
}
