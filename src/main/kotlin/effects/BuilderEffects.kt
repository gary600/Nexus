package xyz.gary600.nexusclasses.effects

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.meta.Damageable
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.extension.itemNexusClass
import xyz.gary600.nexusclasses.extension.nexusClass
import xyz.gary600.nexusclasses.extension.nexusClassesEnabled
import xyz.gary600.nexusclasses.extension.nexusDebugMessage
import java.util.*

/**
 * All of the effects of the Builder class
 */
@Suppress("unused")
class BuilderEffects : Effects() {
    // Keeps track of which players are currently burning (for custom death message)
    private val burningPlayers = HashSet<UUID>()

    // Perk: Inhibit fall damage
    @EventHandler(priority = EventPriority.HIGHEST)
    fun cancelFallDamage(event: EntityDamageEvent) {
        val entity = event.entity
        if (
            entity is Player
            && entity.nexusClass == NexusClass.Builder
            && entity.world.nexusClassesEnabled
            && event.cause == EntityDamageEvent.DamageCause.FALL
        ) {
            event.isCancelled = true
            event.damage = 0.0 // Cancelling doesn't seem to work on the CMURPGA server
            entity.nexusDebugMessage("Builder perk: Fall damage cancelled")
        }
    }

    // Perk: Transmute blocks
    @EventHandler
    fun transmute(event: PlayerInteractEvent) {
        // Only trigger when block right-clicked with a Builder class-item stick
        if (
            event.player.nexusClass == NexusClass.Builder
            && event.player.world.nexusClassesEnabled
            && event.action == Action.RIGHT_CLICK_BLOCK
            && event.item?.itemNexusClass == NexusClass.Builder
            && event.item?.type == Material.STICK
        ) {
            val block = event.clickedBlock!! // cannot be null because of action type
            val newType = when (block.type) {
                // Series 1: cobble -> stone -> stone brick -> obsidian
                Material.COBBLESTONE -> Material.STONE
                Material.STONE -> Material.STONE_BRICKS
                Material.STONE_BRICKS -> Material.OBSIDIAN

                // Series 2: deepslate -> tuff -> nether brick -> blackstone -> obsidian
                Material.DEEPSLATE -> Material.TUFF
                Material.TUFF -> Material.NETHER_BRICKS
                Material.NETHER_BRICKS -> Material.BLACKSTONE
                Material.BLACKSTONE -> Material.OBSIDIAN

                // Otherwise keep it the same
                else -> null
            }
            if (newType != null) {
                // Set block type
                block.type = newType
                // Spawn particles at center of block
                block.world.spawnParticle(
                    Particle.BLOCK_DUST,
                    block.location.add(0.5, 0.5, 0.5),
                    32,
                    block.blockData
                )
                // Play magic noise
                block.world.playSound(
                    block.location,
                    Sound.BLOCK_ENCHANTMENT_TABLE_USE,
                    1.0f,
                    1.0f
                )

                event.player.nexusDebugMessage("Builder perk: Block transmuted")
            }
        }
    }

    // Perk: permanent jump boost II
    @TimerTask(0, 10)
    fun jumpBoost() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Builder
            && player.world.nexusClassesEnabled
        }.forEach { player ->
            player.addPotionEffect(PotionEffect(
                PotionEffectType.JUMP,
                20,
                1, // 1 means level 2
                false,
                false,
                false
            ))
            // no debug message because that'd be too spammy
        }
    }

    // Weakness: burn in sunlight when not wearing a helmet
    @TimerTask(0, 20)
    fun burnInSunTask() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Builder
            && player.world.nexusClassesEnabled
            && player.location.block.lightFromSky >= 15 // no block above head
            && player.world.isClearWeather // isn't raining or thundering
            && player.equipment?.helmet == null // doesn't have a helmet
            && (player.world.time >= 23460 || player.world.time <= 12535) // same time as zombies
        }.forEach { player ->
            burningPlayers.add(player.uniqueId)
            player.fireTicks = 40
            player.nexusDebugMessage("Builder weakness: burning in sunlight")
        }
    }
    // Removes players from the burning players list if they stop burning
    @TimerTask(0, 1)
    fun stopBurningTask() {
        // Only retain players who are online and have non-zero fire ticks
        burningPlayers.retainAll {
            (Bukkit.getServer().getPlayer(it)?.fireTicks ?: 0) > 0
        }
    }
    // Custom death message for burning to death in the sun
    @EventHandler
    fun burnDeathMessage(event: PlayerDeathEvent) {
        if (
            event.entity.uniqueId in burningPlayers
            && event.entity.world.nexusClassesEnabled
        ) {
            event.deathMessage = "${event.entity.name} forgot their hard hat"
            burningPlayers.remove(event.entity.uniqueId) // remove from burning players
        }
    }

    // Weakness: helmet degrades in sunlight
    @TimerTask(0, 1200)
    fun helmetDegradeTask() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Builder
            && player.world.nexusClassesEnabled
            && player.location.block.lightFromSky >= 15 // no block above head
            && player.world.isClearWeather // isn't raining or thundering
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

            player.nexusDebugMessage("Builder weakness: helmet degrading in sunlight")
        }
    }
}