package xyz.gary600.nexus

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.util.UUID

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
    fun save() {
        // create data folder if needed
        if (!Nexus.plugin.dataFolder.exists()) {
            Nexus.plugin.dataFolder.mkdirs()
        }

        Nexus.plugin.configFile.writeText(Nexus.json.encodeToString(this))
    }

    companion object {
        fun load(): Config {
            // If no file, create default and save
            if (!Nexus.plugin.configFile.exists()) {
                return Config().also { it.save() }
            }

            return Nexus.json.decodeFromString(Nexus.plugin.configFile.readText())
        }
    }
}