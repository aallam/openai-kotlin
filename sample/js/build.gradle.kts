plugins {
    kotlin("js")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":openai-client"))
}

kotlin {
    js(LEGACY) {
        nodejs {
            binaries.executable()
        }
    }
}
