plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "me.flame.trialfordevroom"
version = "1.0.0"

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")

    implementation("com.github.Hazae41:Kotlin4MC:3.3.3")
    implementation("studio.mevera:imperat-core:2.4.2")

    implementation(project(":Quests-API"))

    implementation("com.github.Mqzn:Lotus:1.6.0")
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
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