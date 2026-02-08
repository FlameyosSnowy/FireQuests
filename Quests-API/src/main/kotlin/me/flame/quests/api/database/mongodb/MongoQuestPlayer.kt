package me.flame.quests.api.database.mongodb

import me.flame.quests.api.quest.progress.DefaultQuestProgress
import me.flame.quests.api.quest.entity.QuestPlayer
import java.util.UUID

data class MongoQuestPlayer(
    val _id: UUID,
    override val name: String,
    override val progress: DefaultQuestProgress,
    override val completedQuests: Set<UUID>
) : QuestPlayer {
    override val id: UUID get() = _id
}