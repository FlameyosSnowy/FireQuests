package me.flame.quests.spigot

import hazae41.minecraft.kutils.bukkit.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import me.flame.quests.spigot.config.QuestConfig
import org.bukkit.plugin.java.JavaPlugin
import kotlin.coroutines.CoroutineContext

class QuestsPlugin : JavaPlugin(), CoroutineScope {
    lateinit var config: QuestConfig

    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext =
        job + Dispatchers.IO

    override fun onEnable() {
        config = QuestConfig(this)
        info("Quests Plugin Enabled")
    }

    override fun onDisable() {
        // Cancels all running coroutines cleanly
        job.cancel()
        info("Quests Plugin Disabled")
    }
}
