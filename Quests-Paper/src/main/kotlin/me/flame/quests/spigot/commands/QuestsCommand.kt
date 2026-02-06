package me.flame.quests.spigot.commands

import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.flame.quests.api.database.QuestDatabase
import me.flame.quests.api.database.QuestPlayerDatabase
import me.flame.quests.spigot.QuestsPlugin

import org.bukkit.entity.Player

import studio.mevera.imperat.annotations.Command
import studio.mevera.imperat.annotations.Usage

@Command(value = [ "quests" ])
class QuestsCommand(val plugin: QuestsPlugin) {

    @Usage
    fun onQuestsCommand(player: Player) {
        val deferredData = plugin.async {
            plugin.questsManager.getQuestPlayer(player.uniqueId)
        }

        val deferredQuests = plugin.async {
            plugin.questsManager.getAllQuests()
        }

        plugin.launch {
            val (data, quests) = deferredData.await() to deferredQuests.await()

        }
    }
}