buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        val kotlinVersion = "1.4.31"
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath(kotlin("serialization", version = kotlinVersion))
        classpath(MavenPublish())
    }
}

project.extensions.extraProperties.apply {
    set("GROUP", OpenAI.group)
    set("VERSION_NAME", OpenAI.version)
}

subprojects {
    repositories {
        mavenCentral()
    }
    val javadoc by tasks.creating(Javadoc::class)
}
