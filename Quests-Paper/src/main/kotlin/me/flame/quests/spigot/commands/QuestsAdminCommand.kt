package me.flame.quests.spigot.commands

import kotlinx.coroutines.launch
import me.flame.quests.api.quest.QuestManager
import me.flame.quests.spigot.QuestsPlugin
import me.flame.quests.spigot.minimessage.color
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import studio.mevera.imperat.annotations.Command
import studio.mevera.imperat.annotations.Permission
import studio.mevera.imperat.annotations.SubCommand

@Command(value = [ "questsadmin" ])
class QuestsAdminCommand(private val plugin: QuestsPlugin, private val questManager: QuestManager) {
    @SubCommand("reload")
    @Permission("firequests.admin.reload")
    fun onReloadCommand(sender: CommandSender) {
        questManager.reloadQuests()
    }

    @SubCommand("reset")
    @Permission("firequests.admin.resetprogress")
    fun onResetCommand(sender: CommandSender, player: Player, questId: String) {
        plugin.launch {
            val questPlayer = questManager.getQuestPlayer(player.uniqueId)
                ?: run {
                    plugin.logger.warning("Player ${player.name} not found in cache")
                    sender.sendMessage(color("<red>Player ${player.name} was not found."))
                    return@launch
                }

            questManager.resetProgress(questPlayer, questId)
        }
    }

    @SubCommand("setprogress")
    @Permission("firequests.admin.setprogress")
    fun onProgressSetCommand(sender: CommandSender, player: Player, questId: String, amount: Int) {
        plugin.launch {
            val questPlayer = questManager.getQuestPlayer(player.uniqueId)
                ?: run {
                    plugin.logger.warning("Player ${player.name} not found in cache")
                    sender.sendMessage(color("<red>Player ${player.name} was not found."))
                    return@launch
                }

            questManager.updateProgress(questPlayer, questId, amount)
        }
    }
}