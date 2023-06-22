package io.github.scroojalix.npcmanager.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.storage.misc.JsonParser;
import io.github.scroojalix.npcmanager.storage.misc.StorageImplementation;
import io.github.scroojalix.npcmanager.storage.misc.StorageImplementation.LocalStorage;
import io.github.scroojalix.npcmanager.storage.misc.StorageImplementation.RemoteStorage;
import io.github.scroojalix.npcmanager.utils.Messages;

public class Storage {

    private final NPCMain main;
    private final StorageImplementation implementation;

    public Storage(NPCMain main, StorageImplementation implementation) {
        this.main = main;
        this.implementation = implementation;
    }
    
    public void init() {
        if (implementation instanceof LocalStorage) {
            restoreAllNPCs();
            return;
        }
        this.main.sendDebugMessage(Level.INFO, "Initialising storage implementation");
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            if(((RemoteStorage) implementation).init()) {
                main.sendDebugMessage(Level.INFO, "Successfully connected to database.");
            } else {
                main.getLogger().severe(Messages.DATABASE_NOT_CONNECTED);
                main.getLogger().severe("Temp storage will be used instead.");
            }
            restoreAllNPCs();
        });
    }
    
    public void shutdown() {
        if (!(implementation instanceof RemoteStorage)) return;
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            if (((RemoteStorage) implementation).shutdown()) {
                this.main.sendDebugMessage(Level.INFO, "Sucessfully shut down storage implementation");
            } else {
                this.main.getLogger().log(Level.SEVERE, "Failed to shutdown storage implementation");
            }
        });
    }
    
    public void saveNPC(NPCData data) {
        if (!data.isStored()) return;

        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            if (implementation.saveNPC(data)) {
                main.sendDebugMessage(Level.INFO, "Successfully saved "+data.getName()+" to storage.");
            } else {
                if (implementation instanceof RemoteStorage) {
                    main.getLogger().severe(Messages.DATABASE_NOT_CONNECTED);
                    main.getLogger().warning("Saving an NPC to temp storage.");
                    File jsonFile = new File(main.getDataFolder()+"/temp-"+implementation.getImplementationName(), data.getName()+".json");
                    try {
                        jsonFile.getParentFile().mkdirs();
                        if (jsonFile.createNewFile()) {
                            FileWriter writer = new FileWriter(jsonFile);
                            writer.write(JsonParser.toJson(data, true));
                            writer.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    main.getLogger().log(Level.SEVERE, "Failed to save an NPC to storage.");
                }
            }
        });
    }
    
    public void removeNPC(@Nonnull String name) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            if (implementation.removeNPC(name)) {
                main.sendDebugMessage(Level.INFO, "Successfully removed \""+name+"\" from storage.");
            } else {
                if (implementation instanceof RemoteStorage) {
                    main.getLogger().severe(Messages.DATABASE_NOT_CONNECTED);
                    File tempFile = new File(main.getDataFolder()+"/temp-"+implementation.getImplementationName(), name+".json");
                    if (tempFile.exists()) {
                        main.getLogger().warning("Removing an NPC from temp storage.");
                        tempFile.delete();
                        File tempDir = new File(main.getDataFolder()+"/temp-"+implementation.getImplementationName());
                        if (tempDir.list().length == 0) {
                            tempDir.delete();
                        }
                    }
                } else {
                    main.getLogger().log(Level.SEVERE, "Failed to remove an NPC from storage.");
                }
            }
        });
    }
    
    public void restoreAllNPCs() {
        main.sendDebugMessage(Level.INFO, Messages.RESTORE_NPCS);
        if (implementation instanceof RemoteStorage) {
            // Call this synchronously
            Bukkit.getScheduler().runTask(main, () -> {
                if (((RemoteStorage)implementation).restoreAllNPCs()) {
                    main.sendDebugMessage(Level.INFO, "Successfully restored NPCs from remote storage.");
                } else {
                    main.getLogger().log(Level.SEVERE, "Failed to restore NPC's.");
                }
    
                //Check temp storage
                File tempDir = new File(main.getDataFolder()+"/temp-"+implementation.getImplementationName());
                if (tempDir.exists()) {
                    main.sendDebugMessage(Level.INFO, "Restoring NPC's from temp storage...");
                    restoreTempNPCs();
                }
            });
        } else {
            LocalStorage storage = (LocalStorage) implementation;
            File folder = new File(main.getDataFolder() + "/" + storage.getFileExtension()+"-storage");
            if (!folder.exists()) return; // No NPC's stored

            File[] npcFiles = folder.listFiles();
            if (npcFiles == null) return;

            for (int i = 0; i < npcFiles.length; i++) {
                File current = npcFiles[i];
                if (current.isFile() && current.getName().endsWith("."+storage.getFileExtension())) {
                    if (storage.restoreNPC(current)) {
                        main.sendDebugMessage(Level.INFO, "Successfully restored NPC Data from " + current.getName());
                    } else {
                        main.sendDebugMessage(Level.SEVERE, "An issue arose when restoring NPC Data from " + current.getName());
                    }
                }
            }
        }
    }

    private void restoreTempNPCs() {
		File tempStorage = new File(main.getDataFolder()+"/temp-"+implementation.getImplementationName());
		File[] tempFiles = tempStorage.listFiles();
		if (tempFiles != null) {
			for (int i = 0; i < tempFiles.length; i++) {
				File current = tempFiles[i];
				if (current.isFile() && current.getName().endsWith(".json")) {
					try {
						String json = new String(Files.readAllBytes(Paths.get(current.getPath())));
						NPCData data = JsonParser.fromJson(current.getName().replace(".json", ""), json, true);
                        String name = data.getName();
                        if (name == null) continue;
                        boolean restore = true;
                        if (((RemoteStorage)implementation).isConnected()) {
                            if (!((RemoteStorage)implementation).exists(name)) {
                                saveNPC(data);
                            } else {
                                main.sendDebugMessage(Level.WARNING, "Could not merge NPC from temp storage to the database because an NPC with the same name already exists in the database.");
                                restore = false;
                            }
                            current.delete();
                        }
                        if (restore) {
                            main.npc.spawnNPC(data);
                        }
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
        }
        if (tempStorage.listFiles().length == 0) tempStorage.delete();
    }
}
