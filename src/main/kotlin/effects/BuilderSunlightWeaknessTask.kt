package xyz.gary600.nexusclasses.effects

import org.bukkit.scheduler.BukkitRunnable
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.NexusClasses

class BuilderSunlightWeaknessTask(private val plugin: NexusClasses) : BukkitRunnable() {
    override fun run() {
        plugin.server.onlinePlayers.filter { p ->
            plugin.getPlayerData(p.uniqueId).nexusClass == NexusClass.Builder
            && p.location.block.lightFromSky >= 15 // no block above head
            && p.equipment?.helmet == null // doesn't have a helmet
            && (p.world.time >= 23460 || p.world.time <= 12535) // same time as zombies
        }.forEach { p ->
            p.fireTicks = 40
            plugin.sendDebugMessage(p, "[NexusClasses] Builder weakness: burning in sunlight")
        }
    }
}