package me.flame.quests.spigot.config

import hazae41.minecraft.kutils.bukkit.Config
import io.github.mqzen.menus.misc.itembuilder.ComponentItemBuilder
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.CustomModelData
import me.flame.quests.spigot.hooks.parsePapi
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

val SERIALIZER: MiniMessage = MiniMessage.miniMessage();

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
            .setDisplay(SERIALIZER.deserialize(parsePapi(player, name)))
            .setLore(lore.map { SERIALIZER.deserialize(parsePapi(player, it)) })
            .addFlags(*flags.map { ItemFlag.valueOf(it) }.toTypedArray())
            .build()

        val custom = CustomModelData.customModelData()
            .addFloat(customModelData.toFloat())
            .build()

        build.setData(DataComponentTypes.CUSTOM_MODEL_DATA, custom)
        return build
    }
}
