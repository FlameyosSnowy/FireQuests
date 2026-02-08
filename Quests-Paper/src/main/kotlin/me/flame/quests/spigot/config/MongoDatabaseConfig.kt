package me.flame.quests.spigot.config

import org.bukkit.configuration.ConfigurationSection

class MongoDatabaseConfig(
    private val section: ConfigurationSection
) : DatabaseConfig {

    val connectionString: String =
        section.getString("connection-string")
            ?: error("database.mongodb.connection-string is required")

    val databaseName: String =
        section.getString("database-name")
            ?: error("database.mongodb.database-name is required")
}
