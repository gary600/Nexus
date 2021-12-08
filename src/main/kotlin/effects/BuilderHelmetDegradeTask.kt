package xyz.gary600.nexusclasses.effects

import org.bukkit.Sound
import org.bukkit.inventory.meta.Damageable
import org.bukkit.scheduler.BukkitRunnable
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.NexusClasses

class BuilderHelmetDegradeTask(private val plugin: NexusClasses) : BukkitRunnable() {
    override fun run() {
        plugin.server.onlinePlayers.filter { p ->
            plugin.getPlayerData(p.uniqueId).nexusClass == NexusClass.Builder
            && p.location.block.lightFromSky >= 15 // no block above head
            && p.equipment?.helmet != null // has a helmet
            && (p.world.time >= 23460 || p.world.time <= 12535) // same time as zombies
        }.forEach { p ->
            val helmet = p.equipment?.helmet
            val meta = helmet?.itemMeta
            if (meta is Damageable) { // Should always be true if wearing a normal helmet
                meta.damage += 1
                if (meta.damage > helmet.type.maxDurability) { // Break when 0 durability
                    helmet.amount = 0
                    p.playSound(p.location, Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f)
                }
            }
            p.equipment?.helmet?.itemMeta = meta

            plugin.sendDebugMessage(p, "[NexusClasses] Builder weakness: helmet degrading in sunlight")
        }
    }
}