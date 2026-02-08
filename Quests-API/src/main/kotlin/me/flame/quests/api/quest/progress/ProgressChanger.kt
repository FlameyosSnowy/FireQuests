package me.flame.quests.api.quest.progress

import me.flame.quests.api.quest.entity.QuestPlayer
import me.flame.quests.api.quest.payload.QuestPayload
import java.util.UUID

interface ProgressChanger {
    suspend fun apply(player: QuestPlayer, questId: String, payload: QuestPayload)
}