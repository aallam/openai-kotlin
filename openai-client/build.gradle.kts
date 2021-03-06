plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
}

kotlin {
  explicitApi()
  jvm {
    compilations.all {
      kotlinOptions.jvmTarget = "1.8"
    }
    testRuns["test"].executionTask.configure {
      useJUnit()
    }
  }
  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(project(":openai-core"))
        implementation("io.ktor:ktor-client:1.5.2")
        implementation("io.ktor:ktor-client-json:1.5.2")
        implementation("io.ktor:ktor-client-auth:1.5.2")
        implementation("io.ktor:ktor-client-logging:1.5.2")
        implementation("io.ktor:ktor-client-serialization:1.5.2")
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }
    val jvmMain by getting
    val jvmTest by getting {
      dependencies {
        implementation(kotlin("test-junit"))
        implementation("io.ktor:ktor-client-okhttp:1.5.2")
        implementation("ch.qos.logback:logback-classic:1.2.3")
      }
    }
  }
}
