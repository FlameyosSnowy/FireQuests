package me.flame.quests.api.quest.payload

import me.flame.quests.api.quest.QuestKey

data class BlockMinedPayload(
    val block: QuestKey
) : QuestPayload