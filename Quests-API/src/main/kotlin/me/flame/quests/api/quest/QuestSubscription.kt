package me.flame.quests.api.quest

import me.flame.quests.api.quest.progress.ProgressChanger
import java.util.UUID
import java.util.function.Predicate

class QuestSubscription(
    val questId: UUID,
    val filter: Predicate<QuestEventContext>,
    val changer: ProgressChanger
) {
    fun matches(ctx: QuestEventContext): Boolean {
        return filter.test(ctx)
    }

    suspend fun apply(ctx: QuestEventContext) {
        changer.apply(ctx.player, questId, ctx.payload)
    }
}