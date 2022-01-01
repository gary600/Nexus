package xyz.gary600.nexus.classes.effects

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.gary600.nexus.Effects
import xyz.gary600.nexus.TimerTask
import xyz.gary600.nexus.classes.NexusClass
import xyz.gary600.nexus.classes.nexusClass
import xyz.gary600.nexus.nexusEnabled
import xyz.gary600.nexus.nexusDebug

/**
 * All of the effects of the Warrior class
 */
@Suppress("unused")
object WarriorEffects : Effects() {
    // Perk: Automatic fire aspect on melee weapons
    @EventHandler
    fun meleeFireAspect(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        if (
            damager is Player
            && damager.nexusClass == NexusClass.Warrior
            && damager.world.nexusEnabled
            && damager.inventory.itemInMainHand.type in arrayOf(
//                Material.WOODEN_SWORD, Material.WOODEN_AXE, // removed per request
//                Material.STONE_SWORD, Material.STONE_AXE,
                Material.IRON_SWORD, Material.IRON_AXE,
                Material.GOLDEN_SWORD, Material.GOLDEN_AXE,
                Material.DIAMOND_SWORD, Material.DIAMOND_AXE,
                Material.NETHERITE_SWORD, Material.NETHERITE_AXE
            )
        ) {
            event.entity.fireTicks = 80 // equivalent to Fire Aspect 1
            damager.nexusDebug("Warrior perk: fire aspect")
        }
    }

    // Perk: Attacks with golden weapons are equivalent to having Strength II
    @EventHandler
    fun goldenStrength(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        if (
            damager is Player
            && damager.nexusClass == NexusClass.Warrior
            && damager.world.nexusEnabled
            && damager.inventory.itemInMainHand.type in arrayOf(
                Material.GOLDEN_SWORD,
                Material.GOLDEN_AXE
            )
        ) {
            event.damage += 6 // equivalent to Strength II
            damager.nexusDebug("Warrior perk: golden strength")
        }
    }

    // Perk: permanent fire resist
    @TimerTask(0, 10)
    fun fireResist() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Warrior
            && player.world.nexusEnabled
        }.forEach { player ->
            player.addPotionEffect(PotionEffect(
                PotionEffectType.FIRE_RESISTANCE,
                19,
                0,
                false,
                false,
                false
            ))
            // No debug message for this cause even I would find that number of messages unbearable
        }
    }

    // Weakness: Holding iron weapons give mining fatigue II
    @TimerTask(0, 10)
    fun ironAllergy() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Warrior
            && player.world.nexusEnabled
            && player.inventory.itemInMainHand.type in arrayOf(Material.IRON_SWORD, Material.IRON_AXE)
        }.forEach { player ->
            player.addPotionEffect(PotionEffect(
                PotionEffectType.SLOW_DIGGING,
                200,
                1, // 1 means level 2
                false,
                false,
                false
            ))
            player.nexusDebug("Warrior weakness: mining fatigue from iron weapons")
        }
    }
}