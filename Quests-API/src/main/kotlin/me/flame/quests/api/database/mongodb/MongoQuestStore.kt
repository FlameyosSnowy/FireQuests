package me.flame.quests.api.database.mongodb

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates.set
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import me.flame.quests.api.database.QuestStore
import me.flame.quests.api.quest.progress.QuestProgress
import me.flame.quests.api.quest.QuestProgressEntry
import me.flame.quests.api.quest.entity.QuestPlayer
import org.bson.UuidRepresentation
import java.util.UUID

class MongoQuestStore private constructor(
    private val database: MongoDatabase
) : QuestStore {

    constructor(
        client: MongoClient,
        databaseName: String = "quests"
    ) : this(client.getDatabase(databaseName))

    constructor(credentials: MongoCredentials) : this(
        MongoClient.create(
            MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(credentials.connectionString))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build()
        ).getDatabase(credentials.databaseName)
    )

    private val progressCollection: MongoCollection<QuestProgressEntry> =
        database.getCollection("quest_progress")

    override suspend fun getProgress(
        player: QuestPlayer,
        questId: UUID
    ): QuestProgress? = progressCollection.find(and(eq("playerId", player.id), eq("questId", questId))).firstOrNull()?.progress

    override suspend fun saveProgress(
        player: QuestPlayer,
        questId: UUID,
        progress: QuestProgress
    ) {
        progressCollection.updateOne(
            and(
                eq("playerId", player.id),
                eq("questId", questId)
            ),
            set("progress", progress),
            UpdateOptions().upsert(true)
        )
    }

    override suspend fun completeQuest(
        player: QuestPlayer,
        questId: UUID
    ) {
        progressCollection.updateOne(
            and(eq("playerId", player.id), eq("questId", questId)),
            set("completed", true),
            UpdateOptions().upsert(true)
        )
    }

    override suspend fun isCompleted(
        player: QuestPlayer,
        questId: UUID
    ): Boolean =
        progressCollection.find(
            and(
                eq("playerId", player.id),
                eq("questId", questId),
                eq("completed", true)
            )
        ).firstOrNull() != null
}
