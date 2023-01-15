plugins {
    kotlin("js")
}

dependencies {
    //implementation("com.aallam.openai:openai-client:<version>")
    implementation(projects.openaiClient)
}

kotlin {
    js {
        nodejs {
            binaries.executable()
        }
    }
}
