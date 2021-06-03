plugins {
    kotlin("multiplatform")
}

group = "com.aallam.kotlin-data"
version = "1.0-SNAPSHOT"

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }
    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation(project(":openai-client"))
                //implementation("com.aallam.openai:openai-client:0.1.0")
                implementation(libs.ktor.client.curl)
            }
        }
    }
}
