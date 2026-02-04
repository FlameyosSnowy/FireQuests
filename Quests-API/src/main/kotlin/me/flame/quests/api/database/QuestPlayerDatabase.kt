package me.flame.quests.api.database

import me.flame.quests.api.quest.entity.QuestPlayer
import java.util.UUID

interface QuestPlayerDatabase {

    suspend fun findById(id: UUID): Result<QuestPlayer?>

    suspend fun findAll(): Result<List<QuestPlayer>>

    suspend fun insert(player: QuestPlayer): Result<Unit>

    suspend fun update(player: QuestPlayer): Result<Unit>

    suspend fun delete(id: UUID): Result<Unit>
}
