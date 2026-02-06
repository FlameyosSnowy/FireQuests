package me.flame.quests.api.quest

import me.flame.quests.api.quest.progress.DefaultQuestProgress
import java.util.UUID

@JvmRecord
data class QuestProgressEntry(
    val playerId: UUID,
    val questId: UUID,
    val progress: DefaultQuestProgress,
    val completed: Boolean
)
