package me.flame.quests.api.quest

import me.flame.quests.api.quest.progress.QuestProgress

@JvmRecord
data class QuestEvent(
    val type: QuestEventType,
    val filters: MutableMap<String, String>,
    val progress: QuestProgress
)