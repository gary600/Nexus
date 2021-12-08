package xyz.gary600.nexusclasses.effects

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.NexusClasses
import xyz.gary600.nexusclasses.extension.isClassItem
import xyz.gary600.nexusclasses.extension.nexusClass
import xyz.gary600.nexusclasses.extension.sendDebugMessage

/**
 * All of the effects of the Artist class
 */
@Suppress("unused") // EventHandler functions are used internally
class ArtistEffects : Listener {
    fun register() {
        Bukkit.getServer().pluginManager.registerEvents(this, NexusClasses.instance!!)
        Bukkit.getScheduler().runTaskTimer(NexusClasses.instance!!, this::waterAllergyTask, 0, 10)
    }

    // Perk: free end pearl at all times
    //TODO: prevent giving when clicking a chest/other interactable block
    @EventHandler
    fun freeEndPearl(event: PlayerInteractEvent) {
        if (event.action in arrayOf(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)) {
            val classItem = event.item
            if (
                classItem?.type == Material.ENDER_PEARL
                && classItem.isClassItem()
                && event.player.getCooldown(Material.ENDER_PEARL) <= 0 // don't give pearl when on pearl cooldown
            ) {
                if (event.player.nexusClass == NexusClass.Artist) {
                    classItem.amount = 2
                    event.player.sendDebugMessage("[NexusClasses] Artist perk: free end pearl!")
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
    private fun waterAllergyTask() {
        Bukkit.getServer().onlinePlayers.filter {
                player -> player.nexusClass == NexusClass.Artist
                && player.isInWater
        }.forEach { player ->
            player.damage(1.0) // Half-heart
            player.sendDebugMessage("[NexusClasses] Artist weakness: allergic to water!")
        }
    }
}