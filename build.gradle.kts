plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "com.expansemc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo-new.spongepowered.org/repository/maven-public/")
}

val shaded: Configuration by configurations.creating

dependencies {
    shaded(implementation(project(":bending-api"))!!)

    compileOnly("org.spongepowered:spongeapi:8.0.0-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks {
    shadowJar {
        archiveClassifier.set("dist")
        configurations = listOf(shaded)
    }
}

artifacts {
    archives(tasks.shadowJar)
}