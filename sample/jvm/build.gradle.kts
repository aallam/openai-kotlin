plugins {
    id("org.jetbrains.kotlin.jvm")
    application
}

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation(project(":openai-client"))
    //implementation("com.aallam.openai:openai-client:0.1.0")
    implementation(libs.ktor.client.apache)
}

application {
    mainClass.set("com.aallam.openai.sample.jvm.AppKt")
}
