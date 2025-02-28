plugins {
    kotlin("multiplatform")
}

kotlin {
    js {
        nodejs {
            binaries.executable()
        }
    }

    // Option #1. Declare dependencies in the `sourceSets` block:
    sourceSets {
        val jsMain by getting {
            dependencies {
                //implementation("com.aallam.openai:openai-client:<version>")
                implementation(projects.openaiClient)
            }
        }
    }
}
