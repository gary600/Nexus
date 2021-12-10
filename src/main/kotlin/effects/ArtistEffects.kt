package xyz.gary600.nexusclasses.effects

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.extension.isClassItem
import xyz.gary600.nexusclasses.extension.nexusClass
import xyz.gary600.nexusclasses.extension.sendDebugMessage
import java.util.*
import kotlin.collections.HashSet

/**
 * All of the effects of the Artist class
 */
@Suppress("unused") // EventHandler functions are used internally
class ArtistEffects : Effects() {
    private val dissolvingPlayers = HashSet<UUID>()

    // Perk: free end pearl at all times
    //TODO: prevent giving when clicking a chest/other interactable block
    //FIXME: Pearls don't deal any damage on the CMURPA server for some reason?
    @EventHandler
    fun freeEndPearl(event: PlayerInteractEvent) {
        if (event.action in arrayOf(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)) {
            val classItem = event.item
            if (
                classItem?.type == Material.ENDER_PEARL
                && classItem.isClassItem
                && event.player.getCooldown(Material.ENDER_PEARL) <= 0 // don't give pearl when on pearl cooldown
            ) {
                if (event.player.nexusClass == NexusClass.Artist) {
                    classItem.amount = 2
                    event.player.sendDebugMessage("Artist perk: free end pearl!")
                }
                // Don't let non-Artists use the pearl
                else {
                    classItem.amount = 0
                    event.isCancelled = true
                }
            }
        }
    }

    // Weakness: take damage in water
    @TimerTask(0, 10)
    fun dissolveInWaterTask() {
        Bukkit.getServer().onlinePlayers.filter {
                player -> player.nexusClass == NexusClass.Artist
                && player.isInWater
        }.forEach { player ->
            dissolvingPlayers.add(player.uniqueId)
            player.damage(1.0) // Half-heart
            player.sendDebugMessage("Artist weakness: allergic to water!")
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
            event.deathMessage = "${event.entity.name} dissolved"
            dissolvingPlayers.remove(event.entity.uniqueId) // remove from dissolving players
        }
    }
}