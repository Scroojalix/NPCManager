package io.github.scroojalix.npcmanager.utils.storage;

import java.util.logging.Level;

import io.github.scroojalix.npcmanager.NPCMain;

//TODO implement MongoDB
public enum StorageType {
    JSON("JSON"),
    MYSQL("MySQL");

    private final String name;

    private StorageType(String name) {
        this.name = name;
    }

    public static StorageType parse(String name) {
        for (StorageType type : values()) {
            if (type.name().equalsIgnoreCase(name)) return type;
        }
        NPCMain.instance.log(Level.WARNING, "Unknown storage type '"+name+"'. Defaulting to JSON.");
        return StorageType.JSON;
    }

    public String getName() {
        return this.name;
    }

}
