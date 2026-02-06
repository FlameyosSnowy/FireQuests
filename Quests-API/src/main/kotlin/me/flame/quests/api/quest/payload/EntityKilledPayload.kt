package me.flame.quests.api.quest.payload

import me.flame.quests.api.quest.QuestKey

data class EntityKilledPayload(
    val victim: QuestKey
) : QuestPayload
