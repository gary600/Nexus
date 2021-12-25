package xyz.gary600.nexusclasses

import org.bukkit.configuration.serialization.ConfigurationSerializable

class PlayerData : ConfigurationSerializable {
    /**
     * This player's class
     */
    var nexusClass = NexusClass.Mundane

    /**
     * Whether or not this player has subscribed to debug messages
     */
    var debugMessages = false

    /**
     * This player's Spore level
     */
    var sporeLevel = 0.0

    override fun serialize(): Map<String, Any> = mapOf(
        "nexusClass" to nexusClass.name,
        "debugMessages" to debugMessages,
        "sporeLevel" to sporeLevel
    )

    companion object {
        fun deserialize(map: Map<String, Any?>): PlayerData {
            val pd = PlayerData()
            pd.nexusClass = NexusClass.fromString(map["nexusClass"] as? String?) ?: NexusClass.Mundane
            pd.debugMessages = map["debugMessages"] as? Boolean? ?: false
            pd.sporeLevel = map["sporeLevel"] as? Double? ?: 0.0

            return pd
        }
    }
}