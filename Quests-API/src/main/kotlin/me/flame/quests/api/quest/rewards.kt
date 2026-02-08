package me.flame.quests.api.quest

sealed interface QuestReward {
    val type: Type

    enum class Type {
        COMMAND,
        ITEM
    }
}

data class CommandReward(
    val command: String
) : QuestReward {
    override val type: QuestReward.Type = QuestReward.Type.COMMAND
}

data class ItemReward(
    val itemId: String,          // key into config
    val amount: Int
) : QuestReward {
    override val type: QuestReward.Type = QuestReward.Type.ITEM
}
