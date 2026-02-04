package me.flame.quests.api.quest

import org.bukkit.entity.Player

interface QuestReward {
    fun rewardPlayer(player: Player)
}