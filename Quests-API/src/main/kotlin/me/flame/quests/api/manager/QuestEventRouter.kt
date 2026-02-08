package me.flame.quests.api.manager

import me.flame.quests.api.quest.QuestEventContext
import me.flame.quests.api.quest.QuestEventType
import me.flame.quests.api.quest.QuestSubscription

import java.util.*

class QuestEventRouter(val routes: MutableMap<QuestEventType, MutableList<QuestSubscription>> =
                           Collections.synchronizedMap(EnumMap<QuestEventType, MutableList<QuestSubscription>>(QuestEventType::class.java))
) {

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