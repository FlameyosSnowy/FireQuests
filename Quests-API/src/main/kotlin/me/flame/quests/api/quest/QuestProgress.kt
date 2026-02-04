package me.flame.quests.api.quest

interface QuestProgress {

    fun increment(key: String, amount: Int): Int

    fun get(key: String): Int

    fun set(key: String, value: Int)

    fun snapshot(): Map<String, Int>
}
