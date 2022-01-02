package xyz.gary600.nexus.corruption

import org.bukkit.entity.Player
import xyz.gary600.nexus.Nexus
import xyz.gary600.nexus.nexusDebug
import xyz.gary600.nexus.playerData


/**
 * Get or set this player's Corruption level
 */
var Player.corruption: Int
    get() {
        val obj = scoreboard.getObjective(Nexus.config.corruptionScoreboard)
        if (obj == null) {
            nexusDebug("Corruption objective `deaths` doesn't exist!")
        }
        return obj?.getScore(name)?.score ?: 0
    }
    set(x) {
        scoreboard.getObjective(Nexus.config.corruptionScoreboard)?.getScore(name)?.score = x
    }

/**
 * Get this player's Corruption tier
 */
inline val Player.corruptionTier: CorruptionTier
    get() =
        if (corruption >= 150) {
            CorruptionTier.Tier3
        }
        else {
            when (corruption) {
                in 50..99 -> CorruptionTier.Tier1
                in 100..149 -> CorruptionTier.Tier2
                else -> CorruptionTier.Tier0
            }
        }

/**
 * Get or set the maximum Corruption tier this player has ever reached
 */
inline var Player.maxCorruptionTier: CorruptionTier
    get() = playerData.maxCorruptionTier
    set(x) {
        playerData.maxCorruptionTier = x
        playerData.save(uniqueId)
    }

/**
 * Get or set this player's progress toward healing a Corruption level
 */
inline var Player.corruptionHealProgress: Int
    get() = playerData.corruptionHealProgress
    set(x) {
        playerData.corruptionHealProgress = x
        playerData.save(uniqueId)
    }