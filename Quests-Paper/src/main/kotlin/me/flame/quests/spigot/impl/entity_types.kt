package me.flame.quests.spigot.impl

import me.flame.quests.api.quest.QuestKey
import org.bukkit.entity.EntityType

enum class EntityKey : QuestKey {
    ZOMBIE,
    SKELETON;

    override val value: String get() = name

    companion object {
        fun from(entity: EntityType): QuestKey {
            return when (entity) {
                EntityType.ZOMBIE -> ZOMBIE
                EntityType.SKELETON -> SKELETON
                else -> throw IllegalArgumentException("Unknown entity type: $entity")
            }
        }
    }
}
