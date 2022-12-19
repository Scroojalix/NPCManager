# Changelog

This file is here in the hopes of recording all changes made throughout the plugins history and future, to make it easier for users and developers to understand the changes between versions.

This project uses the versioning format {phase}.{major}.{minor}, where phase is pretty much never going to change, major is for backwards incompatible changes, and minor is for backwards compatible bug fixes. There may be some versions that are named incorrectly, as I was a bit careless at the start of this project.

## [Unreleased]
### Added
- support for 1.18

## [1.5.1]
### Added
- Option to store NPC data using TOML syntax
### Fixed
- disableLogging() for MongoDB storage getting called twice
- MySQL servers not saving NPC changes
- NoSuchMethodError on 1.17.1 servers
- Adding dependencies to classpath on Java 9+ not working 

## [1.5.0] - 2021-6-22
### Added
- New serialisation/deserialisation system to make adding new storage methods easier.
### Changed
- Refactored some code.
### Renamed
- renamed io.github.scroojalix.npcmanager.utils to io.github.scroojalix.npcmanager.common
- moved api package to io.github.scroojalix.npcmanager.api
### Fixed
- ItemStack meta now gets serialised and deserialised correctly.
- Some spelling mistakes.

## [1.4.5] - 2021-6-17
### Added
- support for the plugin to work on 1.17 servers.

## [1.4.4] - 2021-6-9
### Added
- Temporary workaround for disabling MongoDB logging.
### Fixed
- NPC's not restoring if the storage method used is not remote.
- "Restoring NPC's..." message getting sent twice if JSON storage is used.

## [1.4.3] - 2021-2-2
### Added
- Dependency Manager that downloads dependencies into a cache folder and remaps them for use within the plugin. Will be used to implement more storage methods.
- `MongoDB` as a new storage method.
### Changed
- Better temp storage system. Each storage type has its own temp storage folder.

## [1.4.2] - 2021-1-10
### Added
- Rename command
- Option in config to fetch skin data using the NPC's name when creating an NPC.
- Option in config to make NPC's have better head rotation by making them perform the punch animation.
- Option in config to change the length of the randomised string for an NPC's tab list name.
- Option in config to change how long the plugin should wait in ticks before removing an NPC from the tab list.
### Changed
- If attempting to get skin data from an online player, it now extracts it from their game profile, rather than through the Mojang API, to speed it up dramatically.
- Can now reset skin layers by running `/npc modify <npc> skinLayers --reset`.
- The type of Skin (URL/PLAYER) and UUID is now stored with the skin to make it easier to determine if a skin needs updating.
- Can now reload a specific by running `/npc reload <npc>`.
- Some of the confirmation messages. eg. `Removed an NPC named <npc>` -> `Removed <npc>`.
- Better implementation for storage methods, making it easier to add new storage methods.
### Fixed
- When the distance to an NPC is 0, the head rotation was weird.
- Issue with modify command displaying all modifications if the input NPC doesn't exist.
- Issue when NPC's had the same name as a player, it caused the player to be put in the NPC scoreboard team, which gave them the `[NPC]` prefix and a gray name. This was fixed by randomly generating a 16 character string for the NPC's tablist name.
- Cancelled all remove tasks for an NPC before cancelling the main loader task.

## [1.4.1] - 2021-1-2
### Added
- MIT License to the project.
- Changelog to keep track of changes.
- Optional argument to the create command to prevent the NPC from being saved, meaning when the server reloads, the NPC will disappear.
- `setSkinLayers` method to API.
- All of the NPC modifications now have their own method in the API.
### Changed
- Renamed/moved a few classes to organise the project better. Shouldn't affect the API.
- Custom Interact Event names can now contain underscores.
- Better update checker. Now uses GitHub API, rather than getting version from looking at raw files.
- Equipment object is now not initialised until a modification to an NPC's equipment is made.
- Some API methods were changed to take in an input of an enum instead of a string.
### Fixed
- Skin Layers no longer get reset when a skin is being updated.
- Skin Layers get displayed correctly in info command.
- When setting an NPC's equipment through the API, some items were being classed as suitable, when in reality they are not.
- Setting the skin of an NPC using NPCBuilder. Now waits for the NPC to be spawned, then fetches and applies skin data.
- Check for plugin updates when running `/npc reload`

