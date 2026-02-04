package me.flame.quests.spigot.impl

import me.flame.quests.api.quest.entity.QuestPlayer
import org.bukkit.entity.Player

class SpigotQuestPlayer(
    private val player: Player,
    private val delegate: QuestPlayer
) : QuestPlayer by delegate {
    val bukkitPlayer: Player get() = player
}