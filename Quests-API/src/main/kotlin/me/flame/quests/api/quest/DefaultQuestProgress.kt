package me.flame.quests.api.quest

/**
 * Default implementation of QuestProgress
 */
data class DefaultQuestProgress(
    private val progressMap: MutableMap<String, Int> = mutableMapOf()
) : QuestProgress {

    override fun increment(key: String, amount: Int): Int {
        val current = progressMap.getOrDefault(key, 0)
        val newValue = current + amount
        progressMap[key] = newValue
        return newValue
    }

    override fun get(key: String): Int {
        return progressMap.getOrDefault(key, 0)
    }

    override fun set(key: String, value: Int) {
        progressMap[key] = value
    }

    override fun snapshot(): Map<String, Int> {
        return progressMap.toMap()
    }

    /**
     * Display progress as a visual bar
     * @param key The progress key
     * @param max The maximum value (required amount)
     * @param length The length of the bar (default 10)
     * @param filled The character for filled portions (default ■)
     * @param empty The character for empty portions (default □)
     */
    fun display(
        key: String, 
        max: Int, 
        length: Int = 10,
        filled: Char = '■',
        empty: Char = '□'
    ): String {
        val current = get(key)
        val percentage = if (max > 0) current.toDouble() / max else 0.0
        val filledCount = (percentage * length).toInt().coerceIn(0, length)
        val emptyCount = length - filledCount
        
        return buildString {
            repeat(filledCount) { append(filled) }
            repeat(emptyCount) { append(empty) }
        }
    }

    /**
     * Display progress with count
     * Example: "■■■□□ 3/5"
     */
    fun displayWithCount(
        key: String,
        max: Int,
        length: Int = 10,
        filled: Char = '■',
        empty: Char = '□'
    ): String {
        val current = get(key)
        return "${display(key, max, length, filled, empty)} $current/$max"
    }

    fun copy(): DefaultQuestProgress {
        return DefaultQuestProgress(progressMap.toMutableMap())
    }

    companion object {
        /**
         * Create from a snapshot map
         */
        fun fromSnapshot(snapshot: Map<String, Int>): DefaultQuestProgress {
            return DefaultQuestProgress(snapshot.toMutableMap())
        }
    }
}