package xyz.gary600.nexus

import kotlinx.serialization.Serializable
import java.util.*
import kotlin.collections.HashSet

/**
 * The global Nexus configuration
 */
@Serializable
class Config(
    /**
     * The set of worlds in which Nexus is enabled
     */
    val enabledWorlds: HashSet<@Serializable(with = UUIDSerializer::class) UUID> = HashSet(),

    /**
     * The scoreboard objective containing the player corruption value
     */
    val corruptionScoreboard: String = "deaths"
) {
    companion object {
        fun load() {
            TODO()
        }
    }
}