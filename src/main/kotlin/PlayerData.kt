package xyz.gary600.nexusclasses

import org.bukkit.configuration.serialization.ConfigurationSerializable

class PlayerData : ConfigurationSerializable {
    var nexusClass = NexusClass.Mundane
    var debugMessages = false

    override fun serialize(): Map<String, String> = mapOf(
        "class" to nexusClass.name,
        "debugMessages" to debugMessages.toString()
    )
}