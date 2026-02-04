package me.flame.quests.spigot.config

import hazae41.minecraft.kutils.bukkit.Config
import io.github.mqzen.menus.Lotus
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.ItemStack

class GuiItemSection(
    override var config: ConfigurationSection
) : Config() {
    val material by string("material")
    val name by string("name")
    val lore by stringList("lore")
    val flags by stringList("flags")
    val customModelData by int("custom-model-data")

    fun getItemStack(): ItemStack {
        Lotus
    }
}
