plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.vanniktech.maven.publish")
    id("binary-compatibility-validator")
}

kotlin {
    explicitApi()
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js(LEGACY) {
        nodejs()
    }

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }
    nativeTarget.apply {
        binaries {}
    }

    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
                optIn("okio.ExperimentalFileSystem")
            }
        }
        val commonMain by getting {
            dependencies {
                api(project(":openai-core"))
                api(libs.coroutines.core)
                implementation(libs.ktor.client.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.serialization)
                implementation(libs.ktor.client.auth)
                implementation(libs.okio.multiplatform)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.okio.fakefilesystem)
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
        val nativeMain by getting
        val nativeTest by getting {
            dependencies {
                implementation(libs.ktor.client.curl)
            }
        }
    }
}
