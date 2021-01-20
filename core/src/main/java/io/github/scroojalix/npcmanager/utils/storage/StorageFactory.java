package io.github.scroojalix.npcmanager.utils.storage;

import java.util.logging.Level;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.storage.implementation.JsonStorage;
import io.github.scroojalix.npcmanager.utils.storage.implementation.MongoStorage;
import io.github.scroojalix.npcmanager.utils.storage.implementation.MySQLStorage;
import io.github.scroojalix.npcmanager.utils.storage.implementation.StorageImplementation;

public class StorageFactory {
    
    private NPCMain main;
    private StorageType type;

    public StorageFactory(NPCMain main) {
        this.main = main;
        this.type = StorageType.parse(main.getConfig().getString("save-method"));
    }

    public StorageType getType() {
        return this.type;
    }

    public Storage getInstance() {
        this.main.log(Level.INFO, "Loading storage provider... [" + type.getName() + "]");
        Storage storage = new Storage(this.main, createNewImplementation(type));
        storage.init();
        return storage;
    }

    public StorageImplementation createNewImplementation(StorageType type) {
        switch (type) {
            case JSON:
                return new JsonStorage(main);
            case MYSQL:
                return new MySQLStorage(main);
            case MONGODB:
                return new MongoStorage(main);
            default:
                throw new RuntimeException("Unknown storage type: " + type);
        }
    }
}
