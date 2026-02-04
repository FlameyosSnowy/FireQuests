package me.flame.quests.spigot.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import me.flame.quests.api.database.QuestPlayerDatabase
import me.flame.quests.api.database.QuestProgressDatabase
import me.flame.quests.api.database.QuestStore
import me.flame.quests.api.database.mongodb.MongoQuestPlayer
import me.flame.quests.api.quest.DefaultQuestProgress
import me.flame.quests.api.quest.Quest
import me.flame.quests.api.quest.QuestProgress
import me.flame.quests.api.quest.entity.QuestPlayer
import me.flame.quests.spigot.config.QuestConfig
import me.flame.quests.spigot.config.QuestSection
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

class QuestManager(
    private val plugin: JavaPlugin,
    private val config: QuestConfig,
    private val playerDatabase: QuestPlayerDatabase,
    private val progressDatabase: QuestProgressDatabase,
    private val questStore: QuestStore
) {

    private val logger: Logger = plugin.logger
    private val scope = CoroutineScope(Dispatchers.IO)

    // Cache for loaded quests
    private val questCache = ConcurrentHashMap<UUID, Quest>()

    // Cache for online players' quest data
    private val playerCache = ConcurrentHashMap<UUID, QuestPlayer>()

    // Cache for quest progress
    private val progressCache = ConcurrentHashMap<Pair<UUID, UUID>, QuestProgress>()

    init {
        loadQuests()
        if (config.debug) {
            logger.info("QuestManager initialized with ${questCache.size} quests")
        }
    }

    /**
     * Load all quests from the config
     */
    private fun loadQuests() {
        questCache.clear()

        val quests = config.quests
        quests.forEach { (section, questSection) ->
            try {
                val quest = createQuestFromSection(questSection)
                questCache[quest.id] = quest

                if (config.debug) {
                    logger.info("Loaded quest: ${quest.id} (${questSection.id})")
                }
            } catch (e: Exception) {
                logger.severe("Failed to load quest from section: ${section?.name}")
                e.printStackTrace()
            }
        }
    }

    /**
     * Create a Quest object from a QuestSection
     */
    private fun createQuestFromSection(section: QuestSection): Quest {
        return Quest(
            id = UUID.fromString(section.id),
            type = section.type,
            target = section.target,
            requiredAmount = section.requiredAmount,
            rewardCommand = section.rewardCommand,
            rewardItems = section.rewardItems
        )
    }

    /**
     * Get a quest by ID
     */
    fun getQuest(questId: UUID): Quest? {
        return questCache[questId]
    }

    /**
     * Get all loaded quests
     */
    fun getAllQuests(): Collection<Quest> {
        return questCache.values
    }

    /**
     * Load player data when they join
     */
    fun loadPlayer(player: Player) {
        scope.launch {
            try {
                val result = playerDatabase.findById(player.uniqueId)
                result.onSuccess { questPlayer ->
                    val loadedPlayer = questPlayer ?: MongoQuestPlayer.create(
                        id = player.uniqueId,
                        name = player.name
                    )
                    playerCache[player.uniqueId] = loadedPlayer

                    if (config.debug) {
                        logger.info("Loaded player data for ${player.name}")
                    }
                }.onFailure { error ->
                    logger.severe("Failed to load player data for ${player.name}: ${error.message}")
                }
            } catch (e: Exception) {
                logger.severe("Error loading player ${player.name}: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * Unload player data when they leave
     */
    fun unloadPlayer(player: Player) {
        scope.launch {
            try {
                val questPlayer = playerCache.remove(player.uniqueId)
                if (questPlayer != null) {
                    playerDatabase.update(questPlayer)

                    // Save all progress for this player
                    progressCache.entries
                        .filter { it.key.first == player.uniqueId }
                        .forEach { (key, progress) ->
                            progressDatabase.saveProgress(key.first, key.second, progress)
                            progressCache.remove(key)
                        }

                    if (config.debug) {
                        logger.info("Unloaded and saved player data for ${player.name}")
                    }
                }
            } catch (e: Exception) {
                logger.severe("Error unloading player ${player.name}: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * Get cached player or load from database
     */
    suspend fun getQuestPlayer(playerId: UUID): QuestPlayer? {
        return playerCache[playerId] ?: run {
            val result = playerDatabase.findById(playerId)
            result.getOrNull()?.also { playerCache[playerId] = it }
        }
    }

    /**
     * Get quest progress for a player
     */
    suspend fun getProgress(player: QuestPlayer, questId: UUID): QuestProgress {
        val key = Pair(player.id, questId)
        return progressCache.getOrPut(key) {
            questStore.getProgress(player, questId)
        }
    }

    /**
     * Get progress display for a quest
     * Example: "■■■□□ 3/5"
     */
    suspend fun getProgressDisplay(
        player: QuestPlayer,
        questId: UUID,
        barLength: Int = 10
    ): String? {
        val quest = getQuest(questId) ?: return null
        val progress = getProgress(player, questId)

        if (progress is DefaultQuestProgress) {
            return progress.displayWithCount(
                key = questId.toString(),
                max = quest.requiredAmount,
                length = barLength
            )
        }

        return null
    }

    /**
     * Update quest progress
     */
    fun updateProgress(player: QuestPlayer, questId: UUID, amount: Int) {
        scope.launch {
            try {
                val progress = getProgress(player, questId)
                val currentAmount = progress.get(questId.toString())
                progress.increment(questId.toString(), amount)

                val key = Pair(player.id, questId)
                progressCache[key] = progress

                questStore.saveProgress(player, questId, progress)

                // Check if quest is completed
                val quest = getQuest(questId)
                if (quest != null && progress.get(questId.toString()) >= quest.requiredAmount) {
                    completeQuest(player, questId)
                }

                if (config.debug) {
                    logger.info("Updated progress for player ${player.id}, quest $questId: ${progress.get(questId.toString())}")
                }
            } catch (e: Exception) {
                logger.severe("Error updating progress: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    /**
     * Complete a quest for a player
     */
    private suspend fun completeQuest(player: QuestPlayer, questId: UUID) {
        try {
            questStore.completeQuest(player, questId)

            val quest = getQuest(questId)
            if (quest != null) {
                // Execute reward command if exists
                quest.rewardCommand?.let { command ->
                    withContext(Dispatchers.Main.immediate) {
                        plugin.server.dispatchCommand(
                            plugin.server.consoleSender,
                            command.replace("%player%", player.name)
                        )
                    }
                }

                if (config.debug) {
                    logger.info("Completed quest $questId for player ${player.id}")
                }
            }
        } catch (e: Exception) {
            logger.severe("Error completing quest: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Check if a player has completed a quest
     */
    suspend fun isQuestCompleted(player: QuestPlayer, questId: UUID): Boolean {
        return questStore.isCompleted(player, questId)
    }

    /**
     * Reload quests from config
     */
    fun reloadQuests() {
        config.reload()
        loadQuests()
        logger.info("Reloaded ${questCache.size} quests")
    }

    /**
     * Shutdown - save all cached data
     */
    fun shutdown() {
        runBlocking {
            // Save all players
            playerCache.values.forEach { player ->
                try {
                    playerDatabase.update(player)
                } catch (e: Exception) {
                    logger.severe("Error saving player ${player.id}: ${e.message}")
                }
            }

            // Save all progress
            progressCache.forEach { (key, progress) ->
                try {
                    progressDatabase.saveProgress(key.first, key.second, progress)
                } catch (e: Exception) {
                    logger.severe("Error saving progress: ${e.message}")
                }
            }

            if (config.debug) {
                logger.info("QuestManager shutdown complete")
            }
        }
    }

    companion object {
        /**
         * Create QuestManager with MongoDB implementations
         */
        fun create(
            plugin: JavaPlugin,
            config: QuestConfig,
            mongoPlayerDb: QuestPlayerDatabase,
            mongoProgressDb: QuestProgressDatabase,
            mongoStore: QuestStore
        ): QuestManager {
            return QuestManager(plugin, config, mongoPlayerDb, mongoProgressDb, mongoStore)
        }
    }
}