package me.flame.quests.spigot.rewards

import me.flame.quests.api.RewardExecutor
import me.flame.quests.api.quest.ItemReward
import me.flame.quests.api.quest.entity.QuestPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemRewardExecutor : RewardExecutor<ItemReward> {
    override fun give(player: QuestPlayer, reward: ItemReward) {
        val item = ItemStack.of(Material.valueOf(reward.itemId), reward.amount)
        Bukkit.getPlayer(player.id)?.inventory?.addItem(item)
    }
}
