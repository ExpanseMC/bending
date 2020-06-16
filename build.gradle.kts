plugins {
    java
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.serialization") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    `maven-publish`
}

group = "com.expansemc"
version = "0.2.1"

repositories {
    mavenCentral()
    // Spigot
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
    // Bungeecord text
    maven("https://oss.sonatype.org/content/groups/public/")
    // Configurate and math
    maven("https://repo.spongepowered.org/maven")
    // Configurate-Serialization
    maven("https://jitpack.io")
    //Brigadier
    maven("https://libraries.minecraft.net")
}

dependencies {
    implementation(project(":bending-api"))
    implementation("org.spongepowered:configurate-hocon:3.6.1")
    implementation("me.lucko:commodore:1.8")

    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")

    runtime(project(":bending-api"))
    runtime("org.spongepowered:configurate-hocon:3.6.1")
    runtime("me.lucko:commodore:1.8")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

val shadowJar: com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar by tasks

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    processResources {
        filesMatching("plugin.yml") {
            filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to mapOf("version" to project.version))
        }
    }
    shadowJar {
        val bendingApi = project(":bending-api")

        archiveClassifier.set(null as String?)
        archiveBaseName.set("Bending")
        archiveVersion.set("v0.2.0-a${bendingApi.properties["version.api"]!!}")

        dependencies {
            exclude(dependency("org.spigotmc:spigot-api:.*"))
            exclude(dependency("com.google.guava:guava:.*"))
            exclude(dependency("com.mojang:brigadier:.*"))
        }

        relocate("me.lucko.commodore", "com.expansemc.bending.plugin.libs.commodore")
    }
}

artifacts {
    add("archives", shadowJar)
}