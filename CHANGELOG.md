# Changelog

This file is here in the hopes of recording all changes made throughout the plugins history and future, to make it easier for users and developers to understand the changes between versions.

This project uses the versioning format {phase}.{major}.{minor}, where phase is pretty much never going to change, major is for backwards incompatible changes, and minor is for backwards compatible bug fixes.

## [Unreleased]
### Added
 - MIT License to the project.
 - Changelog to keep track of changes.  
### Changed
 - Renamed/moved a few classes to organise the project better. Shouldn't affect the API.
### Fixed
 - SkinLayers no longer get reset when a skin is being updated.

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
 - NPCBuilder class to make spawning NPC's using the API much easier. Info on how to use this new system can be found here (//TODO)
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

[//]: # (//TODO add changes for ALL versions since 1.0.0)
[//]: # (Refer to https://keepachangelog.com/en/1.0.0/ and example on wesbite)
[//]: # (Once this is done, update all release descriptions with the changelogs)

[Unreleased]: https://github.com/Scroojalix/NPCManager/compare/v1.4.0...master
[1.4.0]: https://github.com/Scroojalix/NPCManager/compare/v1.3.7...v1.4.0
[1.3.7]: https://github.com/Scroojalix/NPCManager/compare/v1.3.6...v1.3.7
[1.3.6]: https://github.com/Scroojalix/NPCManager/compare/v1.3.5...v1.3.6
[1.3.5]: https://github.com/Scroojalix/NPCManager/compare/v1.3.4...v1.3.5
[1.3.4]: https://github.com/Scroojalix/NPCManager/compare/v1.3.3...v1.3.4
[1.3.3]: https://github.com/Scroojalix/NPCManager/compare/v1.3.2...v1.3.3
