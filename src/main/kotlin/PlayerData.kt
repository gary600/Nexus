package xyz.gary600.nexusclasses

import org.bukkit.configuration.serialization.ConfigurationSerializable

class PlayerData : ConfigurationSerializable {
    var _class = Class.Mundane

    override fun serialize(): Map<String, String> {
        return mapOf("class" to _class.name)
    }
}