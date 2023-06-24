package io.github.scroojalix.npcmanager.storage;

import java.util.logging.Level;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.storage.implementation.JsonStorage;
import io.github.scroojalix.npcmanager.storage.implementation.MongoStorage;
import io.github.scroojalix.npcmanager.storage.implementation.MySQLStorage;
import io.github.scroojalix.npcmanager.storage.implementation.TomlStorage;
import io.github.scroojalix.npcmanager.storage.misc.StorageImplementation;
import io.github.scroojalix.npcmanager.utils.Settings;

public class StorageFactory {
    
    private NPCMain main;
    private StorageType type;

    public StorageFactory(NPCMain main) {
        this.main = main;
        this.type = StorageType.parse(Settings.STORAGE_METHOD.get());
    }

    public StorageType getType() {
        return this.type;
    }

    public Storage getInstance() {
        this.main.sendDebugMessage(Level.INFO, "Loading storage provider... [" + type.getName() + "]");
        Storage storage = new Storage(this.main, createNewImplementation(type));
        storage.init();
        return storage;
    }

    public StorageImplementation createNewImplementation(StorageType type) {
        switch (type) {
            case MYSQL:
                return new MySQLStorage(main);
            case MONGODB:
                return new MongoStorage(main);
            case JSON:
                return new JsonStorage(main);
            case TOML:
                return new TomlStorage(main);
            default:
                throw new RuntimeException("Unknown storage type: " + type);
        }
    }
}
