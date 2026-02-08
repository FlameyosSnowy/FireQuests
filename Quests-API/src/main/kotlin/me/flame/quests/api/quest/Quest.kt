package me.flame.quests.api.quest

import me.flame.quests.api.GuiItemSpec

data class Quest(
    val id: String,
    val type: String,
    val target: String,
    val requiredAmount: Int,
    val rewards: List<QuestReward>,
    val displayItem: GuiItemSpec,
    val parentId: String? = null,
    val children: MutableList<Quest> = mutableListOf()
)