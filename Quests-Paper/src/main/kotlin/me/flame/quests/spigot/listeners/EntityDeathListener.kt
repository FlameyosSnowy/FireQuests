package me.flame.quests.spigot.listeners

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

import me.flame.quests.api.manager.QuestEventRouter
import me.flame.quests.api.quest.QuestEventContext
import me.flame.quests.api.quest.QuestEventType
import me.flame.quests.api.quest.payload.EntityKilledPayload
import me.flame.quests.spigot.impl.EntityKey
import me.flame.quests.spigot.manager.QuestManager

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class EntityDeathListener(private val questRouter: QuestEventRouter, private val questManager: QuestManager, private val scope: CoroutineScope) : Listener {
    @EventHandler
    fun onEntityKill(event: EntityDeathEvent) {
        val player = event.entity.killer ?: return
        val entity = event.entity.type

        scope.launch {
            questManager.getQuestPlayer(player.uniqueId)?.let {
                questRouter.dispatch(
                    QuestEventType.ENTITY_KILLED,
                    QuestEventContext(it, EntityKey.ZOMBIE, EntityKilledPayload(EntityKey.from(entity)))
                )
            }
        }
    }
}