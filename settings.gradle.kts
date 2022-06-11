rootProject.name = "OpenAI-Kotlin"
includeBuild("build-support")

include(":openai-core")
include(":openai-client")
include(":openai-client-bom")

include(":sample:jvm")
include(":sample:js")
include(":sample:native")

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
