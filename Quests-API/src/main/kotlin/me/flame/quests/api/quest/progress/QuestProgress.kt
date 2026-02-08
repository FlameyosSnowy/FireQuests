package me.flame.quests.api.quest.progress

import me.flame.quests.api.quest.QuestKey

interface QuestProgress {
    fun get(key: String): Int
    fun set(key: String, value: Int)
    fun increment(key: String, amount: Int = 1)

    fun getSet(key: String): MutableSet<QuestKey>
    fun addToSet(key: String, value: QuestKey)
}