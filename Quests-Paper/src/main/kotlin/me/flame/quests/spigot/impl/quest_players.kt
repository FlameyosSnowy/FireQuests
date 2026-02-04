package me.flame.quests.spigot.impl

import me.flame.quests.api.quest.entity.QuestPlayer
import org.bukkit.entity.Player

fun Player.asQuestPlayer(): QuestPlayer {
    return SpigotQuestPlayer(this)
}