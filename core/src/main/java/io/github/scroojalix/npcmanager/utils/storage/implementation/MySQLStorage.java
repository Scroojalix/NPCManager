package io.github.scroojalix.npcmanager.utils.storage.implementation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.configuration.file.FileConfiguration;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.storage.implementation.interfaces.RemoteStorage;
import io.github.scroojalix.npcmanager.utils.storage.implementation.interfaces.StorageImplementation;

public class MySQLStorage implements StorageImplementation, RemoteStorage {

    private final NPCMain main;
    private final String address;
	private final String database;
	private final String username;
    private final String password;

    private final String tableName;
    
    private final int connectionTimeout;
    private final boolean useSSL;
    
    private Connection connection;

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
    public boolean isConnected() {
        return connection != null;
        //TODO add more logic to isConnected() methods.
    }

    @Override
    public boolean exists(String name) {
        if (!isConnected())
            return false;
        try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM "+tableName+" WHERE NAME=?");
			ps.setString(1, name);
			ResultSet results = ps.executeQuery();
			return results.next();
		} catch(Exception e) {
            return false;
		}
    }

    @Override
    public void init() throws Throwable {
        DriverManager.setLoginTimeout(connectionTimeout);
        connection = DriverManager.getConnection("jdbc:mysql://"+address+"/"+database+"?useSSL="+useSSL, username, password);
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
        PreparedStatement ps = connection.prepareStatement("INSERT IGNORE INTO "+tableName+" (NAME,DATA) VALUES (?,?)");
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
}
