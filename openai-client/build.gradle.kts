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
        implementation(Ktor("client"))
        implementation(Ktor("client-json"))
        implementation(Ktor("client-logging"))
        implementation(Ktor("client-serialization"))
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
        implementation(Ktor("client-okhttp"))
        implementation(Logback("classic"))
      }
    }
  }
}
