plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.vanniktech.maven.publish")
    id("binary-compatibility-validator")
    id("com.diffplug.spotless")
    id("org.jetbrains.dokka")
    id("build-support")
}

kotlin {
    explicitApi()
    jvm()
    jsNode()
    native()

    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
                optIn("okio.ExperimentalFileSystem")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("com.aallam.openai.api.ExperimentalOpenAI")
                optIn("com.aallam.openai.api.BetaOpenAI")
            }
        }
        val commonMain by getting {
            dependencies {
                api(projects.openaiCore)
                api(libs.coroutines.core)
                api(libs.okio)
                implementation(libs.serialization.json)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.serialization.json)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.openaiCore)
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.coroutines.test)
                implementation(libs.okio.fakefilesystem)
                implementation(libs.ulid)
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.ktor.client.okhttp)
                implementation(libs.logback.classic)
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(libs.okio.nodefilesystem)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(libs.ktor.client.curl)
            }
        }
        val darwinTest by getting {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}
