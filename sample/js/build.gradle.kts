plugins {
    kotlin("js")
}

dependencies {
    //implementation("com.aallam.openai:openai-client:<version>")
    implementation(projects.openaiClient)
    implementation(libs.okio.nodefilesystem)
}

kotlin {
    js {
        nodejs {
            binaries.executable()
        }
    }
}
