package me.flame.quests.spigot.hooks

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import kotlin.properties.Delegates

var hasPapi by Delegates.notNull<Boolean>()
    private set

fun initHooks(plugin: JavaPlugin) {
    hasPapi = plugin.server.pluginManager.isPluginEnabled("PlaceholderAPI")
}

fun parsePapi(player: Player, msg: String): String =
    if (hasPapi) PlaceholderAPI.setPlaceholders(player, msg) else msg