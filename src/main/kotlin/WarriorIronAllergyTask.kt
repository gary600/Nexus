package xyz.gary600.nexusclasses

import org.bukkit.Material
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

class WarriorIronAllergyTask(private val plugin: NexusClasses) : BukkitRunnable() {
    override fun run() {
        // Holding iron weapons give mining fatigue
        plugin.server.onlinePlayers.filter { p ->
            plugin.getPlayerData(p.uniqueId).nexusClass == NexusClass.Warrior
            && p.inventory.getItem(p.inventory.heldItemSlot)?.type in arrayOf(
                Material.IRON_SWORD,
                Material.IRON_AXE
            )
        }.forEach { p ->
            // Mining Fatigue 1, last 3 seconds after unequipping item
            p.addPotionEffect(PotionEffect(PotionEffectType.SLOW_DIGGING, 60, 0, false, false, false))
            plugin.sendDebugMessage(p, "[NexusClasses] Warrior weakness: mining fatigue from iron weapons")
        }

        // Wearing iron armor gives weakness
    }
}