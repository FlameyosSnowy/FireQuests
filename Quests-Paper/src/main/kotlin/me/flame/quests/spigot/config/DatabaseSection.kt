package me.flame.quests.spigot.config

import hazae41.minecraft.kutils.bukkit.Config
import org.bukkit.configuration.ConfigurationSection

class DatabaseSection(
    override var config: ConfigurationSection
) : Config() {
    val type by string("type") // "mongodb" or other types
    val connectionString by string("connection-string")
    val databaseName by string("database-name")
}