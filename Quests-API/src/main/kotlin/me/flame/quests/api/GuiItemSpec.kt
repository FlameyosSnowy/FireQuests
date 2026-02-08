package me.flame.quests.api

data class GuiItemSpec(
    val material: String,
    val name: String,
    val lore: List<String>,
    val flags: List<String>,
    val customModelData: Int?
)
