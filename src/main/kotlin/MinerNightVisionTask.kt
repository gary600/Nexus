package xyz.gary600.nexusclasses

import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

class MinerNightVisionTask(private var plugin: NexusClasses) : BukkitRunnable() {
    override fun run() {
        plugin.server.onlinePlayers.filter { p ->
            plugin.getPlayerData(p.uniqueId).nexusClass == NexusClass.Miner
            && p.location.y <= 60.0 // below y=60
        }.forEach { p ->
            // Set potion effect for 11 seconds (less than 10 seconds causes a warning flicker
            p.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, 220, 0, false, false, false))
            plugin.sendDebugMessage(p, "[NexusClasses] Miner perk: Free night vision")
        }
    }
}