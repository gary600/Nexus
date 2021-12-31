package xyz.gary600.nexus.spore

import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.gary600.nexus.*

/**
 * The various effects caused by players' Corruption/Spore level (total death count including out-of-character deaths)
 */
@Suppress("unused")
object SporeEffects : Effects() {
    // Warped spore particles around corrupted players
    @TimerTask(0, 10)
    fun sporeParticles() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.world.nexusEnabled
            && player.corruption >= 50
        }.forEach { player ->
            when (player.corruption) {
                // Tier I: just around player
                in 50..99 -> {

                }
                // Tier II: radius 2 blocks
                // Tier III: radius 5 blocks
            }
        }
    }

    // Potion effects for various tiers of Corruption
    @TimerTask(0, 10)
    fun potionEffects() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.world.nexusEnabled
            && player.corruptionTier >= CorruptionTier.Tier1
        }.forEach { player ->
            // Speed
            player.addPotionEffect(PotionEffect(
                PotionEffectType.SPEED,
                20,
                when (player.corruptionTier) {
                    CorruptionTier.Tier1 -> 0
                    CorruptionTier.Tier2, CorruptionTier.Tier3 -> 1
                    else -> -1 // Should be unreachable but don't error anyway
                }
            ))

            // Strength
            player.addPotionEffect(PotionEffect(
                PotionEffectType.INCREASE_DAMAGE,
                20,
                when (player.corruptionTier) {
                    CorruptionTier.Tier1 -> 0
                    CorruptionTier.Tier2 -> 1
                    CorruptionTier.Tier3 -> 2
                    else -> -1 // Should be unreachable but don't error anyway
                }
            ))

            if (player.corruptionTier >= CorruptionTier.Tier2) {
                // Hunger
                player.addPotionEffect(PotionEffect(
                    PotionEffectType.HUNGER,
                    20,
                    when (player.corruptionTier) {
                        CorruptionTier.Tier2 -> 0
                        CorruptionTier.Tier3 -> 1
                        else -> -1 // Should be unreachable but don't error anyway
                    }
                ))
            }
        }
    }

    // Immunity to poison on Tier 2 and higher
    @EventHandler
    fun poisonImmunity(event: EntityDamageEvent) {
        val entity = event.entity
        if (
            entity is Player
            && entity.corruption >= 100 // tier 2+
            && entity.world.nexusEnabled
            && event.cause == EntityDamageEvent.DamageCause.POISON
        ) {
            event.isCancelled = true
        }
    }

    // Extra hearts
    @TimerTask(0, 200)
    fun extraHearts() {
        Bukkit.getServer().onlinePlayers.filter {
            player -> player.world.nexusEnabled
        }.forEach { player ->
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = when (player.corruptionTier) {
                CorruptionTier.Tier0 -> 20.0 // regular 10 hearts
                CorruptionTier.Tier1 -> 24.0 // 12 hearts
                CorruptionTier.Tier2 -> 28.0 // 14 hearts
                CorruptionTier.Tier3 -> 32.0 // 16 hearts
            }
        }
    }
}