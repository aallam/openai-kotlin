buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath(kotlin("gradle-plugin", version = "1.4.31"))
    classpath(kotlin("serialization", version = "1.4.31"))
  }
}

subprojects {
  group = "com.aallam.kotlin-data"
  version = "0.1.0-SNAPSHOT"

  repositories {
    mavenCentral()
  }
}