package xyz.gary600.nexus

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.io.File
import java.util.UUID

@Serializable
data class PlayerData(
    /**
     * This player's class
     */
    var nexusClass: NexusClass = NexusClass.Mundane,

    /**
     * Whether or not this player has subscribed to debug messages
     */
    var debug: Boolean = false,

    /**
     * This player's Spore level
     */
    var sporeLevel: Double = 0.0
) {
    /**
     * Saves this PlayerData to its corresponding file
     */
    fun save(uuid: UUID) {
        // Create the player data folder if it doesn't exist yet
        if (!Nexus.plugin.playerDataFolder.exists()) {
            Nexus.plugin.playerDataFolder.mkdirs()
        }
        // Store serialized
        File(Nexus.plugin.playerDataFolder, "$uuid.json").writeText(
            Nexus.json.encodeToString(this)
        )
    }

    companion object {
        fun load(uuid: UUID): PlayerData? {
            val file = File(Nexus.plugin.playerDataFolder, "$uuid.json")

            // Null if the player data file doesn't exist
            if (!file.exists()) {
                return null
            }

            // Deserialize (Kotlin should fill in defaults)
            //TODO: Handle deserialization exceptions
            return Nexus.json.decodeFromString<PlayerData>(file.readText())
        }
    }
}