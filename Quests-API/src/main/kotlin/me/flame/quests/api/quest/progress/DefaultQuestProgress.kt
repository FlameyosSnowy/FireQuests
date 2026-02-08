package me.flame.quests.api.quest.progress

import me.flame.quests.api.quest.QuestKey
import java.util.concurrent.ConcurrentHashMap

/**
 * Default implementation of QuestProgress
 */
class DefaultQuestProgress(
    private val numbers: MutableMap<String, Int> = ConcurrentHashMap(),
    private val sets: MutableMap<String, MutableSet<QuestKey>> = ConcurrentHashMap()
) : QuestProgress {

    override fun get(key: String): Int = numbers[key] ?: 0

    override fun set(key: String, value: Int) {
        numbers[key] = value
    }

    override fun increment(key: String, amount: Int) {
        numbers[key] = get(key) + amount
    }

    override fun getSet(key: String): MutableSet<QuestKey> =
        sets.computeIfAbsent(key) { mutableSetOf() }

    override fun addToSet(key: String, value: QuestKey) {
        sets.computeIfAbsent(key) { mutableSetOf() }.add(value)
    }

    companion object {
        fun fromSnapshot(snapshot: Map<String, Int>): DefaultQuestProgress {
            val progress = DefaultQuestProgress()
            snapshot.forEach { (k, v) -> progress.set(k, v) }
            return progress
        }
    }
}