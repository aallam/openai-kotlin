buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.4.31"))
        classpath(kotlin("serialization", version = "1.4.31"))
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
