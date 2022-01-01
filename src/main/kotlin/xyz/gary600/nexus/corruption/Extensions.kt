package xyz.gary600.nexus.corruption

import org.bukkit.entity.Player
import xyz.gary600.nexus.nexusDebug


/**
 * Get or set this player's Corruption level
 */
inline var Player.corruption: Int
    //TODO: make scoreboard objective name configurable
    get() {
        val obj = scoreboard.getObjective("deaths")
        if (obj == null) {
            nexusDebug("Corruption objective `deaths` doesn't exist!")
        }
        return obj?.getScore(name)?.score ?: 0
    }
    set(x) {
        scoreboard.getObjective("deaths")?.getScore(name)?.score = x
    }

inline val Player.corruptionTier: CorruptionTier
    get() = if (corruption >= 150) CorruptionTier.Tier3
    else when(corruption) {
        in 50..99 -> CorruptionTier.Tier1
        in 100..149 -> CorruptionTier.Tier2
        else -> CorruptionTier.Tier0
    }

enum class CorruptionTier { Tier0, Tier1, Tier2, Tier3 }