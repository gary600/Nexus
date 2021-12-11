package xyz.gary600.nexusclasses.effects

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.extension.nexusClass
import xyz.gary600.nexusclasses.extension.nexusClassesEnabled
import xyz.gary600.nexusclasses.extension.nexusDebugMessage

/**
 * All of the effects of the Warrior class
 */
@Suppress("unused")
class WarriorEffects : Effects() {
    // Perk: Automatic fire aspect on melee weapons
    @EventHandler
    fun meleeFireAspect(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        if (
            damager is Player
            && damager.nexusClass == NexusClass.Warrior
            && damager.world.nexusClassesEnabled
            && damager.inventory.itemInMainHand.type in arrayOf(
                Material.WOODEN_SWORD,
                Material.STONE_SWORD,
                Material.IRON_SWORD,
                Material.GOLDEN_SWORD,
                Material.DIAMOND_SWORD,
                Material.NETHERITE_SWORD,
                Material.WOODEN_AXE,
                Material.STONE_AXE,
                Material.IRON_AXE,
                Material.GOLDEN_AXE,
                Material.DIAMOND_AXE,
                Material.NETHERITE_AXE
            )
        ) {
            event.entity.fireTicks = 80 // equivalent to Fire Aspect 1
            damager.nexusDebugMessage("Warrior perk: Enemy ignited!")
        }
    }

    // Perk: Attacks with golden weapons are equivalent to having Strength II
    @EventHandler
    fun goldenStrength(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        if (
            damager is Player
            && damager.nexusClass == NexusClass.Warrior
            && damager.world.nexusClassesEnabled
            && damager.inventory.itemInMainHand.type in arrayOf(
                Material.GOLDEN_SWORD,
                Material.GOLDEN_AXE
            )
        ) {
            event.damage += 6 // equivalent to Strength II
            damager.nexusDebugMessage("Warrior perk: golden strength")
        }
    }

    // Perk: permanent fire resist
    @TimerTask(0, 10)
    fun fireResist() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Warrior
            && player.world.nexusClassesEnabled
        }.forEach { player ->
            player.addPotionEffect(PotionEffect(
                PotionEffectType.FIRE_RESISTANCE,
                20,
                0,
                false,
                false,
                false
            ))
            // No debug message for this cause even I would find that number of messages unbearable
        }
    }

    // Weaknesses
    @TimerTask(0, 10)
    fun ironAllergy() {
        // Holding iron weapons give mining fatigue
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Warrior
            && player.world.nexusClassesEnabled
            && player.inventory.itemInMainHand.type in arrayOf(Material.IRON_SWORD, Material.IRON_AXE)
        }.forEach { player ->
            // Mining Fatigue 1, last 3 seconds after unequipping item
            player.addPotionEffect(
                PotionEffect(
                PotionEffectType.SLOW_DIGGING,
                60,
                0,
                false,
                false,
                false
            )
            )
            player.nexusDebugMessage("Warrior weakness: mining fatigue from iron weapons")
        }

        // Wearing iron armor gives slowness
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Warrior
            && player.world.nexusClassesEnabled
            && (
                player.equipment?.helmet?.type == Material.IRON_HELMET
                || player.equipment?.chestplate?.type == Material.IRON_CHESTPLATE
                || player.equipment?.leggings?.type == Material.IRON_LEGGINGS
                || player.equipment?.boots?.type == Material.IRON_BOOTS
            )
        }.forEach { player ->
            player.addPotionEffect(
                PotionEffect(
                PotionEffectType.SLOW,
                60,
                1,
                false,
                false,
                false
            )
            )
            player.nexusDebugMessage("Warrior weakness: slowness from iron armor")
        }
    }
}