package xyz.gary600.nexusclasses.effects

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.NexusClasses
import xyz.gary600.nexusclasses.extension.nexusClass
import xyz.gary600.nexusclasses.extension.sendDebugMessage

class WarriorIronAllergyTask : BukkitRunnable() {
    // Warrior weaknesses
    override fun run() {
        // Holding iron weapons give mining fatigue
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Warrior
            && player.inventory.itemInMainHand.type in arrayOf(Material.IRON_SWORD, Material.IRON_AXE)
        }.forEach { player ->
            // Mining Fatigue 1, last 3 seconds after unequipping item
            player.addPotionEffect(PotionEffect(
                PotionEffectType.SLOW_DIGGING,
                60,
                0,
                false,
                false,
                false
            ))
            player.sendDebugMessage("[NexusClasses] Warrior weakness: mining fatigue from iron weapons")
        }

        // Wearing iron armor gives slowness
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Warrior
            && (
                player.equipment?.helmet?.type == Material.IRON_HELMET
                || player.equipment?.chestplate?.type == Material.IRON_CHESTPLATE
                || player.equipment?.leggings?.type == Material.IRON_LEGGINGS
                || player.equipment?.boots?.type == Material.IRON_BOOTS
            )
        }.forEach { player ->
            player.addPotionEffect(PotionEffect(
                PotionEffectType.SLOW,
                60,
                1,
                false,
                false,
                false
            ))
            player.sendDebugMessage("[NexusClasses] Warrior weakness: slowness from iron armor")
        }
    }
}