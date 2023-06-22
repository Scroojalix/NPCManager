package io.github.scroojalix.npcmanager.storage.implementation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Nonnull;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.storage.misc.JsonParser;
import io.github.scroojalix.npcmanager.storage.misc.StorageImplementation;
import io.github.scroojalix.npcmanager.utils.Messages;
import io.github.scroojalix.npcmanager.utils.Settings;

public class MySQLStorage implements StorageImplementation.RemoteStorage {

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
		this.address = Settings.DATABASE_ADDRESS.get();
		this.database = Settings.DATABASE_NAME.get();
		this.username = Settings.DATABASE_USERNAME.get();
        this.password = Settings.DATABASE_PASSWORD.get();

        this.tableName = Settings.DATABASE_TABLE_NAME.get();
        
        this.connectionTimeout = Settings.DATABASE_CONNECTION_TIMEOUT.get();
        this.useSSL = Settings.DATABASE_USE_SSL.get();
    }

    @Override
    public @Nonnull String getImplementationName() {
        return "MySQL";
    }

    @Override
    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public boolean exists(@Nonnull String name) {
        if (!isConnected())
            return false;
        try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM "+tableName+" WHERE NAME=?");
			ps.setString(1, name);
			ResultSet results = ps.executeQuery();
			return results.next();
		} catch(Exception e) {
            main.getLogger().severe(e.getMessage());
            return false;
		}
    }

    @Override
    public boolean init() {
        try {
            DriverManager.setLoginTimeout(connectionTimeout);
            connection = DriverManager.getConnection("jdbc:mysql://"+address+"/"+database+"?useSSL="+useSSL, username, password);
            return this.createTable();
        } catch(SQLException e) {
            main.getLogger().severe(e.getMessage());
            return false;
        }
    }

    private boolean createTable() throws SQLException {
        PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS "+tableName+
                " (NAME VARCHAR(16) BINARY,DATA TEXT BINARY,PRIMARY KEY (NAME))");
        return ps.executeUpdate() > 0;
	}

    @Override
    public boolean shutdown() {
        try {
            if (connection != null) {
                connection.close();
            }
            return connection.isClosed();
        } catch (SQLException e) {
            main.getLogger().severe(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean saveNPC(@Nonnull NPCData data) {
        try {
            PreparedStatement ps = connection.prepareStatement("REPLACE INTO "+tableName+" (NAME,DATA) VALUES (?,?)");
            ps.setString(1, data.getName());
            ps.setString(2, JsonParser.toJson(data, false));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            main.getLogger().severe(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeNPC(@Nonnull String name) {
        if (!exists(name)) return false;
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM "+tableName+" WHERE NAME=?");
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            main.getLogger().severe(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean restoreAllNPCs() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM "+tableName);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                try {
                    NPCData data = JsonParser.fromJson(rs.getString(1), rs.getString(2), false);
                    if (data != null) {
                        main.npc.spawnNPC(data);
                    }
                } catch (Exception e) {
                    Messages.printNPCRestoreError(main, rs.getString(1), e.getMessage());
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
