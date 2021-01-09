package io.github.scroojalix.npcmanager.utils.storage.implementation.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;

public class SQLGetter {
	
	private NPCMain main;
	private String tableName;
	
	public SQLGetter(NPCMain main, String tableName) {
		this.main = main;
		this.tableName = tableName;
	}
	
	public void createTable(Connection connection) {
		try {
			PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS "+tableName+
					" (NAME VARCHAR(16) BINARY,DATA TEXT BINARY,PRIMARY KEY (NAME))");
			ps.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean addNPC(Connection connection, NPCData data, boolean replace) {
		try {
			String name = data.getName();
			String json = data.toJson(false);
			if (exists(connection, name)) {
				if (replace) {
					remove(connection, name);
				} else {
					main.log(Level.WARNING, "Could not merge NPC from temp storage to database. An NPC with the same name already exists in the database.");
					return false;
				}
			}
			PreparedStatement ps = connection.prepareStatement("INSERT IGNORE INTO "+tableName+" (NAME,DATA) VALUES (?,?)");
			ps.setString(1, name);
			ps.setString(2, json);
			ps.executeUpdate();
			return true;
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean exists(Connection connection, String name) {
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
	
	public void remove(Connection connection, String name) {
		try {
			PreparedStatement ps = connection.prepareStatement("DELETE FROM "+tableName+" WHERE NAME=?");
			ps.setString(1, name);
			ps.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean testConnection(Connection connection) {
		if (connection != null) {
			try {
				PreparedStatement ps = connection.prepareStatement("SELECT * FROM "+tableName);
				ps.execute();
				return true;
			} catch (NullPointerException | SQLException e) {}
		}
		return false;
	}
	
	public void restoreDataEntries(Connection connection) {
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM "+tableName);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				NPCData data = NPCData.fromJson(rs.getString(1), rs.getString(2), false);
				if (data != null) {
					main.npc.spawnNPC(data);
				}
			}
		} catch(SQLException | NullPointerException e) {
			e.printStackTrace();
		}
	}
}
