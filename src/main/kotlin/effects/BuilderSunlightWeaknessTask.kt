package xyz.gary600.nexusclasses.effects

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.NexusClasses
import xyz.gary600.nexusclasses.extension.nexusClass
import xyz.gary600.nexusclasses.extension.sendDebugMessage

class BuilderSunlightWeaknessTask : BukkitRunnable() {
    // Builder weakness: burning in sunlight when not wearing a helmet
    override fun run() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Builder
            && player.location.block.lightFromSky >= 15 // no block above head
            && player.equipment?.helmet == null // doesn't have a helmet
            && (player.world.time >= 23460 || player.world.time <= 12535) // same time as zombies
        }.forEach { player ->
            player.fireTicks = 40
            player.sendDebugMessage("[NexusClasses] Builder weakness: burning in sunlight")
        }
    }
}