package me.flame.quests.spigot.rewards

import me.flame.quests.api.RewardExecutor
import me.flame.quests.api.quest.CommandReward
import me.flame.quests.api.quest.entity.QuestPlayer
import org.bukkit.Bukkit

class BukkitCommandRewardExecutor : RewardExecutor<CommandReward> {
    override fun give(player: QuestPlayer, reward: CommandReward) {
        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            reward.command.replace("%player%", player.name)
        )
    }
}
