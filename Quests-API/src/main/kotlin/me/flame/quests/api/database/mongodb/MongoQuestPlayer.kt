package me.flame.quests.api.database.mongodb

import me.flame.quests.api.quest.progress.DefaultQuestProgress
import me.flame.quests.api.quest.progress.QuestProgress
import me.flame.quests.api.quest.entity.QuestPlayer
import java.util.UUID

@JvmRecord
data class MongoQuestPlayer(
    val _id: UUID,
    override val name: String,
    override val progress: QuestProgress,
    override val completedQuests: Set<UUID>
) : QuestPlayer {
    override val id: UUID get() = _id

    /**
     * Create a copy with a new progress snapshot
     */
    fun withProgress(newProgress: QuestProgress): MongoQuestPlayer {
        return copy(progress = newProgress)
    }

    /**
     * Create a copy with an additional completed quest
     */
    fun withCompletedQuest(questId: UUID): MongoQuestPlayer {
        return copy(completedQuests = completedQuests + questId)
    }

    companion object {
        /**
         * Create a new MongoQuestPlayer with default values
         */
        fun create(
            id: UUID,
            name: String,
            progressSnapshot: Map<String, Int> = emptyMap(),
            completedQuests: Set<UUID> = emptySet()
        ): MongoQuestPlayer {
            return MongoQuestPlayer(
                _id = id,
                name = name,
                progress = DefaultQuestProgress.fromSnapshot(progressSnapshot),
                completedQuests = completedQuests
            )
        }
    }
}