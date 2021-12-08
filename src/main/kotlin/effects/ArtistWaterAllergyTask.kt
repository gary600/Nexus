package xyz.gary600.nexusclasses.effects

import org.bukkit.scheduler.BukkitRunnable
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.NexusClasses

class ArtistWaterAllergyTask(private val plugin: NexusClasses) : BukkitRunnable() {
    // Artist weakness: damage from being in water
    override fun run() {
        plugin.server.onlinePlayers.filter { p ->
            plugin.getPlayerData(p.uniqueId).nexusClass == NexusClass.Artist
            && p.isInWater
        }.forEach { p ->
            p.damage(1.0) // Half-heart
            plugin.sendDebugMessage(p, "[NexusClasses] Artist weakness: allergic to water!")
        }
    }
}