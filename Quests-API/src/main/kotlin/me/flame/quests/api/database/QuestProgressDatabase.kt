package me.flame.quests.api.database

import me.flame.quests.api.quest.QuestProgress
import java.util.UUID

interface QuestProgressDatabase {

    suspend fun loadProgress(
        playerId: UUID,
        questId: UUID
    ): QuestProgress?

    suspend fun saveProgress(
        playerId: UUID,
        questId: UUID,
        progress: QuestProgress
    )

    suspend fun markCompleted(
        playerId: UUID,
        questId: UUID
    )

    suspend fun isCompleted(
        playerId: UUID,
        questId: UUID
    ): Boolean
}
