package me.scroojalix.npcmanager.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.scroojalix.npcmanager.NPCMain;

public class FileManager {
	
	private NPCMain main;
	private FileConfiguration dataConfig = null;
	private File configFile = null;
	private String name;

	public FileManager(NPCMain main, String name) {
		this.main = main;
		this.name = name;
		saveDefaultConfig();
	}
	
	public void reloadConfig() {
		if (this.configFile == null)
			this.configFile = new File(this.main.getDataFolder(), name);
		
		this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);
		
		InputStream defaultStream = this.main.getResource(name);
		if (defaultStream != null) {
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
			this.dataConfig.setDefaults(defaultConfig);
		}
	}
	
	public FileConfiguration getConfig() {
		if (this.dataConfig == null)
			reloadConfig();
		
		return this.dataConfig;
	}
	
	public void saveConfig() {
		if (this.dataConfig == null || this.configFile == null)
			return;
		
		try {
			this.getConfig().save(this.configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveDefaultConfig() {
		if (this.configFile == null)
			this.configFile = new File(this.main.getDataFolder(), name);
			
		if (!this.configFile.exists()) {
			this.main.saveResource(name, false);
		}
	}
}
