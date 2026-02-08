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

class MongoQuestStore private constructor(
    database: MongoDatabase
) : QuestStore {
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
        questId: String
    ): QuestProgress? = progressCollection.find(and(eq("playerId", player.id), eq("questId", questId))).firstOrNull()?.progress

    override suspend fun saveProgress(
        player: QuestPlayer,
        questId: String,
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
        questId: String
    ) {
        progressCollection.updateOne(
            and(eq("playerId", player.id), eq("questId", questId)),
            set("completed", true),
            UpdateOptions().upsert(true)
        )
    }

    override suspend fun completeQuestOnce(
        player: QuestPlayer,
        questId: String
    ): Boolean {
        val filter = and(
            eq("playerId", player.id),
            eq("questId", questId),
            eq("completed", false)
        )

        val updateResult = progressCollection.updateOne(
            filter,
            set("completed", true),
            UpdateOptions().upsert(true)
        )

        return updateResult.matchedCount > 0 || updateResult.upsertedId != null
    }

    override suspend fun isCompleted(
        player: QuestPlayer,
        questId: String
    ): Boolean =
        progressCollection.find(
            and(
                eq("playerId", player.id),
                eq("questId", questId),
                eq("completed", true)
            )
        ).firstOrNull() != null
}
