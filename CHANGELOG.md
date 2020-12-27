# Changelog

This file is here in the hopes of recording all changes made throughout the plugins history and future, to make it easier for users and developers to understand the changes between versions.

This project uses the versioning format {phase}.{major}.{minor}, where phase is pretty much never going to change, major is for backwards incompatible changes, and minor is for backwards compatible bug fixes.

## [Unreleased]
### Changed
 - SkinLayers no longer get reset when a skin is being updated.

## [1.4.0] - 2020-12-27
### Added
 - SkinLayers modification to customise what layers of a skin are visible.
 - Ability to get skin data from a direct image URL, or from a players username.
 - Option to keep an NPC's skin up to date with a player's actual skin, if the skin is retrieved from a username.
### Changed
 - Skin data (texture and signature) is cached.
 - Saving and restoring of NPC's is now done asynchronously.
 - NPC names can now contain underscores.
 - Some messages where reworded.
 - changeEquipment method in API was changed to take in an EquipmentSlot enum, instead of an int. May cause some issues, as it is version dependent, but its fine because no one uses the API anyway.
 - Temp files now get stored as JSON files in the directory `<plugin folder>/json-storage/temp`
 - Moved some internal classes to different packages to organise the project a bit better. Shouldn't cause any issues.
### Removed
 - YAML save method because JSON using the GSON library is better and easier.
 - skins.yml because of the changes to the skin system.
 - FileManager class because it is no longer required.

## [1.3.7] - 2020-12-23


[//]: # (//TODO add changes for ALL versions since 1.0.0)
[//]: # (Refer to https://keepachangelog.com/en/1.0.0/ and example on wesbite)

[Unreleased]: https://github.com/Scroojalix/NPCManager/compare/v1.4.0...HEAD
[1.4.0]: https://github.com/Scroojalix/NPCManager/compare/v1.3.7...v1.4.0
[1.3.7]: https://github.com/Scroojalix/NPCManager/compare/v1.3.6...v1.3.7