## [1.4.0] - 2020-12-27
### Added
- SkinLayers modification to customise what layers of a skin are visible.
- Ability to get skin data from a direct image URL, or from a players username.
- Option to keep an NPC's skin up to date with a player's actual skin, if the skin is retrieved from a username.
### Changed
- Skin data (texture and signature) is cached.
- Saving of NPC's is now done asynchronously.
- NPC names can now contain underscores.
- Some messages where reworded.
- changeEquipment method in API was changed to take in an EquipmentSlot enum, instead of an int. May cause some issues, as it is version dependent, but its fine because no one uses the API anyway.
- Temp files now get stored as JSON files in the directory `<plugin folder>/json-storage/temp`
- Moved some internal classes to different packages to organise the project a bit better. Shouldn't cause any issues.
### Removed
- YAML save method because JSON using the GSON library is better and easier.
- skins.yml because of the changes to the skin system.
- FileManager class because it is no longer required.
### Fixed
- NPC's attempting to restore before database is connected.

## [1.3.7] - 2020-12-23
### Changed
- If an NPC has invalid JSON when restoring, it will automatically be removed from storage.
- Better framework for modification commands. (Makes it easier for future releases)
- Better save system for interact events.
- If invalid arguments are used in the API, an IllegalArgumentException is thrown, rather than being logged to the console.
- Interact Events shown in NPC info command now have a prefix for the type of InteractEvent (Command/Custom)
### Removed
- Old system for saving interact events.
### Fixed
- Tab Completion for some commands was incorrect.
- Color codes were removed from messages for cleaner printing to console.

## [1.3.6] - 2020-12-22
### Added
- NPCBuilder class to make spawning NPC's using the API much easier. Info on how to use this new system can be found [here](https://scroojalix.github.io/projects/NPCManager/wiki/).
- Added `spawnNPC(NPCData)` method to API. Use this to spawn NPC's generated with NPCBuilder.
### Changed
- Connecting to database is now done asynchronously.
- Tab Complete code made tidier and easier to implement for future commands.
- The `/npc list` command was redone to be more detailed and look more appealing.
- Now any item can be placed in an NPC's helmet slot.
### Fixed
- Use different method of reading a file to a string because previous method did not exist on older server versions.
- When spawning NPC, their location is rounded off to two decimal places in a language independent way.

## [1.3.5] - 2020-12-17
### Changed
- The interactEvent and equipment commands were moved into subcommands of the modify command.
- Command descriptions were made more informative.
- NPC names now have to be alphanumerical.
### Fixed
- Messages containing meta data (click event/hover event) will now dislay the correct console when sent to console.
- Tab Completion was fixed for some commands.
### Removed
- Some debug messages that were used for testing, but are no longer needed.

## [1.3.4] - 2020-12-11
### Added
- An Update Checker, which prints a message to the console if a new version is available on GitHub.

## [1.3.3] - 2020-12-11
### Added
- Option to save NPC's using JSON.

## [1.3.2] - 2020-12-5
### Changed
- The revision property is now used in each of the Maven Modules, making it easy to change versions.
- The Info Command has been finished/fixed. Now displays all of an NPC's information.

## [1.3.1] - 2020-12-4
### Changed
- Usage message for some commands.
- Redone interactEvent modification. Now have the option to use commands when interacting with a player.
### Fixed
- Error when not typing the required amount of arguments for modify command.

## [1.3.0] - 2020-12-1
### Added
- NPC modification to customise an NPC's equipment.
- Clickable message in info command message that opens the NPC's equipment menu.
- Page argument to info command.
- changeEquipment(String name, int slot, ItemStack item) method to API.
### Changed
- Moved some classes around to organise the project.

## [1.2.2] - 2020-11-22
### Changed
- The command framework, making it way easier to add more commands in the future.
- Organised the project a bit (moving classes to different packages etc)

