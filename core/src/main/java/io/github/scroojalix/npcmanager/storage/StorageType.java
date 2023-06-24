package io.github.scroojalix.npcmanager.storage;

import java.util.logging.Level;

import io.github.scroojalix.npcmanager.NPCMain;

public enum StorageType {

    //Remote database
    MYSQL("MySQL"),
    MONGODB("MongoDB", "Mongo"),

    //Local database - TODO (h2/SQLite)

    //Readable and editable text files
    JSON("JSON"),
    TOML("TOML");

    private final String name;
    private final String[] identifiers;

    private StorageType(String name, String... identifiers) {
        this.name = name;
        this.identifiers = identifiers;
    }

    public static StorageType parse(String name) {
        for (StorageType type : values()) {
            if (type.name().equalsIgnoreCase(name)) return type;
            for (String id : type.identifiers) {
                if (id.equalsIgnoreCase(name)) return type;
            }
        }
        NPCMain.instance.sendDebugMessage(Level.WARNING, "Unknown storage type '"+name+"'. Defaulting to JSON.");
        return StorageType.JSON;
    }

    public String getName() {
        return this.name;
    }

}
