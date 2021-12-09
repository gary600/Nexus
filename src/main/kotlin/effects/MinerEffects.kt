package xyz.gary600.nexusclasses.effects

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.NexusClasses
import xyz.gary600.nexusclasses.extension.nexusClass
import xyz.gary600.nexusclasses.extension.sendDebugMessage

/**
 * All of the effects of the Miner class
 */
@Suppress("unused")
class MinerEffects : Listener {
    fun register() {
        Bukkit.getServer().pluginManager.registerEvents(this, NexusClasses.instance!!)
        Bukkit.getScheduler().runTaskTimer(NexusClasses.instance!!, this::nightVisionTask, 0, 10)
    }

    // Perk: Certain ores additionally drop emerald
    @EventHandler
    fun freeEmerald(event: BlockBreakEvent) {
        if (
            event.player.nexusClass == NexusClass.Miner
            && event.player.gameMode != GameMode.CREATIVE // Don't drop for creative mode players
            && event.block.type in arrayOf(
                Material.GOLD_ORE,
                Material.DEEPSLATE_GOLD_ORE,
                Material.LAPIS_ORE,
                Material.DEEPSLATE_LAPIS_ORE,
                Material.REDSTONE_ORE,
                Material.DEEPSLATE_REDSTONE_ORE,
                Material.DIAMOND_ORE,
                Material.DEEPSLATE_DIAMOND_ORE
            )
        ) {
            // We're not allowed to add items to the block drop list for some reason, so just drop it manually where the block is
            event.block.world.dropItemNaturally(event.block.location, ItemStack(Material.EMERALD, 1))
            event.player.sendDebugMessage("Miner perk: Free emerald!")
        }
    }

    // Weakness: Extra damage from zombies
    @EventHandler
    fun zombieWeakness(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        if (
            entity is Player
            && entity.nexusClass == NexusClass.Miner
            && event.damager is Zombie
        ) {
            event.damage *= 1.2
            entity.sendDebugMessage("Miner weakness: double damage from zombies!")
        }
    }

    // Perk: free night vison below y=60
    private fun nightVisionTask() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Miner
                    && player.location.y <= 60.0 // below y=60
        }.forEach { player ->
            // Set potion effect for 11 seconds (less than 10 seconds causes a warning blinking)
            player.addPotionEffect(
                PotionEffect(
                PotionEffectType.NIGHT_VISION,
                220,
                0,
                false,
                false,
                false
            )
            )
            player.sendDebugMessage("Miner perk: Free night vision")
        }
    }
}