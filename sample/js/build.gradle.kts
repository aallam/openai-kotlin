plugins {
    kotlin("js")
}

dependencies {
    implementation(project(":openai-client"))
    //implementation("com.aallam.openai:openai-client:0.1.0")
}

kotlin {
    js(LEGACY) {
        nodejs {
            binaries.executable()
        }
    }
}
