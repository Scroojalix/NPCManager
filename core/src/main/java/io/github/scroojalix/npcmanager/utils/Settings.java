package io.github.scroojalix.npcmanager.utils;

import org.bukkit.configuration.file.FileConfiguration;

import io.github.scroojalix.npcmanager.NPCMain;

public class Settings {

    public static ConfigEntry<Boolean> SHOW_DEBUG_MESSAGES = () -> {
        return getConfig().getBoolean("show-debug-messages", false);
    };

    public static ConfigEntry<Double> HEAD_ROTATION_RANGE = () -> {
        double headRotationRange = getConfig().getDouble("head-rotation-range", 6);
        if (headRotationRange < 0) headRotationRange = 0;
        return headRotationRange;
    };

    public static ConfigEntry<Boolean> RESET_HEAD_ROTATION = () -> {
        return getConfig().getBoolean("reset-head-rotation", true);
    };

    public static ConfigEntry<Boolean> PERFECT_HEAD_ROTATION = () -> {
        return getConfig().getBoolean("perfect-head-rotation", true);
    };

    public static ConfigEntry<Boolean> FETCH_DEFAULT_SKINS = () -> {
        return getConfig().getBoolean("fetch-default-skins", true);
    };

    public static ConfigEntry<Long> NPC_REMOVE_DELAY = () -> {
        long removeDelay = getConfig().getLong("npc-remove-delay", 60);
        if (removeDelay < 1) removeDelay = 1;
        return removeDelay;
    };

    public static ConfigEntry<Integer> NPC_NAME_LENGTH = () -> {
        int npcNameLength = getConfig().getInt("npc-name-length");
		if (npcNameLength > 16) npcNameLength = 16;
		if (npcNameLength < 3) npcNameLength = 3;
        return npcNameLength;
    };

    public static ConfigEntry<String> STORAGE_METHOD = () -> {
        return getConfig().getString("storage-method", "JSON");
    };

    // DATABASE SETTINGS

    public static ConfigEntry<String> DATABASE_ADDRESS = () -> {
        return getConfig().getString("database.address", "localhost");  
    };

    public static ConfigEntry<String> DATABASE_NAME = () -> {
        return getConfig().getString("database.name", "database");  
    };

    public static ConfigEntry<String> DATABASE_USERNAME = () -> {
        return getConfig().getString("database.username", "");  
    };

    public static ConfigEntry<String> DATABASE_PASSWORD = () -> {
        return getConfig().getString("database.password", "");  
    };

    public static ConfigEntry<String> DATABASE_TABLE_NAME = () -> {
        return getConfig().getString("database.table-name", "npcdata");  
    };

    public static ConfigEntry<Integer> DATABASE_CONNECTION_TIMEOUT = () -> {
        int connectionTimeout = getConfig().getInt("database.connection-timeout", 10);
        if (connectionTimeout < 1) connectionTimeout = 1;
        return connectionTimeout;  
    };

    public static ConfigEntry<Boolean> DATABASE_USE_SSL = () -> {
        return getConfig().getBoolean("database.useSSL", false);  
    };

    public static ConfigEntry<String> MONGODB_CONNECTION_STRING = () -> {
        return getConfig().getString("database.mongodb-connection-string", "");  
    };

        
    private static FileConfiguration getConfig() {
        return NPCMain.instance.getConfig();
    }

    public interface ConfigEntry<T> {
        public T get();
    }   
}
