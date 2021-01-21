package io.github.scroojalix.npcmanager.utils.storage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.chat.Messages;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.storage.implementation.interfaces.RemoteStorage;
import io.github.scroojalix.npcmanager.utils.storage.implementation.interfaces.StorageImplementation;

public class Storage {

    private final NPCMain main;
    private final StorageImplementation implementation;
    private final boolean remote;

    public Storage(NPCMain main, StorageImplementation implementation) {
        this.main = main;
        this.implementation = implementation;
        this.remote = implementation instanceof RemoteStorage;
    }
    
    public void init() {
        if (remote) {
            this.main.log(Level.INFO, "Initialising storage implementation");
            Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
                @Override
                public void run() {
                    try {
                        ((RemoteStorage) implementation).init();
                        main.log(Level.INFO, "Successfully connected to database.");
                    } catch(Throwable t) {
                        if (main.showDebugMessages) {
                            main.getLogger().log(Level.SEVERE, Messages.DATABASE_NOT_CONNECTED, t);
                        } else {
                            main.getLogger().severe(Messages.DATABASE_NOT_CONNECTED);
                        }
                        main.getLogger().severe("Temp storage will be used instead.");
                    }
                    restoreNPCs();
                }
            });
        }
    }
    
    public void shutdown() {
        if (remote) {
            try {
                ((RemoteStorage) implementation).shutdown();
                this.main.log(Level.INFO, "Sucessfully shut down storage implementation");
            } catch(Throwable t) {
                this.main.getLogger().log(Level.SEVERE, "Failed to shutdown storage implementation", t);
            }
        }
    }
    
    public void saveNPC(NPCData data) {
        if (data.isStored()) {
            Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
                @Override
                public void run() {
                    try {
                        implementation.saveNPC(data);
                    } catch(Throwable t) {
                        if (remote) {
                            main.getLogger().severe(Messages.DATABASE_NOT_CONNECTED);
                            main.getLogger().warning("Saving an NPC to temp storage.");
                            File jsonFile = new File(main.getDataFolder()+"/json-storage/temp-"+implementation.getImplementationName(), data.getName()+".json");
                            try {
                                jsonFile.getParentFile().mkdirs();
                                if (jsonFile.createNewFile()) {
                                    FileWriter writer = new FileWriter(jsonFile);
                                    writer.write(data.toJson(true));
                                    writer.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            main.getLogger().log(Level.SEVERE, "Failed to save an NPC to storage.", t);
                        }
                    }
                }
            });
        }
    }
    
    public void removeNPC(String name) {
        Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable(){
            @Override
            public void run() {
                try {
                    implementation.removeNPC(name);
                } catch(Throwable t) {
                    if (remote) {
                        main.getLogger().severe(Messages.DATABASE_NOT_CONNECTED);
                        File tempFile = new File(main.getDataFolder()+"/json-storage/temp-"+implementation.getImplementationName(), name+".json");
                        if (tempFile.exists()) {
                            main.getLogger().warning("Removing an NPC from temp storage.");
                            tempFile.delete();
                            File tempDir = new File(main.getDataFolder()+"/json-storage/temp-"+implementation.getImplementationName());
                            if (tempDir.list().length == 0) {
                                tempDir.delete();
                            }
                        }
                    } else {
                        main.getLogger().log(Level.SEVERE, "Failed to remove an NPC from storage.", t);
                    }
                }                
            }
        });
    }
    
    public void restoreNPCs() {
        Bukkit.getScheduler().runTaskLater(main, new Runnable() {
            @Override
            public void run() {
                try {
                    main.getLogger().info(Messages.RESTORE_NPCS);
                    implementation.restoreNPCs();
                } catch(Throwable t) {
                    if (!remote) {
                        main.getLogger().log(Level.SEVERE, "Failed to restore an NPC's.", t);
                    }
                } finally {
                    if (remote) {
                        File tempDir = new File(main.getDataFolder()+"/json-storage/temp-"+implementation.getImplementationName());
                        if (tempDir.exists()) {
                            main.log(Level.INFO, "Restoring NPC's from temp storage...");
                            restoreTempNPCs();
                        }
                    }
                }
            }
        }, 1l);
    }

    private void restoreTempNPCs() {
		File tempStorage = new File(main.getDataFolder()+"/json-storage/temp-"+implementation.getImplementationName());
		File[] tempFiles = tempStorage.listFiles();
		if (tempFiles != null) {
			for (int i = 0; i < tempFiles.length; i++) {
				File current = tempFiles[i];
				if (current.isFile() && current.getName().endsWith(".json")) {
					try {
						String json = new String(Files.readAllBytes(Paths.get(current.getPath())));
						NPCData data = NPCData.fromJson(current.getName().replace(".json", ""), json, true);
						if (data != null) {
							boolean restore = true;
							if (((RemoteStorage)implementation).isConnected()) {
                                if (!((RemoteStorage)implementation).exists(data.getName())) {
                                    saveNPC(data);
                                } else {
                                    main.log(Level.WARNING, "Could not merge NPC from temp storage to the database because an NPC with the same name already exists in the database.");
                                    restore = false;
                                }
                                current.delete();
							}
							if (restore) {
								main.npc.spawnNPC(data);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
        }
        if (tempStorage.listFiles().length == 0) tempStorage.delete();
    }
}
