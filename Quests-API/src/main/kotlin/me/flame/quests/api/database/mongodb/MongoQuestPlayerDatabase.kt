package me.flame.quests.api.database.mongodb

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.flame.quests.api.database.QuestPlayerDatabase
import me.flame.quests.api.quest.entity.QuestPlayer
import org.bson.UuidRepresentation

import java.util.UUID

class MongoQuestPlayerDatabase private constructor(
    private val database: MongoDatabase
) : QuestPlayerDatabase {
    constructor(credentials: MongoCredentials) : this(
        database = MongoClients.create(
            MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(credentials.connectionString))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build()
        ).getDatabase(credentials.databaseName)
    )

    private val collection: MongoCollection<MongoQuestPlayer> =
        database.getCollection("quest_players", MongoQuestPlayer::class.java)

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
                name = this.name,
                progress = this.progress,
                completedQuests = this.completedQuests
            )
}