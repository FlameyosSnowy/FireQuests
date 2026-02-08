package me.flame.quests.api.database

import me.flame.quests.api.quest.progress.QuestProgress
import me.flame.quests.api.quest.entity.QuestPlayer
import java.util.UUID

interface QuestStore {
    suspend fun getProgress(
        player: QuestPlayer,
        questId: String
    ): QuestProgress?

    suspend fun saveProgress(
        player: QuestPlayer,
        questId: String,
        progress: QuestProgress
    )

    suspend fun completeQuest(
        player: QuestPlayer,
        questId: String
    )

    suspend fun isCompleted(
        player: QuestPlayer,
        questId: String
    ): Boolean

    suspend fun completeQuestOnce(
        player: QuestPlayer,
        questId: String
    ): Boolean
}
