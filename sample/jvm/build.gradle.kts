plugins {
    id("org.jetbrains.kotlin.jvm")
    application
}

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation(project(":openai-client"))
    //implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.aallam.openai:openai-client:0.1.0")
    implementation(Ktor("client-apache"))
}

application {
    mainClassName = "com.aallam.openai.sample.jvm.AppKt"
}
