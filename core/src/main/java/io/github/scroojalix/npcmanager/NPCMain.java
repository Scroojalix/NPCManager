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
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.PluginUtils.SaveMethod;
import io.github.scroojalix.npcmanager.utils.PluginUtils.ServerVersion;
import io.github.scroojalix.npcmanager.utils.chat.Messages;
import io.github.scroojalix.npcmanager.utils.npc.equipment.EmptySlots;
import io.github.scroojalix.npcmanager.utils.sql.MySQL;

/**
 * Main class for the NPCManager plugin.
 * 
 * @author Scroojalix.
 * @see <a href="https://github.com/Scroojalix/NPCManager">Github Repository</a>
 */
public class NPCMain extends JavaPlugin {

	public static NPCMain instance;
	public static ServerVersion serverVersion;
	
	public INPCManager npc;
	public IPacketReader reader;
	public SaveMethod saveMethod;
	public MySQL sql;
	public boolean showDebugMessages;
	
	private boolean validVersion = true;
	
	@Override
	public void onEnable() {
		NPCMain.instance = this;
		this.showDebugMessages = this.getConfig().getBoolean("show-debug-messages");
		this.setSaveMethod();
		if (!validVersion()) {
			getLogger().severe("This plugin is not compatible with that server version!");
			getLogger().severe("Disabling the plugin.");
			NPCMain.instance = null;
			validVersion = false;
			this.setEnabled(false);
		} else {
			initialise();
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
		this.saveDefaultConfig();
		EmptySlots.generateItems();
		this.initSaveMethod();
		this.getCommand("npc").setExecutor(new CommandManager(this));
		this.getServer().getPluginManager().registerEvents(new NPCEvents(this), this);
		this.getServer().getPluginManager().registerEvents(new EquipmentEvents(this), this);
		PluginUtils.checkForUpdate();
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

		try {
			serverVersion = ServerVersion.valueOf(version);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}

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
		setSaveMethod();
		initSaveMethod();
		PluginUtils.checkForUpdate();
	}
	
	private void setSaveMethod() {
		String save = getConfig().getString("save-method");
		switch(save) {
		case "JSON":
			saveMethod = SaveMethod.JSON;
			log(Level.INFO, "Save method set to JSON");
			break;
		case "MYSQL":
			saveMethod = SaveMethod.MYSQL;
			log(Level.INFO, "Save method set to MYSQL");
			break;
		default:
			log(Level.WARNING, "Unknown saving method '"+save+"'. Defaulting it to JSON");
			saveMethod = SaveMethod.JSON;
			break;
		}
	}

	private void initSaveMethod() {
		Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				switch(saveMethod) {
				case JSON:
					break;
				case MYSQL:
					NPCMain.instance.sql = new MySQL(NPCMain.instance);
					try {
						sql.connect();
						if (sql.isConnected()) {
							log(Level.INFO, "Successfully connected to database.");
							sql.getGetter().createTable();
						}
					} catch (ClassNotFoundException | SQLException e) {
						getLogger().log(Level.SEVERE, Messages.DATABASE_NOT_CONNECTED);
					}
					break;
				}

				Bukkit.getScheduler().runTask(NPCMain.instance, new Runnable() {
					@Override
					public void run() {
						NPCMain.instance.npc.restoreNPCs();
					}
				});
			}
		});
	}
}
