package xyz.gary600.nexusclasses.effects

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.inventory.meta.Damageable
import org.bukkit.scheduler.BukkitRunnable
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.NexusClasses
import xyz.gary600.nexusclasses.extension.nexusClass
import xyz.gary600.nexusclasses.extension.sendDebugMessage

class BuilderHelmetDegradeTask : BukkitRunnable() {
    // Builder weakness: helmet degrades in the sun
    override fun run() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Builder
            && player.location.block.lightFromSky >= 15 // no block above head
            && player.equipment?.helmet != null // has a helmet
            && (player.world.time >= 23460 || player.world.time <= 12535) // same time as zombies
        }.forEach { player ->
            val helmet = player.equipment?.helmet
            val meta = helmet?.itemMeta
            if (meta is Damageable) { // Should always be true if wearing a normal helmet
                meta.damage += 1
                if (meta.damage > helmet.type.maxDurability) { // Break when 0 durability
                    helmet.amount = 0
                    player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f)
                }
            }
            player.equipment?.helmet?.itemMeta = meta

            player.sendDebugMessage("[NexusClasses] Builder weakness: helmet degrading in sunlight")
        }
    }
}