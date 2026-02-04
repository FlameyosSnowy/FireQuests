package me.flame.quests.api.database

import me.flame.quests.api.quest.QuestProgress
import me.flame.quests.api.quest.entity.QuestPlayer
import java.util.UUID

interface QuestStore {
    suspend fun getProgress(
        player: QuestPlayer,
        questId: UUID
    ): QuestProgress

    suspend fun saveProgress(
        player: QuestPlayer,
        questId: UUID,
        progress: QuestProgress
    )

    suspend fun completeQuest(
        player: QuestPlayer,
        questId: UUID
    )

    suspend fun isCompleted(
        player: QuestPlayer,
        questId: UUID
    ): Boolean
}
