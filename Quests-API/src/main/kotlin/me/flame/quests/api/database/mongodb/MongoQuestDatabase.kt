package me.flame.quests.api.database.mongodb

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.mongodb.client.model.Filters.eq
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import me.flame.quests.api.database.QuestDatabase
import me.flame.quests.api.quest.Quest
import org.bson.UuidRepresentation
import java.util.UUID

class MongoQuestDatabase private constructor(
    private val database: MongoDatabase
) : QuestDatabase {
    constructor(
        client: MongoClient,
        databaseName: String = "quests"
    ) : this(
        database = client.getDatabase(databaseName)
    )

    private val quests: MongoCollection<Quest> =
        database.getCollection("quests")

    constructor(credentials: MongoCredentials) : this(
        database = MongoClient.create(
            MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(credentials.connectionString))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build()
        ).getDatabase(credentials.databaseName)
    )

    override suspend fun fetchQuests(): List<Quest> =
        quests.find().toList()

    override suspend fun fetchQuest(id: UUID): Quest? =
        quests.find(eq("_id", id)).firstOrNull()

    override suspend fun persistQuest(quest: Quest) {
        quests.insertOne(quest)
    }

    override suspend fun deleteQuest(id: UUID) {
        quests.deleteOne(eq("_id", id))
    }
}