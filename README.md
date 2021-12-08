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
Gives you the specific item used to activate a class's ability, if applicable (Builder and Artist)

### Hidden subcommand `/nexusclass debugMessages <yesno>`:
Enables or disables messages whenever a class ability activates. Very spammy, but useful for debugging

## Classes and implementation
Builder:
* no fall damage (`ClassesListener.builderNoFallDamage`) [TESTED]
* transmute blocks (`ClassesListener.builderTransmute`) [TESTED]
* burn in sunlight w/o helmet (`BuilderSunlightWeaknessTask`) [TESTED]
* degrade helment in sunlight (`BuilderHelmetDegradeTask`) [TESTED]

Miner:
* free emerald from mining some ores (`ClassesListener.minerFreeEmerald`) [TESTED]
* night vision below y=60 (`MinerNightVisionTask`) [TESTED]
* extra damage from zombies (`ClassesListener.minerZombieWeakness`) [TESTED]

Warrior:
* gold weapons light enemies on fire and give Strength II (`ClassesListener.warriorGoldWeapons`) [TESTED]
* wearing gold armor removes fire/lava damage (`ClassesListener.warriorFireResist`) [TESTED]
* holding iron weapon gives mining fatigue, wearing iron armor gives slowness (`WarriorIronAllergyTask`) [TESTED]

Artist:
* free ender pearl at all times (`ClassesListener.artistFreeEndPearl`) [TESTED]
* take damage in water (`ArtistWaterAllergyTask`) [TESTED]