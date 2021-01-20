package io.github.scroojalix.npcmanager.utils.storage;

import java.util.logging.Level;

import org.bukkit.Bukkit;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.chat.Messages;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.storage.implementation.StorageImplementation;

public class Storage {

    private NPCMain main;
    private StorageImplementation implementation;
    private boolean initialised;

    public Storage(NPCMain main, StorageImplementation implementation) {
        this.main = main;
        this.implementation = implementation;
    }
    
    public void init() {
        this.main.log(Level.INFO, "Initialising storage implementation");
        Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
            @Override
            public void run() {
                try {
                    implementation.init();
                    initialised = true;
                    if (implementation.isRemote()) {
                        main.log(Level.INFO, "Successfully connected to database.");
                    }
                } catch(Throwable t) {
                    if (main.showDebugMessages) {
                        main.getLogger().log(Level.SEVERE, "Failed to init storage implementation.", t);
                    }
                    main.getLogger().severe(Messages.DATABASE_NOT_CONNECTED);
                    main.getLogger().severe("Temp storage will be used instead.");
                }
                restoreNPCs();
            }
        });
    }
    
    public void shutdown() {
        if (initialised) {
            try {
                this.implementation.shutdown();
                this.main.log(Level.INFO, "Shut down storage implementation");
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
                    if (initialised) {
                        try {
                            implementation.saveNPC(data);
                            return;
                        } catch(Throwable t) {
                            main.getLogger().log(Level.SEVERE, "Failed to save an NPC to storage.", t);
                            initialised = false;
                        }
                    }
                    if (implementation.isRemote()) {
                        main.getLogger().severe(Messages.DATABASE_NOT_CONNECTED);
                    }
                    main.getLogger().warning("Saving an NPC to temp storage.");
                    //TODO save to temp storage.
                }
            });
        }
    }
    
    public void removeNPC(String name) {
        Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable(){
            @Override
            public void run() {
                if (initialised) {
                    try {
                        implementation.removeNPC(name);
                        return;
                    } catch(Throwable t) {
                        main.getLogger().log(Level.SEVERE, "Failed to remove an NPC from storage.", t);
                        initialised = false;
                    }
                }
                if (implementation.isRemote()) {
                    main.getLogger().severe(Messages.DATABASE_NOT_CONNECTED);
                }
                main.getLogger().warning("Removing an NPC from temp storage.");
                //TODO remove from temp storage if necessary.
                
            }
        });
    }
    
    public void restoreNPCs() {
        Bukkit.getScheduler().runTaskLater(main, new Runnable() {
            @Override
            public void run() {
                if (initialised) {
                    try {
                        main.getLogger().info(Messages.RESTORE_NPCS);
                        implementation.restoreNPCs();
                        return;
                    } catch(Throwable t) {
                        main.getLogger().log(Level.SEVERE, "Failed to restore NPC's from storage.", t);
                        initialised = false;
                    }
                }
                if (implementation.isRemote()) {
                    main.getLogger().severe(Messages.DATABASE_NOT_CONNECTED);
                }
                main.getLogger().warning("Restoring NPC's from temp storage.");
                //TODO restore from temp storage if necessary.
            }
        }, 1l);
    }
}
