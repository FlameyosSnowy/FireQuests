package me.flame.quests.spigot.listeners

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.flame.quests.api.manager.QuestEventRouter
import me.flame.quests.api.quest.QuestEventType
import me.flame.quests.spigot.impl.asQuestPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent


class EntityDeathListener(private val questRouter: QuestEventRouter, private val scope: CoroutineScope) : Listener {
    @EventHandler
    fun onEntityKill(event: EntityDeathEvent) {
        val player = event.entity.killer ?: return

        scope.launch {
            questRouter.dispatch(QuestEventType.ENTITY_KILLED, player.asQuestPlayer())
        }
    }
}