package me.flame.quests.spigot.commands

import io.github.mqzen.menus.Lotus
import io.github.mqzen.menus.base.pagination.Pagination
import io.github.mqzen.menus.base.pagination.exception.InvalidPageException

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

import me.flame.quests.spigot.QuestsPlugin
import me.flame.quests.spigot.gui.QuestsAutoPage

import org.bukkit.entity.Player

import studio.mevera.imperat.annotations.Command
import studio.mevera.imperat.annotations.Usage

@Command(value = [ "quests" ])
class QuestsCommand(val plugin: QuestsPlugin, val lotus: Lotus) {
    @Usage
    fun onQuestsCommand(player: Player) {
        plugin.launch {
            val (data, quests) = coroutineScope {
                val dataDeferred = async {
                    plugin.questsManager.getQuestPlayer(player.uniqueId)
                }
                val questsDeferred = async {
                    plugin.questsManager.getAllQuests()
                }

                dataDeferred.await() to questsDeferred.await()
            }

            val questData = data ?: return@launch

            val pagination = Pagination.auto(lotus)
                .creator(QuestsAutoPage(questData, quests))
                .build()

            try {
                pagination.open(player)
            } catch (_: InvalidPageException) {
                throw RuntimeException("Failed to open pagination due to invalid pages or no pages.")
            }
        }
    }
}