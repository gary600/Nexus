package xyz.gary600.nexus.classes.effects

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.gary600.nexus.Effects
import xyz.gary600.nexus.TimerTask
import xyz.gary600.nexus.classes.NexusClass
import xyz.gary600.nexus.classes.itemNexusClass
import xyz.gary600.nexus.classes.nexusClass
import xyz.gary600.nexus.nexusEnabled
import xyz.gary600.nexus.nexusDebug

/**
 * All of the effects of the Miner class
 */
@Suppress("unused")
object MinerEffects : Effects() {
    // Perk: Certain ores additionally drop emerald
    @EventHandler
    fun freeEmerald(event: BlockBreakEvent) {
        if (
            event.player.nexusClass == NexusClass.Miner
            && event.player.world.nexusEnabled
            && event.player.gameMode != GameMode.CREATIVE // Don't drop for creative mode players
            && !event.player.inventory.itemInMainHand.containsEnchantment(Enchantment.SILK_TOUCH) // oopsie
            && event.block.type in arrayOf(
                Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE,
                Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
                Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE,
                Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE
            )
        ) {
            // We're not allowed to add items to the block drop list for some reason,
            // so just drop it manually where the block is
            event.block.world.dropItemNaturally(event.block.location, ItemStack(Material.EMERALD, 1))
            event.player.nexusDebug("Miner perk: Free emerald")
        }
    }

    // Perk: free night vison when wearing headlamp below y=60
    @TimerTask(0, 10)
    fun nightVisionTask() {
        // Give night vision to Miners wearing a headlamp (Miner-classed leather helmet) below y=60
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Miner
            && player.world.nexusEnabled
            && player.location.y <= 60.0 // below y=60
            && player.equipment?.helmet?.let {
                it.type == Material.LEATHER_HELMET
                && it.itemNexusClass == NexusClass.Miner
            } == true
        }.forEach { player ->
            // Set night vision potion effect for 11 seconds (less than 10 seconds causes a warning blinking)
            player.addPotionEffect(PotionEffect(
                PotionEffectType.NIGHT_VISION,
                219,
                0,
                false,
                false,
                false
            ))
            player.nexusDebug("Miner perk: Free night vision")
        }
    }

    // Perk: free haste below y=60
    @TimerTask(0, 10)
    fun hasteTask() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Miner
            && player.world.nexusEnabled
            && player.location.y <= 60.0
        }.forEach { player ->
            player.addPotionEffect(PotionEffect(
                PotionEffectType.FAST_DIGGING,
                19,
                0, // haste 1
                false,
                false,
                false
            ))
            player.nexusDebug("Miner perk: Free haste")
        }
    }

    // Weakness: Extra damage from zombies
    @EventHandler
    fun zombieWeakness(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        if (
            entity is Player
            && entity.nexusClass == NexusClass.Miner
            && entity.world.nexusEnabled
            && event.damager is Zombie
        ) {
            event.damage *= 1.2
            entity.nexusDebug("Miner weakness: increased damage from zombies")
        }
    }
}