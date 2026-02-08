package me.flame.quests.api.quest

interface QuestKey {
    val value: String

    companion object {
        fun none(): QuestKey {
            return object : QuestKey {
                override val value: String = ""
            }
        }
    }
}
