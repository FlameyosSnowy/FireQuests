package me.flame.quests.api.quest

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import me.flame.quests.api.RewardExecutor
import me.flame.quests.api.database.QuestPlayerDatabase
import me.flame.quests.api.database.QuestStore
import me.flame.quests.api.quest.entity.QuestPlayer
import me.flame.quests.api.quest.progress.QuestProgress

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

class QuestManager(
    private val config: QuestSystemConfig,
    val playerDatabase: QuestPlayerDatabase,
    val questStore: QuestStore,
    private val commandRewardExecutor: RewardExecutor<CommandReward>,
    private val itemRewardExecutor: RewardExecutor<ItemReward>,
    val scope: CoroutineScope
) {
    private val logger = Logger.getLogger(javaClass.name)

    val questCache = ConcurrentHashMap<String, Quest>()
    val playerCache = ConcurrentHashMap<UUID, QuestPlayer>()
    val progressCache = ConcurrentHashMap<Pair<UUID, String>, QuestProgress>()

    private val completionGuard =
        ConcurrentHashMap<Pair<UUID, String>, Boolean>()

    init {
        reloadQuests()
    }

    fun reloadQuests() {
        questCache.clear()
        playerCache.clear()
        progressCache.clear()
        completionGuard.clear()

        config.quests.forEach { quest ->
            questCache[quest.id] = quest
        }

        linkQuestHierarchy(config.quests)

        if (config.debug) {
            logger.info("Loaded ${questCache.size} quests")
        }
    }

    fun linkQuestHierarchy(allQuests: Collection<Quest>) {
        val questMap = allQuests.associateBy { it.id }
        allQuests.forEach { quest ->
            quest.parentId?.let { pid ->
                questMap[pid]?.children?.add(quest)
            }
        }
    }


    fun getQuest(id: String): Quest? = questCache[id]

    fun getAllQuests(): List<Quest> = listOf(*questCache.values.toTypedArray())

    suspend inline fun loadPlayer(playerId: UUID, name: String, crossinline loader: (UUID, String) -> QuestPlayer) {
        val existing = playerDatabase.findById(playerId).getOrNull()
        val player = existing ?: loader(playerId, name)
        playerCache[playerId] = player
    }

    suspend fun unloadPlayer(playerId: UUID) {
        val player = playerCache.remove(playerId) ?: return
        playerDatabase.update(player)

        progressCache
            .filterKeys { it.first == playerId }
            .forEach { (key, progress) ->
                questStore.saveProgress(player, key.second, progress)
                progressCache.remove(key)
            }
    }

    suspend fun getProgress(player: QuestPlayer, questId: String): QuestProgress? {
        val key = player.id to questId
        return progressCache.getOrPut(key) {
            questStore.getProgress(player, questId)
        }
    }

    suspend fun updateProgress(player: QuestPlayer, questId: String, amount: Int) {
        val quest = getQuest(questId) ?: return
        val progress = getProgress(player, questId) ?: return

        val current = progress.get(questId)
        if (current >= quest.requiredAmount) return

        val newValue = (current + amount).coerceAtMost(quest.requiredAmount)
        progress.set(questId, newValue)

        questStore.saveProgress(player, questId, progress)

        if (newValue == quest.requiredAmount) {
            completeQuest(player, quest)
        }
    }

    fun resetProgress(player: QuestPlayer, questId: String) {
        scope.launch {
            val quest = getQuest(questId) ?: return@launch
            val progress = getProgress(player, questId) ?: return@launch

            val current = progress.get(questId)
            if (current >= quest.requiredAmount) return@launch

            progress.set(questId, 0)

            questStore.saveProgress(player, questId, progress)
        }
    }

    private suspend fun completeQuest(player: QuestPlayer, quest: Quest) {
        val key = player.id to quest.id

        if (completionGuard.putIfAbsent(key, true) != null) return

        val completedNow = questStore.completeQuestOnce(player, quest.id)
        if (!completedNow) return

        withContext(Dispatchers.Main) {
            quest.rewards.forEach { reward ->
                when (reward) {
                    is CommandReward -> commandRewardExecutor.give(player, reward)
                    is ItemReward -> itemRewardExecutor.give(player, reward)
                }
            }
        }

        quest.parentId?.let { parentId ->
            tryCompleteParent(player, parentId)
        }
    }

    private suspend fun tryCompleteParent(player: QuestPlayer, parentId: String) {
        val parent = getQuest(parentId) ?: return

        val allChildrenCompleted = parent.children.all { child ->
            questStore.isCompleted(player, child.id)
        }

        if (!allChildrenCompleted) return

        completeQuest(player, parent)
    }

    suspend fun isQuestCompleted(player: QuestPlayer, questId: String): Boolean =
        questStore.isCompleted(player, questId)

    suspend fun getQuestPlayer(playerId: UUID): QuestPlayer? {
        return playerCache[playerId] ?: run {
            val result = playerDatabase.findById(playerId)
            result.getOrNull()?.also { playerCache[playerId] = it }
        }
    }

    suspend inline fun getOrCreateQuestPlayer(playerId: UUID, crossinline default: () -> QuestPlayer): QuestPlayer {
        return playerCache[playerId] ?: run {
            val result = playerDatabase.findById(playerId)
            val player = result.getOrNull() ?: run {
                playerDatabase.insert(default())
                playerCache[playerId] = default()
                return default()
            }
            player.also { playerCache[playerId] = it }
        }
    }

    suspend fun unloadAllPlayers() {
        playerCache.forEach { (_, player) ->
            unloadPlayer(player.id)
        }
    }

}
