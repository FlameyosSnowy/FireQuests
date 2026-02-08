@file:Suppress("SameParameterValue")

package me.flame.quests.spigot.config

import org.bukkit.configuration.ConfigurationSection

class DatabaseSection(
    override var config: ConfigurationSection
) : Config() {

    val type by string("type")

    fun load(): DatabaseConfig =
        when (type.lowercase()) {
            "mongodb" -> MongoDatabaseConfig(
                configSection("mongodb")
            )
            else -> error("Unsupported database type: $type")
        }

    private fun configSection(path: String): ConfigurationSection =
        config.getConfigurationSection(path)
            ?: error("Missing database.$path section")
}
