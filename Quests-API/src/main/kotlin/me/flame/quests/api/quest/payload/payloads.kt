package me.flame.quests.api.quest.payload

import me.flame.quests.api.quest.QuestKey

data class EntityKilledPayload(val victim: QuestKey) : QuestPayload {
    override fun matchesTarget(target: String) = victim.value == target
    override val increment: Int = 1
}

data class BlockMinedPayload(val block: QuestKey) : QuestPayload {
    override fun matchesTarget(target: String) = block.value == target
    override val increment: Int = 1
}

data class DistanceWalkedPayload(override val increment: Int) : QuestPayload {
    override fun matchesTarget(target: String) = true
}

data class VisitedBiomePayload(val biome: QuestKey) : QuestPayload {
    override fun matchesTarget(target: String) = biome.value == target
    override val increment: Int = 1
}
