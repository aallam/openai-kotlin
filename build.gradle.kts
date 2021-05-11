buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        val kotlinVersion = "1.5.0"
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath(kotlin("serialization", version = kotlinVersion))
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.14.2")
    }
}

subprojects {
    repositories {
        mavenCentral()
    }
    val javadoc by tasks.creating(Javadoc::class)
}
