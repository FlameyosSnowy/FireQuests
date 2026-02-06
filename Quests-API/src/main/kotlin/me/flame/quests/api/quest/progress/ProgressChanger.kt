package me.flame.quests.api.quest.progress

import me.flame.quests.api.quest.entity.QuestPlayer
import java.util.UUID

interface ProgressChanger {
    suspend fun apply(player: QuestPlayer, questId: UUID, payload: Any)
}