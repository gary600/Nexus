package xyz.gary600.nexusclasses

enum class NexusClass {
    // Normal Minecraft character
    Mundane,

    Builder,
    Miner,
    Warrior,
    Artist;

    companion object {
        // Parse from text
        fun parse(str: String?): NexusClass? = when (str?.lowercase()) {
            "mundane" -> Mundane
            "builder" -> Builder
            "miner" -> Miner
            "warrior" -> Warrior
            "artist" -> Artist
            else -> null
        }
    }
}