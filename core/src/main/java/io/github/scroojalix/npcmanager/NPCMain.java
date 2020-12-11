package io.github.scroojalix.npcmanager;

import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.scroojalix.npcmanager.commands.CommandManager;
import io.github.scroojalix.npcmanager.events.EquipmentEvents;
import io.github.scroojalix.npcmanager.events.NPCEvents;
import io.github.scroojalix.npcmanager.nms.interfaces.INPCManager;
import io.github.scroojalix.npcmanager.nms.interfaces.IPacketReader;
import io.github.scroojalix.npcmanager.utils.EmptySlots;
import io.github.scroojalix.npcmanager.utils.FileManager;
import io.github.scroojalix.npcmanager.utils.SkinManager;
import io.github.scroojalix.npcmanager.utils.PluginUtils.SaveMethod;
import io.github.scroojalix.npcmanager.utils.PluginUtils.ServerVersion;
import io.github.scroojalix.npcmanager.utils.sql.MySQL;

/**
 * Main class for the NPCManager plugin.
 * @author Scroojalix.
 * @see <a href="https://github.com/Scroojalix/NPCManager">Github Repository</a>
 */
public class NPCMain extends JavaPlugin {
	
	public static NPCMain instance;
	public static ServerVersion serverVersion;
	
	public FileManager skinFile;
	public INPCManager npc;
	public IPacketReader reader;
	public SkinManager skinManager;
	public SaveMethod saveMethod;
	public FileManager npcFile;
	public MySQL sql;
	public boolean showDebugMessages;
	
	private boolean validVersion = true;
	
	@Override
	public void onEnable() {
		this.showDebugMessages = this.getConfig().getBoolean("show-debug-messages");
		this.setSaveMethod();
		if (!validVersion()) {
			getLogger().severe("This plugin is not compatible with that server version!");
			getLogger().severe("Disabling the plugin.");
			validVersion = false;
			this.setEnabled(false);
		} else {
			if (!Bukkit.getOnlinePlayers().isEmpty()) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					reader.inject(player);
				}
			}
		}
	}

	/**
	 * Initialises the plugin.
	 */
	private void initialise() {
		NPCMain.instance = this;
		this.saveDefaultConfig();
		EmptySlots.generateItems();
		this.initSaveMethod();
		this.skinFile = new FileManager(this, "skins.yml");
		this.skinManager = new SkinManager(this);
		this.getCommand("npc").setExecutor(new CommandManager(this));
		this.getServer().getPluginManager().registerEvents(new NPCEvents(this), this);
		this.getServer().getPluginManager().registerEvents(new EquipmentEvents(this), this);
	}

	@Override
	public void onDisable() {
		if (validVersion) {
			if (!npc.getNPCs().isEmpty()) {
				npc.removeAllNPCs();
			}
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.closeInventory();
				reader.uninject(player);
			}
			if (saveMethod == SaveMethod.MYSQL) {
				sql.disconnect();
			}
		}
		instance = null;
	}
	
	private boolean validVersion() {
		String version;
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		} catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
			getLogger().severe("Unknown Server Version");
			return false;
		}
		log(Level.INFO, "Your server is running version "+version);

		try {
			serverVersion = ServerVersion.valueOf(version);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}

		initialise();

		String pack = "io.github.scroojalix.npcmanager.nms."+version;
		try {
			npc = (INPCManager) Class.forName(pack + ".NPCManager").getConstructors()[0].newInstance(this);
			reader = (IPacketReader) Class.forName(pack + ".PacketReader").getConstructors()[0].newInstance(this);
		} catch (Exception e) {
			return false;
		}
		return npc != null && reader != null;
	}
	
	public void log(Level level, String msg) {
		if (showDebugMessages) {
			getLogger().log(level, msg);
		}
	}
	
	public void reloadPlugin() {
		npc.removeAllNPCs();
		this.saveDefaultConfig();
		reloadConfig();
		showDebugMessages = getConfig().getBoolean("show-debug-messages");
		skinFile.reloadConfig();
		skinManager.generateSkins();
		setSaveMethod();
		initSaveMethod();
		npc.restoreNPCs();
	}
	
	private void setSaveMethod() {
		String save = getConfig().getString("save-method");
		switch(save) {
		case "YAML":
			saveMethod = SaveMethod.YAML;
			log(Level.INFO, "Save method set to YAML");
			break;
		case "JSON":
			saveMethod = SaveMethod.JSON;
			log(Level.INFO, "Save method set to JSON");
			break;
		case "MYSQL":
			saveMethod = SaveMethod.MYSQL;
			log(Level.INFO, "Save method set to MYSQL");
			break;
		default:
			log(Level.WARNING, "Unknown saving method '"+save+"'. Defaulting it to YAML");
			saveMethod = SaveMethod.YAML;
			break;
		}
	}

	private void initSaveMethod() {
		switch(saveMethod) {
		case YAML:
			npcFile = new FileManager(this, "npcs.yml");
			break;
		case JSON:
			break;
		case MYSQL:
			sql = new MySQL(this);
			try {
				sql.connect();
			} catch (ClassNotFoundException | SQLException e) {
				getLogger().log(Level.SEVERE, "Could not connect to database. Is the database online and the login info correct?");
			}
			
			if (sql.isConnected()) {
				log(Level.INFO, "Successfully connected to database.");
				sql.getGetter().createTable();
			}
			break;
		}
	}
}
