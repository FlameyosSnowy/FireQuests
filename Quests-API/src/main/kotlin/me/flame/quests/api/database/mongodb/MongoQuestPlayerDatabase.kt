package me.flame.quests.api.database.mongodb

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import me.flame.quests.api.database.QuestPlayerDatabase
import me.flame.quests.api.quest.entity.QuestPlayer
import org.bson.UuidRepresentation

import java.util.UUID

class MongoQuestPlayerDatabase private constructor(
    private val database: MongoDatabase
) : QuestPlayerDatabase {
    constructor(
        client: MongoClient,
        databaseName: String = "quests"
    ) : this(
        database = client.getDatabase(databaseName)
    )

    constructor(credentials: MongoCredentials) : this(
        database = MongoClient.create(
            MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(credentials.connectionString))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build()
        ).getDatabase(credentials.databaseName)
    )

    private val collection: MongoCollection<MongoQuestPlayer> =
        database.getCollection("quest_players")

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