package me.flame.quests.spigot.config

import hazae41.minecraft.kutils.bukkit.ConfigFile
import hazae41.minecraft.kutils.bukkit.sections
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class QuestConfig(plugin: JavaPlugin) :
    ConfigFile(File(plugin.dataFolder, "config.yml")) {

    var debug by boolean("debug")

    val quests: Map<ConfigurationSection?, QuestSection>
        get() {
            val root = config.getConfigurationSection("quests") ?: return emptyMap()
            return root.sections.associateWith { key ->
                QuestSection(key!!)
            }
        }

    val database: DatabaseSection?
        get() = config.getConfigurationSection("database")?.let { DatabaseSection(it) }

    fun reload() {
        // calls BukkitConfiguration.loadConfiguration(file) which reloads the configuration
        config
    }
}