package xyz.gary600.nexusclasses.effects

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.extension.itemNexusClass
import xyz.gary600.nexusclasses.extension.nexusClass
import xyz.gary600.nexusclasses.extension.nexusClassesEnabled
import xyz.gary600.nexusclasses.extension.nexusDebugMessage
import java.util.UUID

/**
 * All of the effects of the Artist class
 */
@Suppress("unused") // EventHandler functions are used internally
class ArtistEffects : Effects() {
    // Set of players who are currently being hurt by being in water (for custom death message)
    private val dissolvingPlayers = HashSet<UUID>()

    // Perk: free end pearl at all times
    //TODO: prevent giving when clicking a chest/other interactable block
    //FIXME: Pearls don't deal any damage on the CMURPA server for some reason?
    @EventHandler
    fun freeEndPearl(event: PlayerInteractEvent) {
        if (
            event.action in arrayOf(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)
        ) {
            val classItem = event.item
            if (
                classItem?.type == Material.ENDER_PEARL
                && classItem.itemNexusClass == NexusClass.Artist
                && event.player.getCooldown(Material.ENDER_PEARL) <= 0 // don't give pearl when on pearl cooldown
                && event.player.gameMode != GameMode.CREATIVE // don't give in creative mode, it's not used up
            ) {
                // Free pearl if artist and in enabled world
                if (
                    event.player.nexusClass == NexusClass.Artist
                    && event.player.world.nexusClassesEnabled
                ) {
                    classItem.amount = 2
                    // Increase cooldown to 10 seconds
                    event.player.setCooldown(Material.ENDER_PEARL, 200)
                    event.player.nexusDebugMessage("Artist perk: free end pearl")
                }
                // If not artist or not in world, delete pearl and prevent throwing it
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
            && player.world.nexusClassesEnabled
            && player.isInWater
        }.forEach { player ->
            dissolvingPlayers.add(player.uniqueId)
            player.damage(1.0) // Half-heart
            player.nexusDebugMessage("Artist weakness: allergic to water")
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
            if (event.entity.world.nexusClassesEnabled) {
                event.deathMessage = "${event.entity.name} dissolved"
            }
            dissolvingPlayers.remove(event.entity.uniqueId) // remove from dissolving players
        }
    }
}