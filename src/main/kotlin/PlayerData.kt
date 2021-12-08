package xyz.gary600.nexusclasses

import org.bukkit.configuration.serialization.ConfigurationSerializable

class PlayerData : ConfigurationSerializable {
    var nexusClass = NexusClass.Mundane
    var debugMessages = false

    override fun serialize(): Map<String, Any> = mapOf(
        "nexusClass" to nexusClass.name,
        "debugMessages" to debugMessages
    )

    companion object {
        fun deserialize(map: Map<String, Any?>): PlayerData {
            val pd = PlayerData()
            pd.nexusClass = NexusClass.parse(map["nexusClass"] as String?) ?: NexusClass.Mundane
            pd.debugMessages = map["debugMessages"] as Boolean? ?: false

            return pd
        }
    }
}