package io.github.scroojalix.npcmanager.utils.storage.implementation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;

public class MySQLStorage implements StorageImplementation {

    private final NPCMain main;
    private final String address;
	private final String database;
	private final String username;
    private final String password;

    private final String tableName;
    
    private final int connectionTimeout;
    private final boolean useSSL;
    
    private Connection connection;

    //TODO change this code
    //remove temp storage code. Do that in Storage.java, so it works for all storage methods.

    //TODO change it so a connection is acquired, something is changed, then the connection is closed.

    public MySQLStorage(NPCMain main) {
        this.main = main;
        FileConfiguration config = main.getConfig();
		this.address = config.getString("data.address");
		this.database = config.getString("data.database");
		this.username = config.getString("data.username");
        this.password = config.getString("data.password");

        this.tableName = config.getString("data.table-name");
        
        this.connectionTimeout = config.getInt("data.connection-timeout");
        this.useSSL = config.getBoolean("data.useSSL");        
    }

    @Override
    public String getImplementationName() {
        return "MySQL";
    }

    @Override
    public boolean isRemote() {
        return true;
    }

    @Override
    public void init() throws Throwable {
        //TODO check that this doesnt affect other plugins.
        //See how LuckPerms does it.
        DriverManager.setLoginTimeout(connectionTimeout);
        connection = DriverManager.getConnection("jdbc:mysql://"+address+"/"+database+"?useSSL="+useSSL, username, password);

        main.log(Level.INFO, "Successfully connected to database.");
        this.createTable();
    }

    private void createTable() {
		try {
			PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS "+tableName+
					" (NAME VARCHAR(16) BINARY,DATA TEXT BINARY,PRIMARY KEY (NAME))");
			ps.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

    @Override
    public void shutdown() throws Throwable {
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public void saveNPC(NPCData data) throws Throwable {
        PreparedStatement ps = connection.prepareStatement("REPLACE IGNORE INTO "+tableName+" (NAME,DATA) VALUES (?,?)");
        ps.setString(1, data.getName());
        ps.setString(2, data.toJson(false));
        ps.executeUpdate();
    }

    @Override
    public void removeNPC(String name) throws Throwable {
        if (exists(name)) {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM "+tableName+" WHERE NAME=?");
			ps.setString(1, name);
			ps.executeUpdate();
        }
            //TODO remove this code once universal temp storage is set up.
            /*
            File tempFile = new File(main.getDataFolder()+"/json-storage/mysql-temp", name+".json");
            if (tempFile.exists()) {
                tempFile.delete();
                File tempDir = new File(main.getDataFolder()+"/json-storage/mysql-temp");
                if (tempDir.list().length == 0) {
                    tempDir.delete();
                }
            }
            */
    }

    private boolean exists(String name) {
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM "+tableName+" WHERE NAME=?");
			ps.setString(1, name);
			ResultSet results = ps.executeQuery();
			return results.next();
		} catch(NullPointerException | SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

    @Override
    public void restoreNPCs() throws Throwable {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM "+tableName);
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            NPCData data = NPCData.fromJson(rs.getString(1), rs.getString(2), false);
            if (data != null) {
                main.npc.spawnNPC(data);
            }
        }
    }

    /*
    private void saveTempNPC(NPCData data) {
		File jsonFile = new File(main.getDataFolder()+"/json-storage/mysql-temp", data.getName()+".json");
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
		File tempStorage = new File(main.getDataFolder()+"/json-storage/mysql-temp");
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
    */
}
