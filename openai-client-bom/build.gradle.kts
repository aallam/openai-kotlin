plugins {
    id("com.vanniktech.maven.publish")
    id("com.google.cloud.artifactregistry.gradle-plugin") version "2.2.0"
    id("java-platform")
}

dependencies {
    constraints {
        api(projects.openaiCore)
        api(projects.openaiClient)
        api(libs.ktor.client.apache)
        api(libs.ktor.client.cio)
        api(libs.ktor.client.java)
        api(libs.ktor.client.jetty)
        api(libs.ktor.client.okhttp)
    }
}

publishing {
    repositories {
        maven {
            name = "slingshot"
            url = uri("artifactregistry://us-east1-maven.pkg.dev/ashley-repositories/ashley-maven")
        }
    }
}
