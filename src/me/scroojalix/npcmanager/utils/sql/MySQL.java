package me.scroojalix.npcmanager.utils.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.configuration.file.FileConfiguration;

import me.scroojalix.npcmanager.NPCMain;

public class MySQL {
	
	private String host;
	private String port;
	private String database;
	private String username;
	private String password;
	private boolean useSSL;
	private String tableName;
	
	private Connection connection;
	
	private SQLGetter getter;
	
	public MySQL(NPCMain main) {
		FileConfiguration config = main.getConfig();
		host = config.getString("sql.host");
		port = config.getString("sql.port");
		database = config.getString("sql.database");
		username = config.getString("sql.username");
		password = config.getString("sql.password");
		useSSL = config.getBoolean("sql.useSSL");
		
		getter = new SQLGetter(main, config.getString("sql.table-name"));
	}
	
	public SQLGetter getGetter() {
		return getter;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public boolean isConnected() {
		return connection != null;
	}
	
	public void connect() throws ClassNotFoundException, SQLException {
		if (!isConnected())
			connection = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/"+database+"?useSSL="+useSSL, username, password);
	}
	
	public void disconnect() {
		if (isConnected())
			try {
				connection.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
	}
	
	public Connection getConnection() {
		return connection;
	}
}
