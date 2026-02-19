import org.jetbrains.kotlin.konan.target.HostManager
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest

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
    jsWasm()
    native()

    sourceSets {
        all {
            languageSettings {
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("okio.ExperimentalFileSystem")
                optIn("com.aallam.openai.api.ExperimentalOpenAI")
                optIn("com.aallam.openai.api.BetaOpenAI")
                optIn("com.aallam.openai.api.InternalOpenAI")
                optIn("com.aallam.openai.api.LegacyOpenAI")
            }
        }
        val commonMain by getting {
            dependencies {
                api(projects.openaiCore)
                api(libs.coroutines.core)
                api(libs.kotlinx.io.core)
                implementation(libs.kotlinx.io.bytestring)
                implementation(libs.serialization.json)
                api(libs.ktor.client.core)
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
                implementation(libs.ktor.client.mock)
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
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val wasmJsMain by getting {
            dependencies {
            }
        }
        val wasmJsTest by getting {
            dependencies {
                implementation(kotlin("test-wasm-js"))
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(libs.ktor.client.curl)
            }
        }
        if (HostManager.hostIsMac) {
            val darwinTest by getting {
                dependencies {
                    implementation(libs.ktor.client.darwin)
                }
            }
        }
    }
}

val liveTestsEnabled = providers.environmentVariable("OPENAI_LIVE_TESTS")
    .map { it == "1" }
    .orElse(false)

tasks.withType<KotlinJvmTest>().configureEach {
    onlyIf("Live API tests are disabled. Set OPENAI_LIVE_TESTS=1 to enable.") { liveTestsEnabled.get() }
}

tasks.withType<KotlinJsTest>().configureEach {
    onlyIf("Live API tests are disabled. Set OPENAI_LIVE_TESTS=1 to enable.") { liveTestsEnabled.get() }
}

tasks.withType<KotlinNativeTest>().configureEach {
    onlyIf("Live API tests are disabled. Set OPENAI_LIVE_TESTS=1 to enable.") { liveTestsEnabled.get() }
}
