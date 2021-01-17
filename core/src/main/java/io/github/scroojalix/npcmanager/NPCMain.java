package io.github.scroojalix.npcmanager;

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
import io.github.scroojalix.npcmanager.utils.PluginUtils.ServerVersion;
import io.github.scroojalix.npcmanager.utils.dependencies.DependencyManager;
import io.github.scroojalix.npcmanager.utils.dependencies.classloader.ReflectionClassLoader;
import io.github.scroojalix.npcmanager.utils.npc.equipment.EmptySlots;
import io.github.scroojalix.npcmanager.utils.npc.equipment.EquipmentInventory;
import io.github.scroojalix.npcmanager.utils.storage.Storage;
import io.github.scroojalix.npcmanager.utils.storage.StorageFactory;

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
	public DependencyManager dependencyManager;
	public Storage storage;
	public boolean showDebugMessages;
	
	private boolean validVersion = true;
	private ReflectionClassLoader classLoader;
	
	@Override
	public void onEnable() {
		NPCMain.instance = this;
		this.showDebugMessages = this.getConfig().getBoolean("show-debug-messages");
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
		String pack = "io.github.scroojalix.npcmanager.nms."+serverVersion;
		try {
			npc = (INPCManager) Class.forName(pack + ".NPCManager").getConstructors()[0].newInstance(this);
			reader = (IPacketReader) Class.forName(pack + ".PacketReader").getConstructors()[0].newInstance(this);
		} catch (Exception e) {
			this.getLogger().log(Level.SEVERE, "Could not initialise the plugin", e);
			validVersion = false;
			this.setEnabled(false);
			return;
		}
		this.saveDefaultConfig();
		EmptySlots.generateItems();
		long npcRemoveDelay = getConfig().getLong("npc-remove-delay");
		if (npcRemoveDelay < 1) npcRemoveDelay = 1;
		PluginUtils.NPC_REMOVE_DELAY = npcRemoveDelay;
		this.classLoader = new ReflectionClassLoader(this);
		this.dependencyManager = new DependencyManager(this);
		this.storage = new StorageFactory(this).getInstance();
		this.storage.init();
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
				if (player.getOpenInventory().getTopInventory().getHolder() instanceof EquipmentInventory) 
					player.closeInventory();
				reader.uninject(player);
			}
			storage.shutdown();
		}
		instance = null;
	}
	
	private boolean validVersion() {
		String version;
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		} catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
			getLogger().log(Level.SEVERE, "Unknown Server Version", whatVersionAreYouUsingException);
			return false;
		}

		try {
			serverVersion = ServerVersion.valueOf(version);
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	public ReflectionClassLoader getPluginClassLoader() {
		return this.classLoader;
	}
	
	public void log(Level level, String msg) {
		if (showDebugMessages) {
			getLogger().log(level, msg);
		}
	}
	
	public void reloadPlugin() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getOpenInventory().getTopInventory().getHolder() instanceof EquipmentInventory) 
				player.closeInventory();
		}
		npc.removeAllNPCs();
		this.saveDefaultConfig();
		reloadConfig();
		showDebugMessages = getConfig().getBoolean("show-debug-messages");
		int newNPCNameLength = this.getConfig().getInt("npc-name-length");
		if (newNPCNameLength > 16) newNPCNameLength = 16;
		if (newNPCNameLength < 3) newNPCNameLength = 3;
		log(Level.INFO, "Set NPC tab list name length to "+newNPCNameLength);
		npc.setNPCNameLength(newNPCNameLength);
		long npcRemoveDelay = getConfig().getLong("npc-remove-delay");
		if (npcRemoveDelay < 1) npcRemoveDelay = 1;
		PluginUtils.NPC_REMOVE_DELAY = npcRemoveDelay;
		storage.shutdown();
		this.storage = new StorageFactory(this).getInstance();
		this.storage.init();
		PluginUtils.checkForUpdate();
	}
}
