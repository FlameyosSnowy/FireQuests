package me.flame.quests.spigot.listeners

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.flame.quests.api.database.mongodb.MongoQuestPlayer
import me.flame.quests.api.quest.QuestManager
import me.flame.quests.api.quest.progress.DefaultQuestProgress
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerConnectionStateListener(private val scope: CoroutineScope, private val questManager: QuestManager) : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
       scope.launch {
           val uuid = event.player.uniqueId
           val name = event.player.name
           val player = questManager.getOrCreateQuestPlayer(uuid) {
               MongoQuestPlayer(uuid, name, DefaultQuestProgress(), setOf())
           }

           questManager.loadPlayer(uuid, name) { _, _ ->
               player
           }
       }
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
       scope.launch {
           questManager.unloadPlayer(event.player.uniqueId)
       }
    }
}