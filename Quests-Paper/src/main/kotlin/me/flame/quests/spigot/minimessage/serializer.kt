package me.flame.quests.spigot.minimessage

import net.kyori.adventure.text.minimessage.MiniMessage

val SERIALIZER: MiniMessage = MiniMessage.miniMessage()

fun color(color: String) = SERIALIZER.deserialize(color)