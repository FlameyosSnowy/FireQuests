package me.flame.quests.api.quest.entity

import me.flame.quests.api.quest.progress.DefaultQuestProgress
import java.util.UUID

interface QuestPlayer {
    val id: UUID

    val name: String

    val progress: DefaultQuestProgress

    val completedQuests: Set<UUID>
}
