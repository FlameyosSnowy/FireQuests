package me.flame.quests.api.database

import kotlinx.coroutines.Deferred
import me.flame.quests.api.quest.Quest
import java.util.UUID

interface QuestDatabase {
    suspend fun fetchQuests(): List<Quest>

    suspend fun fetchQuest(id: UUID): Quest?

    suspend fun persistQuest(quest: Quest)

    suspend fun deleteQuest(id: UUID)
}
