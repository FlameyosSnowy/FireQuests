plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1"
    kotlin("jvm") version "2.3.0"
}

group = "me.flame.quests"
version = "1.0.0"

dependencies {
    implementation("studio.mevera:imperat-core:2.4.2")
    implementation("studio.mevera:imperat-bukkit:2.4.2")

    implementation(project(":Quests-API"))

    implementation("com.github.Mqzn:Lotus:1.6.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.2")

    compileOnly("com.github.placeholderapi:placeholderapi:2.11.6")

    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

tasks {
    runServer {
        minecraftVersion("1.21")
    }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}
