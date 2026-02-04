package me.flame.quests.api.quest

import java.util.UUID

data class Quest(
    val id: UUID,
    val type: String,
    val target: String,
    val requiredAmount: Int,
    val rewardCommand: String?,
    val rewardItems: List<*>
)