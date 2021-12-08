package xyz.gary600.nexusclasses.effects

import org.bukkit.Bukkit
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.NexusClasses
import xyz.gary600.nexusclasses.extension.nexusClass
import xyz.gary600.nexusclasses.extension.sendDebugMessage

class MinerNightVisionTask : BukkitRunnable() {
    // Miner perk: free night vison below y=60
    override fun run() {
        Bukkit.getServer().onlinePlayers.filter { player ->
            player.nexusClass == NexusClass.Miner
            && player.location.y <= 60.0 // below y=60
        }.forEach { player ->
            // Set potion effect for 11 seconds (less than 10 seconds causes a warning blinking)
            player.addPotionEffect(PotionEffect(
                PotionEffectType.NIGHT_VISION,
                220,
                0,
                false,
                false,
                false
            ))
            player.sendDebugMessage("[NexusClasses] Miner perk: Free night vision")
        }
    }
}