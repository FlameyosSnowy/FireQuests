package me.flame.quests.spigot

import me.flame.quests.spigot.listeners.MovementAndExplorationListener
import hazae41.minecraft.kutils.bukkit.info

import io.github.mqzen.menus.Lotus

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

import me.flame.quests.api.database.mongodb.MongoCredentials
import me.flame.quests.api.database.mongodb.MongoQuestPlayerDatabase
import me.flame.quests.api.database.mongodb.MongoQuestStore
import me.flame.quests.api.manager.QuestEventRouter
import me.flame.quests.api.quest.QuestEventType
import me.flame.quests.api.quest.QuestManager
import me.flame.quests.api.quest.QuestSubscription
import me.flame.quests.api.quest.QuestSystemConfig
import me.flame.quests.api.quest.entity.QuestPlayer
import me.flame.quests.api.quest.payload.QuestPayload
import me.flame.quests.api.quest.progress.ProgressChanger

import me.flame.quests.spigot.commands.QuestsAdminCommand
import me.flame.quests.spigot.commands.QuestsCommand
import me.flame.quests.spigot.config.QuestConfig
import me.flame.quests.spigot.config.QuestSection
import me.flame.quests.spigot.hooks.initHooks
import me.flame.quests.spigot.listeners.BlockMineListener
import me.flame.quests.spigot.listeners.EntityDeathListener
import me.flame.quests.spigot.listeners.PlayerConnectionStateListener
import me.flame.quests.spigot.rewards.BukkitCommandRewardExecutor
import me.flame.quests.spigot.rewards.ItemRewardExecutor

import org.bukkit.plugin.java.JavaPlugin

import studio.mevera.imperat.BukkitImperat

import kotlin.coroutines.CoroutineContext

class QuestsPlugin : JavaPlugin(), CoroutineScope {
    lateinit var questsManager: QuestManager
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    override fun onEnable() {
        initHooks(this)

        val bukkitConfig = QuestConfig(this)
        val apiConfig = QuestSystemConfig(
            quests = bukkitConfig.quests.values.map { it.toQuest(bukkitConfig.quests) },
            debug = bukkitConfig.debug
        )

        val database = bukkitConfig.database ?: error("No database configured")
        val (playerDatabase, questStore) = when (database.type.lowercase()) {
            "mongodb" -> {
                val creds = MongoCredentials(database.connectionString, database.databaseName)
                MongoQuestPlayerDatabase(creds) to MongoQuestStore(creds)
            }
            else -> error("Unsupported database type: ${database.type}")
        }

        val commandExecutor = BukkitCommandRewardExecutor()
        val itemExecutor = ItemRewardExecutor()

        questsManager = QuestManager(
            config = apiConfig,
            playerDatabase = playerDatabase,
            questStore = questStore,
            commandRewardExecutor = commandExecutor,
            itemRewardExecutor = itemExecutor,
            scope = this
        )

        val questEventRouter = QuestEventRouter()

        for (section in bukkitConfig.quests.values) {
            registerQuestSubscription(
                router = questEventRouter,
                questsManager = questsManager,
                section = section,
                type = QuestEventType.valueOf(section.type),
            )
        }

        val imperat = BukkitImperat.builder(this).build()
        val lotus = Lotus.load(this)

        imperat.registerCommand(QuestsAdminCommand(this, questsManager))
        imperat.registerCommand(QuestsCommand(this, lotus))

        server.pluginManager.registerEvents(PlayerConnectionStateListener(this, questsManager), this)
        server.pluginManager.registerEvents(EntityDeathListener(questEventRouter, questsManager, this), this)
        server.pluginManager.registerEvents(BlockMineListener(questEventRouter, questsManager, this), this)
        server.pluginManager.registerEvents(MovementAndExplorationListener(questEventRouter, questsManager, this), this)

        info("Quests Plugin Enabled")
    }

    override fun onDisable() {
        runBlocking {
            withContext(Dispatchers.IO) {
                questsManager.unloadAllPlayers()
                job.cancel()
                info("Quests Plugin Disabled")
            }
        }
    }

    fun registerQuestSubscription(
        router: QuestEventRouter,
        questsManager: QuestManager,
        section: QuestSection,
        type: QuestEventType
    ) {
        val subscription = QuestSubscription(
            questId = section.id,
            filter = { ctx -> ctx.payload.matchesTarget(section.target) },
            changer = object : ProgressChanger {
                override suspend fun apply(
                    player: QuestPlayer,
                    questId: String,
                    payload: QuestPayload
                ) {
                    questsManager.updateProgress(player, questId, payload.increment)
                }
            }
        )

        router.register(type, subscription)
    }

}
