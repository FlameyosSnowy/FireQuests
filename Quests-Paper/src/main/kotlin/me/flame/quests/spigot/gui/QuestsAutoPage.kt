package me.flame.quests.spigot.gui

import io.github.mqzen.menus.base.Content
import io.github.mqzen.menus.base.iterator.Direction
import io.github.mqzen.menus.base.pagination.FillRange
import io.github.mqzen.menus.base.pagination.Page
import io.github.mqzen.menus.base.pagination.Pagination
import io.github.mqzen.menus.misc.Capacity
import io.github.mqzen.menus.misc.DataRegistry
import io.github.mqzen.menus.misc.Slot
import io.github.mqzen.menus.misc.button.Button
import io.github.mqzen.menus.misc.itembuilder.ItemBuilder
import io.github.mqzen.menus.titles.MenuTitle
import io.github.mqzen.menus.titles.MenuTitles
import me.flame.quests.api.quest.Quest
import me.flame.quests.api.quest.entity.QuestPlayer
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class QuestsAutoPage(val player: QuestPlayer, val quests: Collection<Quest>) : Page() {
    override fun getFillRange(capacity: Capacity, opener: Player): FillRange {
        return FillRange.start(capacity, Slot.of(10))
            .end(Slot.of(53))
            .except(Slot.of(0))
    }

    override fun nextPageItem(player: Player): ItemStack {
        return ItemBuilder.legacy(Material.PAPER)
            .setDisplay("&aNext page")
            .build()
    }

    override fun previousPageItem(player: Player): ItemStack {
        return ItemBuilder.legacy(Material.PAPER)
            .setDisplay("&ePrevious page")
            .build()
    }

    override fun getName(): String {
        return "Example auto-pagination"
    }

    override fun getTitle(dataRegistry: DataRegistry, player: Player): MenuTitle {
        val index = dataRegistry.getData<Int>("index")
        val pagination = dataRegistry.getData<Pagination>("pagination")
        val max = pagination.maximumPages
        return MenuTitles.createModern("<gold>Quests " + (index + 1) + "/" + max)
    }

    override fun getCapacity(dataRegistry: DataRegistry, player: Player): Capacity {
        return Capacity.ofRows(4)
    }

    override fun getContent(
        dataRegistry: DataRegistry,
        player: Player,
        capacity: Capacity
    ): Content {
        val glass = ItemStack.of(Material.LIGHT_GRAY_STAINED_GLASS)
        return Content.builder(capacity)
            .draw(Slot.of(0, 0), Direction.LEFT, glass)
            .draw(Slot.of(capacity.rows - 1, 0), Direction.LEFT, glass)
            .draw(Slot.of(0, 0), Direction.DOWNWARDS, glass)
            .draw(Slot.of(0, 8), Direction.DOWNWARDS, glass)
            .apply { content ->
                var index = 0
                for (row in 1 until capacity.rows - 1) {
                    for (col in 2 until 8) {
                        if (index >= quests.size) return@apply
                        val item = ItemStack.of(Material.DIAMOND_PICKAXE).apply {
                            itemMeta = itemMeta?.apply { displayName(Component.text(index.toString())) }
                        }

                        content.setButton(
                            Slot.of(row, col),
                            Button.empty(item)
                        )
                        index++
                    }
                }
            }

            .build()
    }

}