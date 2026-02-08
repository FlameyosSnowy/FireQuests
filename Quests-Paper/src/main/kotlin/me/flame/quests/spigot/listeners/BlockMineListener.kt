package me.flame.quests.spigot.listeners

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.flame.quests.api.database.mongodb.MongoQuestPlayer
import me.flame.quests.api.manager.QuestEventRouter
import me.flame.quests.api.quest.QuestEventContext
import me.flame.quests.api.quest.QuestEventType
import me.flame.quests.api.quest.QuestManager
import me.flame.quests.api.quest.payload.BlockMinedPayload
import me.flame.quests.api.quest.progress.DefaultQuestProgress
import me.flame.quests.spigot.impl.fromBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BlockMineListener(private val questRouter: QuestEventRouter, private val questsManager: QuestManager, private val scope: CoroutineScope) : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        scope.launch {
            val player = event.player
            val block = event.block
            val questPlayer = questsManager.getOrCreateQuestPlayer(player.uniqueId) {
                MongoQuestPlayer(player.uniqueId, player.name, DefaultQuestProgress(), setOf())
            }

            val blockType = fromBlock(block.type)
            questRouter.dispatch(
                QuestEventType.BLOCK_MINED,
                QuestEventContext(questPlayer, blockType, BlockMinedPayload(blockType))
            )
        }
    }
}