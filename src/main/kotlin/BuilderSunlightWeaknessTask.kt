package xyz.gary600.nexusclasses

import org.bukkit.inventory.EquipmentSlot
import org.bukkit.scheduler.BukkitRunnable

class BuilderSunlightWeaknessTask(private val plugin: NexusClasses) : BukkitRunnable() {
    override fun run() {
        plugin.server.onlinePlayers.filter { p ->
            plugin.getPlayerData(p.uniqueId).nexusClass == NexusClass.Builder
//            && p.location.block.getRelative(BlockFace.DOWN).type == Material.AIR // no block above player
            && p.equipment?.getItem(EquipmentSlot.HEAD) == null // doesn't have a helmet
            && p.location.block.lightFromSky > 11 // same as zombies
            && (p.world.time >= 23460 || p.world.time <= 12535) // same time as zombies
        }.forEach { p ->
            p.fireTicks += 20
        }
    }
}