rootProject.name = "openai-kotlin"
includeBuild("build-support")

include(":openai-core")
include(":openai-client")
include(":openai-client-bom")

include(":sample:jvm")
include(":sample:js")
include(":sample:native")

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
