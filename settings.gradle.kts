rootProject.name = "OpenAI-Kotlin"
includeBuild("build-support")

include(":openai-core")
include(":openai-client")

include(":sample:jvm")
include(":sample:js")
include(":sample:native")

enableFeaturePreview("VERSION_CATALOGS")

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
