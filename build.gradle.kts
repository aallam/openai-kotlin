buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath(kotlin("gradle-plugin", version = "1.5.0-M1"))
  }
}

subprojects {
  group = "com.aallam.kotlin-data"
  version = "0.1.0-SNAPSHOT"
  repositories {
    mavenCentral()
  }
}