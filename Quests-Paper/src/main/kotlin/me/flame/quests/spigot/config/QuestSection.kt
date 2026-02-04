package me.flame.quests.spigot.config

import hazae41.minecraft.kutils.bukkit.Config
import org.bukkit.configuration.ConfigurationSection

class QuestSection(
    override var config: ConfigurationSection
) : Config() {

    val id by string("id")
    val type by string("type")
    val target by string("target")

    val requiredAmount by int("required-amount")

    val rewardCommand by string("reward-command")
    val rewardItems by list("reward-items")

    val guiItem: GuiItemSection?
        get() = config.getConfigurationSection("gui-item")?.let { GuiItemSection(it) }
}
