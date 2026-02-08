package me.flame.quests.spigot.config

import io.github.mqzen.menus.misc.itembuilder.ComponentItemBuilder

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.CustomModelData
import me.flame.quests.api.GuiItemSpec

import me.flame.quests.spigot.hooks.parsePapi
import me.flame.quests.spigot.minimessage.color

import net.kyori.adventure.text.minimessage.MiniMessage

import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class GuiItemSection(
    override var config: ConfigurationSection
) : Config() {
    val material by string("material")
    val name by string("name")
    val lore by stringList("lore")
    val flags by stringList("flags")
    val customModelData by int("custom-model-data")

    fun buildItemStack(player: Player): ItemStack {
        val build = ComponentItemBuilder.modern(Material.valueOf(material))
            .setDisplay(color(parsePapi(player, name)))
            .setLore(lore.map { color(parsePapi(player, it)) })
            .addFlags(*flags.map { ItemFlag.valueOf(it) }.toTypedArray())
            .build()

        val custom = CustomModelData.customModelData()
            .addFloat(customModelData.toFloat())
            .build()

        build.setData(DataComponentTypes.CUSTOM_MODEL_DATA, custom)
        return build
    }

    fun buildGuiItemSpec(): GuiItemSpec {
        return GuiItemSpec(
            material = material,
            name = name,
            lore = lore,
            flags = flags,
            customModelData = customModelData.takeIf { it > 0 }
        )
    }
}
