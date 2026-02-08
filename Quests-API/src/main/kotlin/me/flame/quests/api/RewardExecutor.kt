package me.flame.quests.api

import me.flame.quests.api.quest.QuestReward
import me.flame.quests.api.quest.entity.QuestPlayer

interface RewardExecutor<R : QuestReward> {
    fun give(player: QuestPlayer, reward: R)
}
