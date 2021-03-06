package xyz.gary600.nexus.classes.effects

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import xyz.gary600.nexus.*
import xyz.gary600.nexus.classes.NexusClass
import xyz.gary600.nexus.classes.itemNexusClass
import xyz.gary600.nexus.classes.nexusClass
import java.util.UUID

/**
 * All of the effects of the Artist class
 */
@Suppress("unused") // EventHandler functions are used internally
object ArtistEffects : Effects() {
    // Set of players who are currently being hurt by being in water (for custom death message)
    private val dissolvingPlayers = HashSet<UUID>()

    // Perk: free end pearl at all times
    //TODO minor: prevent giving when clicking a chest/other interactable block
    @EventHandler
    fun freeEndPearl(event: PlayerInteractEvent) {
        if (
            event.action in arrayOf(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)
        ) {
            val classItem = event.item
            if (
                event.player.nexusClass == NexusClass.Artist
                && event.player.world.nexusEnabled
                && classItem?.type == Material.ENDER_PEARL
                && classItem.itemNexusClass == NexusClass.Artist
                && event.player.getCooldown(Material.ENDER_PEARL) <= 0 // don't give pearl when on pearl cooldown
                && event.player.gameMode != GameMode.CREATIVE // don't give in creative mode, it's not used up
            ) {
                classItem.amount = 2
                // Increase cooldown to 10 seconds (deferred by a tick to prevent it from cancelling this event)
                defer {
                    event.player.setCooldown(Material.ENDER_PEARL, 200)
                }
                event.player.nexusDebug("Artist perk: free end pearl")
            }
        }
    }

    // Weakness: take damage in water
    @TimerTask(0, 10)
    fun dissolveInWaterTask() {
        Bukkit.getServer().onlinePlayers.filter {
            player -> player.nexusClass == NexusClass.Artist
            && player.world.nexusEnabled
            && player.isInWater
        }.forEach { player ->
            dissolvingPlayers.add(player.uniqueId)
            player.damage(1.0) // Half-heart
            player.nexusDebug("Artist weakness: dissolving in water")
        }
    }

    // Remove players from list after they leave water
    @TimerTask(0, 1)
    fun stopDissolvingTask() {
        // Only retain players if they are online and in water
        dissolvingPlayers.retainAll {
            Bukkit.getServer().getPlayer(it)?.isInWater == true
        }
    }

    // Custom death message for dissolving in water
    @EventHandler
    fun dissolveDeathMessage(event: PlayerDeathEvent) {
        if (event.entity.uniqueId in dissolvingPlayers) {
            if (event.entity.world.nexusEnabled) {
                event.deathMessage = "${event.entity.name} dissolved"
            }
            dissolvingPlayers.remove(event.entity.uniqueId) // remove from dissolving players
        }
    }
}