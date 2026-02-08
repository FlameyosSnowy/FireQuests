package me.flame.quests.spigot.impl

import me.flame.quests.api.quest.QuestKey
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockType
import org.bukkit.entity.EntityType

fun fromEntity(entity: EntityType): QuestKey {
    return fromNamespacedKey(entity.key)
}

fun fromBlock(entity: Material): QuestKey {
    return fromNamespacedKey(entity.key)
}

fun fromNamespacedKey(key: NamespacedKey): QuestKey {
    return QuestNamespacedKey(key)
}

class QuestNamespacedKey(key: NamespacedKey) : QuestKey {
    override val value: String = key.key
}