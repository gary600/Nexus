package xyz.gary600.nexusclasses

import org.bukkit.scheduler.BukkitRunnable

class ArtistWaterAllergyTask(private val plugin: NexusClasses) : BukkitRunnable() {
    // Artist weakness: damage from being in water
    override fun run() {
        plugin.server.onlinePlayers.filter { p ->
            plugin.getPlayerData(p.uniqueId).nexusClass == NexusClass.Artist
            && p.isInWater
        }.forEach { p ->
            p.damage(1.0) // Half-heart
            plugin.sendPerkMessage(p, "[NexusClasses] Artist weakness: allergic to water!")
        }
    }
}