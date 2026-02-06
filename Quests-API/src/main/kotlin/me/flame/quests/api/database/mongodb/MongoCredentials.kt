package me.flame.quests.api.database.mongodb

@JvmRecord
data class MongoCredentials(val connectionString: String, val databaseName: String)