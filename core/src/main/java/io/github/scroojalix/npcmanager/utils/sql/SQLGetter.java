package io.github.scroojalix.npcmanager.utils.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;

public class SQLGetter {
	
	private NPCMain main;
	private String tableName;
	
	public SQLGetter(NPCMain main, String tableName) {
		this.main = main;
		this.tableName = tableName;
	}
	
	public void createTable() {
		try {
			PreparedStatement ps = main.sql.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS "+tableName+
					" (NAME VARCHAR(16) BINARY,DATA TEXT BINARY,PRIMARY KEY (NAME))");
			ps.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addNPC(NPCData data) {
		try {
			String name = data.getName();
			String json = data.toJson(false);
			if (exists(name)) {
				remove(name);
			}
			PreparedStatement ps = main.sql.getConnection().prepareStatement("INSERT IGNORE INTO "+tableName+" (NAME,DATA) VALUES (?,?)");
			ps.setString(1, name);
			ps.setString(2, json);
			ps.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean exists(String name) {
		try {
			PreparedStatement ps = main.sql.getConnection().prepareStatement("SELECT * FROM "+tableName+" WHERE NAME=?");
			ps.setString(1, name);
			ResultSet results = ps.executeQuery();
			return results.next();
		} catch(NullPointerException | SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String getData(String name) {
		try {
			PreparedStatement ps = main.sql.getConnection().prepareStatement("SELECT DATA FROM "+tableName+" WHERE NAME=?");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			String data = null;
			if (rs.next()) {
				data = rs.getString("DATA");
			}
			return data;
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void emptyTable() {
		try {
			PreparedStatement ps = main.sql.getConnection().prepareStatement("TRUNCATE "+tableName);
			ps.executeUpdate();
		} catch(SQLException | NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	public void remove(String name) {
		try {
			PreparedStatement ps = main.sql.getConnection().prepareStatement("DELETE FROM "+tableName+" WHERE NAME=?");
			ps.setString(1, name);
			ps.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean testConnection() {
		try {
			PreparedStatement ps = main.sql.getConnection().prepareStatement("SELECT * FROM "+tableName);
			ps.execute();
			return true;
		} catch (NullPointerException | SQLException e) {
			return false;
		}
	}
	
	public void restoreDataEntries() {
		try {
			PreparedStatement ps = main.sql.getConnection().prepareStatement("SELECT DATA FROM "+tableName);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				NPCData data = NPCData.fromJson(rs.getString(1), false);
				if (data != null) {
					main.npc.restoreNPC(data);
				}
			}
		} catch(SQLException | NullPointerException e) {
			e.printStackTrace();
		}
	}
}