## [1.2.1] - 2020-11-21
### Added
- `[NPC]` prefix in tab for NPC's
### Fixed
- NPC names not appearing gray in tablist on some server versions.

## [1.2.0] - 2020-11-9
### Added
- Support for MC Version 1.16.4.
### Changed
- Converted the project to a multi module maven project to make it easy to build the jar.
### Fixed
- Some debug messages not showing even if `show-debug-messages` is set to true in the config.

## [1.1.0] - 2020-11-5
### Changed
- NPC's now have the option to not be saved to a file/database, meaning after the plugin reloads they will be gone.
- NPC capes have been hidden. Might be changed later down the line.
### Fixed
- NPC changes now actually get saved.

## [1.0.2] - 2020-11-2
### Changed
- Some code was redone to be more versatile.
- When saving to YAML, NPCData gets parsed into a JSON string. I will probably end up replacing YAML saving with JSON at some point because now it is useless.
- NPC's get saved everytime a modification is made to them, rather than just when the plugin is disabling.
### Fixed
- Plugin properly disconnects from database on disable.
- NPC's get updated after changes are made to them from the API.

## [1.0.1] - 2020-10-31
### Added
- Support for all versions since 1.8.3
- Ability to hide holograms.
### Fixed
- Plugin was attempting to initialise when run on an invalid server version (prior to 1.8.3)
- Fixed NoSuchMethodError on older server versions.
### Removed
- Some unnecessary files.

## [1.0.0] - 2020-10-27
Initial Commit. No Changes.

[Unreleased]: https://github.com/Scroojalix/NPCManager/compare/v1.5.1...master
[1.5.1]: https://github.com/Scroojalix/NPCManager/compare/v1.5.0...v1.5.1
[1.5.0]: https://github.com/Scroojalix/NPCManager/compare/v1.4.5...v1.5.0
[1.4.5]: https://github.com/Scroojalix/NPCManager/compare/v1.4.4...v1.4.5
[1.4.4]: https://github.com/Scroojalix/NPCManager/compare/v1.4.3...v1.4.4
[1.4.3]: https://github.com/Scroojalix/NPCManager/compare/v1.4.2...v1.4.3
[1.4.2]: https://github.com/Scroojalix/NPCManager/compare/v1.4.1...v1.4.2
[1.4.1]: https://github.com/Scroojalix/NPCManager/compare/v1.4.0...v1.4.1
[1.4.0]: https://github.com/Scroojalix/NPCManager/compare/v1.3.7...v1.4.0
[1.3.7]: https://github.com/Scroojalix/NPCManager/compare/v1.3.6...v1.3.7
[1.3.6]: https://github.com/Scroojalix/NPCManager/compare/v1.3.5...v1.3.6
[1.3.5]: https://github.com/Scroojalix/NPCManager/compare/v1.3.4...v1.3.5
[1.3.4]: https://github.com/Scroojalix/NPCManager/compare/v1.3.3...v1.3.4
[1.3.3]: https://github.com/Scroojalix/NPCManager/compare/v1.3.2...v1.3.3
[1.3.2]: https://github.com/Scroojalix/NPCManager/compare/v1.3.1...v1.3.2
[1.3.1]: https://github.com/Scroojalix/NPCManager/compare/v1.3.0...v1.3.1
[1.3.0]: https://github.com/Scroojalix/NPCManager/compare/v1.2.2...v1.3.0
[1.2.2]: https://github.com/Scroojalix/NPCManager/compare/v1.2.1...v1.2.2
[1.2.1]: https://github.com/Scroojalix/NPCManager/compare/v1.2.0...v1.2.1
[1.2.0]: https://github.com/Scroojalix/NPCManager/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/Scroojalix/NPCManager/compare/v1.0.2...v1.1.0
[1.0.2]: https://github.com/Scroojalix/NPCManager/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/Scroojalix/NPCManager/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/Scroojalix/NPCManager/commit/6796697b5c20409b843e96dc817dde5a0c256c2d
