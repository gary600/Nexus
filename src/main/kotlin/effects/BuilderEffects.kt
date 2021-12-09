package xyz.gary600.nexusclasses.effects

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.meta.Damageable
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.NexusClasses
import xyz.gary600.nexusclasses.extension.isClassItem
import xyz.gary600.nexusclasses.extension.nexusClass
import xyz.gary600.nexusclasses.extension.sendDebugMessage

/**
 * All of the effects of the Builder class
 */
@Suppress("unused")
class BuilderEffects : Listener {
    fun register() {
        Bukkit.getServer().pluginManager.registerEvents(this, NexusClasses.instance!!)
        Bukkit.getScheduler().let {
            it.runTaskTimer(NexusClasses.instance!!, this::burnInSunTask, 0, 20) // Once per second
            it.runTaskTimer(NexusClasses.instance!!, this::helmetDegradeTask, 0, 1200) // Once per minute
        }
    }

    // Perk: Inhibit fall damage
    //FIXME: not working on CMURPGA server, plugin conflict?
    @EventHandler(priority = EventPriority.HIGHEST)
    fun cancelFallDamage(event: EntityDamageEvent) {
        val entity = event.entity
        if (
            entity is Player
            && entity.nexusClass == NexusClass.Builder
            && event.cause == EntityDamageEvent.DamageCause.FALL
        ) {
            event.isCancelled = true
            entity.sendDebugMessage("Builder perk: Fall damage cancelled!")
        }
    }

    // Perk: Transmute blocks
    @EventHandler
    fun transmute(event: PlayerInteractEvent) {
        // Only trigger when block right-clicked with a class-item stick
        if (
            event.action == Action.RIGHT_CLICK_BLOCK
            && event.hand == EquipmentSlot.HAND
            && event.item?.type == Material.STICK
            && event.item?.isClassItem == true
        ) {
            // Transmute if builder
            if (event.player.nexusClass == NexusClass.Builder) {
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
                    // Play block break sound
                    block.world.playSound(
                        block.location,
                        Sound.BLOCK_ENCHANTMENT_TABLE_USE,
                        1.0f,
                        1.0f
                    )

                    event.player.sendDebugMessage("Builder perk: Block transmuted!")
                }
            }
            // If not builder, delete item
            else {
                event.player.inventory.itemInMainHand.amount = 0
            }
        }
    }

    // Weakness: burn in sunlight when not wearing a helmet
    private fun burnInSunTask() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Builder
                    && player.location.block.lightFromSky >= 15 // no block above head
                    && player.equipment?.helmet == null // doesn't have a helmet
                    && (player.world.time >= 23460 || player.world.time <= 12535) // same time as zombies
        }.forEach { player ->
            player.fireTicks = 40
            player.sendDebugMessage("Builder weakness: burning in sunlight")
        }
    }

    // Weakness: helmet degrades in sunlight
    private fun helmetDegradeTask() {
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

            player.sendDebugMessage("Builder weakness: helmet degrading in sunlight")
        }
    }
}