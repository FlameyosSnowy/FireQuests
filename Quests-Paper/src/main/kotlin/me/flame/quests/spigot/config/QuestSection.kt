package me.flame.quests.spigot.config

import me.flame.quests.api.quest.CommandReward
import me.flame.quests.api.quest.ItemReward
import me.flame.quests.api.quest.Quest
import me.flame.quests.api.quest.QuestReward
import org.bukkit.configuration.ConfigurationSection
import kotlin.collections.get

class QuestSection(
    override var config: ConfigurationSection
) : Config() {

    val id by string("id")
    val type by string("type")
    val target by string("target")
    val requiredAmount by int("required-amount", def = 1)

    // Optional parent quest
    val parentId by string("parent-id", def = "")

    // Optional children quests
    val childrenIds: List<String>
        get() = config.getStringList("children-ids")

    val rewards: List<QuestReward>
        get() = config.getMapList("rewards").map { map ->
            when (map["type"]) {
                "COMMAND" -> CommandReward(map["command"] as String)
                "ITEM" -> {
                    val item = map["item"] as Map<*, *>
                    ItemReward(itemId = item["id"] as String, amount = item["amount"] as Int)
                }
                else -> error("Unknown reward type: ${map["type"]}")
            }
        }

    val guiItem: GuiItemSection
        get() = GuiItemSection(
            config.getConfigurationSection("gui-item")
                ?: error("gui-item section not found")
        )

    fun toQuest(questIndex: Map<String, QuestSection>, built: MutableMap<String, Quest> = mutableMapOf()): Quest {
        built[id]?.let { return it }

        val quest = Quest(
            id = id,
            type = type,
            target = target,
            requiredAmount = requiredAmount,
            rewards = rewards,
            displayItem = guiItem.buildGuiItemSpec(),
            parentId = parentId.ifEmpty { null }
        )
        built[id] = quest

        quest.children.addAll(childrenIds.mapNotNull { childId ->
            questIndex[childId]?.toQuest(questIndex, built)
        })

        return quest
    }
}
