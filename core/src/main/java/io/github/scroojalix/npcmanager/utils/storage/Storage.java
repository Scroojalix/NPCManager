package io.github.scroojalix.npcmanager.utils.storage;

import java.util.logging.Level;

import org.bukkit.Bukkit;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.storage.implementation.StorageImplementation;

public class Storage {

    private NPCMain main;
    private StorageImplementation implementation;

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
                } catch(Exception e) {
                    main.getLogger().log(Level.SEVERE, "Failed to init storage implementation", e);
                }
                restoreNPCs();
            }
        });
    }
    
    public void shutdown() {
        this.main.log(Level.INFO, "Shutting down storage implementation");
        try {
            this.implementation.shutdown();
        } catch(Exception e) {
            this.main.getLogger().log(Level.SEVERE, "Failed to shutdown storage implementation", e);
        }
    }

    public void saveNPC(NPCData data) {
        if (data.isStored()) {
            Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
                @Override
                public void run() {
                    implementation.saveNPC(data);
                }
            });
        }
    }

    public void removeNPC(String name) {
        Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable(){
			@Override
            public void run() {
                implementation.removeNPC(name);
            }
        });
    }

    public void restoreNPCs() {
        Bukkit.getScheduler().runTaskLater(main, new Runnable() {
            @Override
            public void run() {
                implementation.restoreNPCs();
            }
        }, 1l);
    }
}
