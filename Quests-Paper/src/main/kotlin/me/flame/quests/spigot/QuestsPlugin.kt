package me.flame.quests.spigot

import hazae41.minecraft.kutils.bukkit.info

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

import me.flame.quests.api.database.mongodb.MongoCredentials
import me.flame.quests.api.database.mongodb.MongoQuestPlayerDatabase
import me.flame.quests.api.database.mongodb.MongoQuestStore
import me.flame.quests.api.manager.QuestEventRouter
import me.flame.quests.spigot.commands.QuestsAdminCommand
import me.flame.quests.spigot.commands.QuestsCommand
import me.flame.quests.spigot.config.QuestConfig
import me.flame.quests.spigot.hooks.initHooks
import me.flame.quests.spigot.listeners.EntityDeathListener
import me.flame.quests.spigot.listeners.PlayerConnectionStateListener
import me.flame.quests.spigot.manager.QuestManager

import org.bukkit.plugin.java.JavaPlugin
import studio.mevera.imperat.BukkitImperat

import kotlin.coroutines.CoroutineContext

class QuestsPlugin : JavaPlugin(), CoroutineScope {
    lateinit var config: QuestConfig
    lateinit var questsManager: QuestManager

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext =
        job + Dispatchers.IO

    override fun onEnable() {
        initHooks(this)

        config = QuestConfig(this)

        val database = config.database ?: return
        val (questPlayerDatabase, questStore) = when (database.type) {
            "mongodb" -> {
                val credentials = MongoCredentials(
                    database.connectionString,
                    database.databaseName
                )

                MongoQuestPlayerDatabase(credentials) to MongoQuestStore(credentials)
            }
            else -> error("Unsupported database type: ${database.type}")
        }

        questsManager = QuestManager(this, config, questPlayerDatabase, questStore)
        val questEventRouter = QuestEventRouter(parentScope = this)

        val imperat = BukkitImperat.builder(this)
            .build()

        imperat.registerCommand(QuestsAdminCommand())
        imperat.registerCommand(QuestsCommand(this))

        server.pluginManager.registerEvents(PlayerConnectionStateListener(questsManager), this)
        server.pluginManager.registerEvents(EntityDeathListener(questEventRouter, questsManager, this), this)

        info("Quests Plugin Enabled")
    }

    override fun onDisable() {
        job.cancel()
        info("Quests Plugin Disabled")
    }
}
