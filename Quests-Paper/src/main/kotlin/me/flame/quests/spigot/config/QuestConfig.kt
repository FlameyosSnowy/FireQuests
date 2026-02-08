package me.flame.quests.spigot.config

import org.bukkit.plugin.java.JavaPlugin
import java.io.File

// I had to do this because I don't want to use hazae41's config system
// hazae41's config system doesn't give you the ability to reload, lmao
class QuestConfig(plugin: JavaPlugin) :
    ConfigFile(File(plugin.dataFolder, "config.yml")) {

    init {
        reload()
    }

    var debug by boolean("debug")

    val quests: Map<String, QuestSection>
        get() {
            val root = config.getConfigurationSection("quests") ?: return emptyMap()
            return root.getKeys(false).associateWith { key ->
                QuestSection(root.getConfigurationSection(key)!!)
            }
        }

    val database: DatabaseSection?
        get() = config.getConfigurationSection("database")?.let { DatabaseSection(it) }
}
