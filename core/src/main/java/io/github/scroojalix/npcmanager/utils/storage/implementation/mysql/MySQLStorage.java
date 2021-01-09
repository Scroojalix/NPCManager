package io.github.scroojalix.npcmanager.utils.storage.implementation.mysql;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.chat.Messages;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.storage.implementation.StorageImplementation;

public class MySQLStorage implements StorageImplementation {

    private NPCMain main;
    private String host;
	private String port;
	private String database;
	private String username;
	private String password;
    private boolean useSSL;
    
    private Connection connection;
    private SQLGetter getter;

    public MySQLStorage(NPCMain main) {
        this.main = main;
        FileConfiguration config = main.getConfig();
		host = config.getString("sql.host");
		port = config.getString("sql.port");
		database = config.getString("sql.database");
		username = config.getString("sql.username");
		password = config.getString("sql.password");
        useSSL = config.getBoolean("sql.useSSL");
        
        getter = new SQLGetter(main, config.getString("sql.table-name"));
    }

    @Override
    public void init() {
        try {
            connect();
            if (isConnected()) {
                main.log(Level.INFO, "Successfully connected to database.");
                getter.createTable(connection);
            }
        } catch (ClassNotFoundException | SQLException e) {
            main.getLogger().log(Level.SEVERE, Messages.DATABASE_NOT_CONNECTED);
        }
    }

    public boolean isConnected() {
        return connection != null;
    }
	
	public void connect() throws ClassNotFoundException, SQLException {
		if (!isConnected())
			connection = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database+"?useSSL="+useSSL, username, password);
	}

    @Override
    public void shutdown() {
        if (connection != null)
            try {
                connection.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void saveNPC(NPCData data) {
        if (getter.testConnection(connection)) {
            getter.addNPC(connection, data, true);
        } else {
            main.log(Level.SEVERE, Messages.DATABASE_NOT_CONNECTED);
            main.log(Level.SEVERE, "Saving NPC to temp storage instead.");
            saveTempNPC(data);
        }
    }

    @Override
    public void removeNPC(String name) {
        if (getter.testConnection(connection)) {
            getter.remove(connection, name);
        } else {
            File tempFile = new File(main.getDataFolder()+"/json-storage/temp", name+".json");
            if (tempFile.exists()) {
                tempFile.delete();
                File tempDir = new File(main.getDataFolder()+"/json-storage/temp");
                if (tempDir.list().length == 0) {
                    tempDir.delete();
                }
            }
        }
    }

    @Override
    public void restoreNPCs() {
        boolean connected = getter.testConnection(connection);
        if (connected) {
            main.log(Level.INFO, Messages.RESTORE_NPCS);
            getter.restoreDataEntries(connection);
        }
        File tempDir = new File(main.getDataFolder()+"/json-storage/temp");
        if (tempDir.exists()) {
            main.log(Level.INFO, "Restoring NPC's from temp storage.");
            restoreTempNPCs(connected);
        }
    }

    private void saveTempNPC(NPCData data) {
		File jsonFile = new File(main.getDataFolder()+"/json-storage/temp", data.getName()+".json");
		try {
			jsonFile.getParentFile().mkdirs();
			jsonFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileWriter writer = new FileWriter(jsonFile);
			writer.write(data.toJson(true));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    private void restoreTempNPCs(boolean connected) {
		File tempStorage = new File(main.getDataFolder()+"/json-storage/temp");
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
							if (connected) {
								restore = getter.addNPC(connection, data, false);
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
        if (connected) tempStorage.delete();
	}
    
}
