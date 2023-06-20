package io.github.scroojalix.npcmanager;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.scroojalix.npcmanager.commands.CommandManager;
import io.github.scroojalix.npcmanager.dependencies.DependencyManager;
import io.github.scroojalix.npcmanager.events.EquipmentEvents;
import io.github.scroojalix.npcmanager.events.NPCEvents;
import io.github.scroojalix.npcmanager.npc.equipment.EmptySlots;
import io.github.scroojalix.npcmanager.npc.equipment.EquipmentInventory;
import io.github.scroojalix.npcmanager.protocol.NPCManager;
import io.github.scroojalix.npcmanager.protocol.PacketReader;
import io.github.scroojalix.npcmanager.storage.Storage;
import io.github.scroojalix.npcmanager.storage.StorageFactory;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.PluginUtils.ServerVersion;

/**
 * Main class for the NPCManager plugin.
 * 
 * @author Scroojalix.
 * @see <a href="https://github.com/Scroojalix/NPCManager">Github Repository</a>
 */
public class NPCMain extends JavaPlugin {

	public static NPCMain instance;
	public static ServerVersion serverVersion;
	
	public NPCManager npc;
	private PacketReader reader;
	public DependencyManager dependencyManager;
	public Storage storage;
	public boolean showDebugMessages;
	
	private boolean validVersion = true;
	
	@Override
	public void onEnable() {
		NPCMain.instance = this;
		this.showDebugMessages = this.getConfig().getBoolean("show-debug-messages");
		if (!validVersion()) {
			getLogger().severe("Disabling the plugin.");
			NPCMain.instance = null;
			validVersion = false;
			this.setEnabled(false);
		} else {
			initialise();
		}
	}
	
	/**
	 * Initialises the plugin.
	 */
	private void initialise() {
		try {
			npc = new NPCManager(this);
			reader = new PacketReader(this);
			reader.registerInteractPacketListener();
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

		StorageFactory factory = new StorageFactory(this);
		this.dependencyManager = new DependencyManager(this);
		this.dependencyManager.loadStorageDependencies(factory.getType());

		this.storage = factory.getInstance();
		
		this.getCommand("npc").setExecutor(new CommandManager(this));
		this.getServer().getPluginManager().registerEvents(new NPCEvents(), this);
		this.getServer().getPluginManager().registerEvents(new EquipmentEvents(this), this);
		PluginUtils.checkForUpdate();
	}
	
	@Override
	public void onDisable() {
		if (validVersion) {
			reader.deregisterPacketListeners();
			if (!PluginUtils.noNPCs()) {
				npc.removeAllNPCs();
			}
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getOpenInventory().getTopInventory().getHolder() instanceof EquipmentInventory) 
					player.closeInventory();
			}
			if (storage != null)
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
			this.getLogger().severe(version + " is not a supported version!");
			return false;
		}
		getLogger().info(String.format("Running on NMS version %s", serverVersion));
		return true;
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
		this.storage.shutdown();

		StorageFactory factory = new StorageFactory(this);
		this.dependencyManager.loadStorageDependencies(factory.getType());

		this.storage = factory.getInstance();

		PluginUtils.checkForUpdate();
	}
}
