plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("maven-publish")
    id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.0"
    id("binary-compatibility-validator")
    id("com.diffplug.spotless")
    id("org.jetbrains.dokka")
    id("build-support")
}

val VERSION_NAME: String by project
version = VERSION_NAME

kotlin {
    explicitApi()
    jvm()

    sourceSets {
        all {
            languageSettings.apply{
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
            }
        }
        val commonMain by getting {
            dependencies {
                api(libs.kotlinx.io.core)
                api(libs.serialization.json)
                implementation(libs.serialization.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["kotlin"])
        }
    }

    repositories {
        maven {
            name = "slingshot"
            url = uri("artifactregistry://us-east1-maven.pkg.dev/ashley-repositories/ashley-maven")
        }
    }
}
