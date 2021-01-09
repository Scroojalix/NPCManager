package io.github.scroojalix.npcmanager.utils.storage;

import java.util.logging.Level;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.storage.implementation.StorageImplementation;
import io.github.scroojalix.npcmanager.utils.storage.implementation.json.JsonStorage;
import io.github.scroojalix.npcmanager.utils.storage.implementation.mysql.MySQLStorage;

public class StorageFactory {
    
    private NPCMain main;

    public StorageFactory(NPCMain main) {
        this.main = main;
    }

    public Storage getInstance() {
        StorageType type = StorageType.parse(main.getConfig().getString("save-method"));
        this.main.log(Level.INFO, "Loading storage provider... [" + type.getName() + "]");
        return new Storage(this.main, createNewImplementation(type));
    }

    public StorageImplementation createNewImplementation(StorageType type) {
        switch (type) {
            case JSON:
                return new JsonStorage(main);
            case MYSQL:
                return new MySQLStorage(main);
            default:
                throw new RuntimeException("Unknown storage type: " + type);
        }
    }
}
