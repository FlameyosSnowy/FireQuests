package me.flame.quests.api.quest

data class QuestSystemConfig(
    val quests: List<Quest>,
    val debug: Boolean = false
)
