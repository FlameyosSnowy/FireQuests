package me.flame.quests.api.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import me.flame.quests.api.quest.QuestEventContext
import me.flame.quests.api.quest.QuestEventType
import me.flame.quests.api.quest.QuestSubscription
import me.flame.quests.api.quest.entity.QuestPlayer
import java.util.*

class QuestEventRouter(val routes: MutableMap<QuestEventType, MutableList<QuestSubscription>> =
                           EnumMap<QuestEventType, MutableList<QuestSubscription>>(QuestEventType::class.java),
                       parentScope: CoroutineScope) {
    private val scope = CoroutineScope(
        parentScope.coroutineContext + SupervisorJob()
    )

    fun register(type: QuestEventType, subscription: QuestSubscription) {
        routes.computeIfAbsent(type) { k: QuestEventType? -> ArrayList<QuestSubscription>() }.add(subscription)
    }

    suspend fun dispatch(type: QuestEventType, ctx: QuestEventContext) {
        val subs: MutableList<QuestSubscription> = routes[type] ?: return

        for (sub in subs) {
            if (sub.matches(ctx)) {
                sub.apply(ctx)
            }
        }
    }

    fun clear() {
        routes.clear()
    }
}