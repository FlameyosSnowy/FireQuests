plugins {
    kotlin("jvm") version "2.3.0" apply false
    id("com.gradleup.shadow") version "8.3.1" apply false
    // shadowjar
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

group = "me.flame.quests"
version = "1.0.0"

subprojects {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/") {
            name = "papermc-repo"
        }
        maven("https://jitpack.io")
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {

        dependencies {
            add("implementation", kotlin("stdlib"))
            add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
        }

        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
            jvmToolchain(21)
        }

        tasks.named("build") {
            dependsOn("shadowJar")
        }
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "com.gradleup.shadow")
}
