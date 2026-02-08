package me.flame.quests.api.quest

import me.flame.quests.api.quest.entity.QuestPlayer
import me.flame.quests.api.quest.payload.QuestPayload

@JvmRecord
data class QuestEventContext(
    val player: QuestPlayer,
    val questKey: QuestKey,
    val payload: QuestPayload
)