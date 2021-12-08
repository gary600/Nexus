package xyz.gary600.nexusclasses.effects

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.NexusClasses
import xyz.gary600.nexusclasses.extension.nexusClass
import xyz.gary600.nexusclasses.extension.sendDebugMessage

/**
 * All of the effects of the Warrior class
 */
@Suppress("unused")
class WarriorEffects : Listener {
    fun register() {
        Bukkit.getServer().pluginManager.registerEvents(this, NexusClasses.instance!!)
        Bukkit.getScheduler().runTaskTimer(NexusClasses.instance!!, this::ironAllergy, 0, 10)
    }

    // Perk: Automatic fire aspect on golden weapons
    @EventHandler
    fun goldWeaponsBuff(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        if (
            damager is Player
            && damager.nexusClass == NexusClass.Warrior
            && damager.inventory.itemInMainHand.type in arrayOf(
                Material.GOLDEN_SWORD,
                Material.GOLDEN_AXE
            )
        ) {
            event.entity.fireTicks = 80 // equivalent to Fire Aspect 1
            event.damage += 6 // equivalent to Strength II
            damager.sendDebugMessage("[NexusClasses] Warrior perk: Enemy ignited!")
        }
    }

    // Perk: Wearing gold armor gives fire immunity
    //FIXME: not working on CMURPGA server, plugin conflict?
    @EventHandler
    fun fireResist(event: EntityDamageEvent) {
        val entity = event.entity
        if (
            entity is Player
            && entity.nexusClass == NexusClass.Warrior
            && (
                    entity.equipment?.helmet?.type == Material.GOLDEN_HELMET
                            || entity.equipment?.chestplate?.type == Material.GOLDEN_CHESTPLATE
                            || entity.equipment?.leggings?.type == Material.GOLDEN_LEGGINGS
                            || entity.equipment?.boots?.type == Material.GOLDEN_BOOTS
                    )
            && event.cause in arrayOf(
                EntityDamageEvent.DamageCause.FIRE,
                EntityDamageEvent.DamageCause.FIRE_TICK,
                EntityDamageEvent.DamageCause.LAVA
            )
        ) {
            event.isCancelled = true
            entity.sendDebugMessage("[NexusClasses] Warrior perk: Fire resistance!") // very spammy
        }
    }

    // Weaknesses
    private fun ironAllergy() {
        // Holding iron weapons give mining fatigue
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Warrior
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
            player.sendDebugMessage("[NexusClasses] Warrior weakness: slowness from iron armor")
        }
    }
}