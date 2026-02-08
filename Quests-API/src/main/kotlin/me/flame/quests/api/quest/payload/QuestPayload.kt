package me.flame.quests.api.quest.payload

interface QuestPayload {
    /**
     * Checks if this payload matches the quest target
     */
    fun matchesTarget(target: String): Boolean

    /**
     * Returns the amount of progress this payload represents
     */
    val increment: Int
}