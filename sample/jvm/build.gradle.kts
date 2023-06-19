plugins {
    id("org.jetbrains.kotlin.jvm")
    kotlin("plugin.serialization")
    application
}

dependencies {
    //implementation("com.aallam.openai:openai-client:<version>")
    implementation(projects.openaiClient)
    implementation(libs.ktor.client.apache)
}

application {
    mainClass.set("com.aallam.openai.sample.jvm.AppKt")
}
