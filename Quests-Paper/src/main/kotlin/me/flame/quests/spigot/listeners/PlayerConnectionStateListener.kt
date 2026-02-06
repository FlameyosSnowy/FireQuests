package me.flame.quests.spigot.listeners

import me.flame.quests.spigot.manager.QuestManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerConnectionStateListener(val questManager: QuestManager) : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
       questManager.loadPlayer(event.player)
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
       questManager.unloadPlayer(event.player)
    }
}