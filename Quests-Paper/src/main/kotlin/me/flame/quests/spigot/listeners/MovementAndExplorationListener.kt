package me.flame.quests.spigot.listeners

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.flame.quests.api.manager.QuestEventRouter
import me.flame.quests.api.quest.QuestEventContext
import me.flame.quests.api.quest.QuestEventType
import me.flame.quests.api.quest.QuestKey
import me.flame.quests.api.quest.QuestManager
import me.flame.quests.api.quest.payload.DistanceWalkedPayload
import me.flame.quests.api.quest.payload.VisitedBiomePayload
import me.flame.quests.spigot.impl.fromNamespacedKey
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import java.util.UUID

class MovementAndExplorationListener(
    private val router: QuestEventRouter,
    private val questManager: QuestManager,
    private val scope: CoroutineScope
) : Listener {

    private val distanceAccumulator = mutableMapOf<UUID, Double>()
    private val exploredBiomes = mutableMapOf<UUID, MutableSet<QuestKey>>()

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val from = event.from
        val to = event.to
        if (from.blockX == to.blockX && from.blockZ == to.blockZ) return

        scope.launch {
            handleMovement(player, from, to)
        }
    }

    private suspend fun handleMovement(player: Player, from: Location, to: Location) {
        val playerId = player.uniqueId
        val questPlayer = questManager.getQuestPlayer(playerId) ?: return

        // Distance walked accumulation
        val dx = (to.x - from.x)
        val dz = (to.z - from.z)
        val distanceSq = dx * dx + dz * dz

        val prev = distanceAccumulator.getOrDefault(playerId, 0.0)
        val total = prev + distanceSq
        val blocks = kotlin.math.floor(total).toInt() // convert squared distance to block count approximately
        distanceAccumulator[playerId] = total - blocks

        if (blocks > 0) {
            val payload = DistanceWalkedPayload(blocks)
            val ctx = QuestEventContext(
                player = questPlayer,
                questKey = QuestKey.none(),
                payload = payload
            )
            router.dispatch(QuestEventType.DISTANCE_WALKED, ctx)
        }

        // Biome exploration
        val biomeKey = to.block.biome.key
        val visited = exploredBiomes.computeIfAbsent(playerId) { mutableSetOf() }
        val questKey = fromNamespacedKey(biomeKey)
        if (visited.add(questKey)) {
            val payload = VisitedBiomePayload(questKey)
            val ctx = QuestEventContext(
                player = questPlayer,
                questKey = questKey,
                payload = payload
            )
            router.dispatch(QuestEventType.BIOME_EXPLORED, ctx)
        }
    }
}
