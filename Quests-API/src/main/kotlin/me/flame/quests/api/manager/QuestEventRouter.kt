package me.flame.quests.api.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.flame.quests.api.quest.QuestEventType
import me.flame.quests.api.quest.entity.QuestPlayer

class QuestEventRouter(
    private val scope: CoroutineScope
) {
    fun dispatch(type: QuestEventType, player: QuestPlayer) {
        scope.launch {
            println("Dispatching $type for $player")
        }
    }
}
