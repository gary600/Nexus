# NexusClasses
This is a custom Bukkit/Spigot plugin written specifically for CMURPGA's
Nexus Minecraft RP. It manages custom buffs and debuffs based on the
player's chosen class.

## Command: `/nexusclass`, alias `/class`
### Subcommand `/nexusclass choose <class>` (requires permission `nexusclasses.choose`):
Selects your own class

### Subcommand `/nexusclass set <class> <player>` (requires permission `nexusclasses.set`):
Sets another player's class

### Subcommand `/nexusclass get [<player>]`:
Gets your own class or another player's class

### Subcommand `/nexusclass item`:
Gives you the specific item (the "class item") used to activate a class's ability, if applicable (Builder and Artist).
Class items cannot be removed from your inventory.

### Hidden subcommand `/nexusclass debugMessages <yesno>`:
Enables or disables messages whenever a class ability activates. Very spammy, but useful for debugging

## Classes and implementation
Builder (`NexusClass.Builder`, effects in `effects.BuilderEffects`):
* no fall damage [FIXME]
* transmute blocks
* burn in sunlight w/o helmet
* degrade helment in sunlight

Miner (`NexusClass.Miner`, effects in `effects.MinerEffects`):
* free emerald from mining some ores
* night vision below y=60
* extra damage from zombies

Warrior (`NexusClass.Warrior`, effects in `effects.WarriorEffects`):
* gold weapons light enemies on fire and give Strength II
* wearing gold armor removes fire/lava damage [FIXME]
* holding iron weapon gives mining fatigue, wearing iron armor gives slowness

Artist (`NexusClass.Artist`, effects in `effects.ArtistEffects`):
* free ender pearl at all times [FIXME]
* take damage in water

## License
This plugin is licensed under the Apache 2.0 license.