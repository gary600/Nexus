package xyz.gary600.nexus.corruption

import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.gary600.nexus.Effects
import xyz.gary600.nexus.TimerTask
import xyz.gary600.nexus.nexusEnabled
import kotlin.random.Random

/**
 * The various effects caused by players' Corruption level (total death count including out-of-character deaths)
 */
@Suppress("unused")
object CorruptionEffects : Effects() {
    // Warped spore particles around corrupted players
    @TimerTask(0, 3)
    fun sporeParticles() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.world.nexusEnabled
            && player.gameMode in arrayOf(GameMode.SURVIVAL, GameMode.ADVENTURE)
            && player.corruptionTier >= CorruptionTier.Tier1
        }.forEach { player ->
            // Effect field radii
            //TODO: clarify particles
            val (offsetXZ: Double, offsetY: Double) = when (player.corruptionTier) {
                CorruptionTier.Tier1 -> 0.5 to 1.0 // "around the player"
                CorruptionTier.Tier2 -> 2.0 to 2.0
                CorruptionTier.Tier3 -> 5.0 to 3.0
                else -> 0.0 to 0.0 // unreachable
            }

            player.world.spawnParticle(
                Particle.WARPED_SPORE,
                player.location,
                when (player.corruptionTier) { // particle count
                    CorruptionTier.Tier1 -> 1
                    CorruptionTier.Tier2 -> 5
                    CorruptionTier.Tier3 -> 10
                    else -> 0 // unreachable
                },
//                offsetXZ,
//                offsetY,
//                offsetXZ
                // cover the player's bounding box
                0.5,
                1.0,
                0.5
            )
        }
    }

    // Potion effects for various tiers of Corruption
    @TimerTask(0, 10)
    fun potionEffects() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.world.nexusEnabled
            && player.gameMode in arrayOf(GameMode.SURVIVAL, GameMode.ADVENTURE)
            && player.corruptionTier >= CorruptionTier.Tier1
        }.forEach { player ->
            // Speed
            player.addPotionEffect(PotionEffect(
                PotionEffectType.SPEED,
                19,
                when (player.corruptionTier) {
                    CorruptionTier.Tier1 -> 0
                    CorruptionTier.Tier2, CorruptionTier.Tier3 -> 1
                    else -> -1 // Should be unreachable but don't error anyway
                },
                false,
                false,
                false
            ))

            // Strength
            player.addPotionEffect(PotionEffect(
                PotionEffectType.INCREASE_DAMAGE,
                19,
                when (player.corruptionTier) {
                    CorruptionTier.Tier1 -> 0
                    CorruptionTier.Tier2 -> 1
                    CorruptionTier.Tier3 -> 2
                    else -> -1 // Should be unreachable but don't error anyway
                },
                false,
                false,
                false
            ))

            if (player.corruptionTier >= CorruptionTier.Tier2) {
                // Hunger
                player.addPotionEffect(PotionEffect(
                    PotionEffectType.HUNGER,
                    19,
                    when (player.corruptionTier) {
                        CorruptionTier.Tier2 -> 0
                        CorruptionTier.Tier3 -> 1
                        else -> -1 // Should be unreachable but don't error anyway
                    },
                    false,
                    false,
                    false
                ))
            }
        }
    }

    // Immunity to poison on Tier 2 and higher
    @EventHandler
    fun removePoison(event: EntityPotionEffectEvent) {
        val entity = event.entity
        if (
            entity is Player
            && entity.corruptionTier >= CorruptionTier.Tier2
            && entity.world.nexusEnabled
            && event.newEffect?.type == PotionEffectType.POISON // if event is *giving* poison to the player
        ) {
            entity.removePotionEffect(PotionEffectType.POISON)
        }
    }
    @EventHandler
    fun poisonNoDamage(event: EntityDamageEvent) {
        val entity = event.entity
        if (
            entity is Player
            && entity.corruptionTier >= CorruptionTier.Tier2 // tier 2+
            && entity.world.nexusEnabled
            && event.cause == EntityDamageEvent.DamageCause.POISON
        ) {
            event.isCancelled = true
        }
    }

    // Extra hearts
    @TimerTask(0, 200)
    fun extraHearts() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.world.nexusEnabled
        }.forEach { player ->
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = when (player.corruptionTier) {
                CorruptionTier.Tier0 -> 20.0 // regular 10 hearts
                CorruptionTier.Tier1 -> 24.0 // 12 hearts
                CorruptionTier.Tier2 -> 28.0 // 14 hearts
                CorruptionTier.Tier3 -> 32.0 // 16 hearts
            }
        }
    }

    // Poison to mobs in range
    @TimerTask(0, 20)
    fun poisonMobsTier2() { // poison 1 10% chance every 10 seconds within 2 blocks
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.world.nexusEnabled
            && player.gameMode in arrayOf(GameMode.SURVIVAL, GameMode.ADVENTURE)
            && player.corruptionTier == CorruptionTier.Tier2
        }.forEach { player ->
            for (entity in player.getNearbyEntities(2.0, 2.0, 2.0)) {
                if (entity is LivingEntity && entity !is Player && Random.Default.nextDouble() <= 0.1) { // 10% chance
                    entity.addPotionEffect(PotionEffect(PotionEffectType.POISON, 200, 0))
                }
            }
        }
    }
    @TimerTask(0, 3)
    fun poisonMobsTier3() { // poison 2 instantly
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.world.nexusEnabled
            && player.gameMode in arrayOf(GameMode.SURVIVAL, GameMode.ADVENTURE)
            && player.corruptionTier == CorruptionTier.Tier3
        }.forEach { player ->
            for (entity in player.getNearbyEntities(5.0, 5.0, 5.0)) {
                if (entity is LivingEntity && entity !is Player) {
                    //TODO: fix not damaging mobs until leaving range
                    entity.addPotionEffect(PotionEffect(PotionEffectType.POISON, 200, 1))
                }
            }
        }
    }

    // Prevent regular targeting by hostile mobs
    @EventHandler
    fun preventMobTarget(event: EntityTargetEvent) {
        val target = event.target
        if (
            target is Player
            && target.world.nexusEnabled
            && target.gameMode in arrayOf(GameMode.SURVIVAL, GameMode.ADVENTURE)
            && target.corruptionTier >= CorruptionTier.Tier3
            && event.reason == EntityTargetEvent.TargetReason.CLOSEST_PLAYER // regular random mob aggros
        ) {
            event.isCancelled = true
        }
    }

    // Corrupt nearby grass and dirt into warped nylium
    @TimerTask(0, 20)
    fun corruptBlocks() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.world.nexusEnabled
            && player.gameMode in arrayOf(GameMode.SURVIVAL, GameMode.ADVENTURE)
            && player.corruptionTier >= CorruptionTier.Tier2
        }.forEach { player ->
            val range = when (player.corruptionTier) {
                CorruptionTier.Tier2 -> 2
                CorruptionTier.Tier3 -> 5
                else -> 0 // unreachable
            }
            for (x in -range..range) {
                for (y in -range..range) {
                    for (z in -range..range) {
                        val block = player.world.getBlockAt(
                            player.location.add(x.toDouble(), y.toDouble(), z.toDouble())
                        )

                        if (Random.Default.nextDouble() <= 0.1) { // 10% chance
                            // Grass and Dirt -> Warped Nylium
                            if (block.type in arrayOf(Material.GRASS_BLOCK, Material.DIRT)) {
                                block.setType(Material.WARPED_NYLIUM, false) // don't send block update
                            }
                            // Plants except Crops -> Warped Fungus
                            if (
                                Tag.REPLACEABLE_PLANTS.isTagged(block.type) // tall grass, etc
                                || Tag.FLOWERS.isTagged(block.type) // small flowers
                            ) {
                                block.setType(Material.WARPED_FUNGUS, false) // don't send block update
                            }
                        }
                    }
                }
            }
        }
    }
}