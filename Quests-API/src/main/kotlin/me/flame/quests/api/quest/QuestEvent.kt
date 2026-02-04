package me.flame.quests.api.quest

data class QuestEvent(
    val type: QuestEventType,
    val filters: MutableMap<String, String>,
    val progress: QuestProgress
)