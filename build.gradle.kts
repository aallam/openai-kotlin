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
  group = OpenAI.group
  version = OpenAI.version

  repositories {
    mavenCentral()
  }
}