package xyz.gary600.nexus

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.util.UUID

/**
 * A serializer to allow kotlinx.serialization to handle Java UUIDs
 *
 * This object was modified from Ben Butterworth on SO, CC-BY-SA
 */
object UUIDSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: UUID) { encoder.encodeString(value.toString()) }
}

/**
 * Defer the action using the task scheduler
 */
fun defer(delay: Long = 1, block: () -> Unit): BukkitTask {
    return Bukkit.getScheduler().runTaskLater(
        Nexus.plugin,
        Runnable { block() },
        delay
    )
}