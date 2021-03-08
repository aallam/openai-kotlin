plugins {
    id("org.jetbrains.kotlin.jvm")
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(project(":openai-client"))
    implementation(Ktor("client-apache"))
}

application {
    mainClassName = "com.aallam.openai.sample.jvm.AppKt"
}
