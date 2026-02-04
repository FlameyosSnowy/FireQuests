package me.flame.quests.api.quest.entity

import me.flame.quests.api.quest.QuestProgress
import java.util.UUID

interface QuestPlayer {
    val id: UUID

    val name: String

    val progress: QuestProgress

    val completedQuests: Set<UUID>
}
