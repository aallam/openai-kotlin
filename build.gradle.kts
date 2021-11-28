buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        val kotlinVersion = "1.6.0"
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath(kotlin("serialization", version = kotlinVersion))
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.18.0")
        classpath("org.jetbrains.kotlinx:binary-compatibility-validator:0.8.0")
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
    val javadoc by tasks.creating(Javadoc::class)
}
