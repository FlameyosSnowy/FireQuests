package me.flame.quests.api.database.mongodb

import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import me.flame.quests.api.database.QuestPlayerDatabase
import me.flame.quests.api.quest.entity.QuestPlayer

import java.util.UUID

class MongoQuestPlayerDatabase(
    client: MongoClient,
    databaseName: String = "quests"
) : QuestPlayerDatabase {

    private val collection: MongoCollection<MongoQuestPlayer> =
        client.getDatabase(databaseName)
            .getCollection("quest_players")

    override suspend fun findById(id: UUID): Result<QuestPlayer?> = runCatching {
        withContext(Dispatchers.IO) {
            collection.find(eq("_id", id)).firstOrNull()
        }
    }

    override suspend fun findAll(): Result<List<QuestPlayer>> = runCatching {
        withContext(Dispatchers.IO) {
            collection.find().toList()
        }
    }

    override suspend fun insert(player: QuestPlayer): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            collection.insertOne(player.toMongo())
        }
    }
    override suspend fun update(player: QuestPlayer): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            collection.replaceOne(eq("_id", player.id), player.toMongo())
        }
    }

    override suspend fun delete(id: UUID): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            collection.deleteOne(eq("_id", id))
        }
    }

    private fun QuestPlayer.toMongo(): MongoQuestPlayer =
        this as? MongoQuestPlayer
            ?: MongoQuestPlayer(
                _id = this.id,
                progress = this.progress,
                completedQuests = this.completedQuests
            )
}