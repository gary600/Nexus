package xyz.gary600.nexusclasses.effects

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.NexusClasses
import xyz.gary600.nexusclasses.extension.nexusClass
import xyz.gary600.nexusclasses.extension.sendDebugMessage

class ArtistWaterAllergyTask : BukkitRunnable() {
    // Artist weakness: damage from being in water
    override fun run() {
        Bukkit.getServer().onlinePlayers.filter {
            player -> player.nexusClass == NexusClass.Artist
            && player.isInWater
        }.forEach { player ->
            player.damage(1.0) // Half-heart
            player.sendDebugMessage("[NexusClasses] Artist weakness: allergic to water!")
        }
    }
}