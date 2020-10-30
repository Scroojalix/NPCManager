package me.scroojalix.npcmanager;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.scroojalix.npcmanager.api.InteractionsManager;
import me.scroojalix.npcmanager.commands.NPCCommands;
import me.scroojalix.npcmanager.events.NPCEvents;
import me.scroojalix.npcmanager.nms.interfaces.INPCManager;
import me.scroojalix.npcmanager.nms.interfaces.IPacketReader;
import me.scroojalix.npcmanager.utils.FileManager;
import me.scroojalix.npcmanager.utils.SkinManager;
import me.scroojalix.npcmanager.utils.sql.MySQL;
import net.md_5.bungee.api.ChatColor;

public class NPCMain extends JavaPlugin {
	
	public static NPCMain instance;
	
	private final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
	
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
		if (!validVersion()) {
			getLogger().severe("This plugin is not compatible with that server version!");
			getLogger().severe("Disabling the plugin.");
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

	public void initialise() {
		NPCMain.instance = this;
		this.saveDefaultConfig();
		this.showDebugMessages = this.getConfig().getBoolean("show-debug-messages");
		this.setSaveMethod();
		this.skinFile = new FileManager(this, "skins.yml");
		this.skinManager = new SkinManager(this);
		this.getCommand("npc").setExecutor(new NPCCommands(this));
		this.getServer().getPluginManager().registerEvents(new NPCEvents(this), this);
	}

	@Override
	public void onDisable() {
		if (validVersion) {
			npc.saveNPCs();
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.closeInventory();
				reader.uninject(player);
			}
			InteractionsManager.getInteractEvents().clear();
		}
		instance = null;
	}
	
	public String format(String msg) {
		if (Bukkit.getVersion().contains("1.16")) {
			Matcher match = pattern.matcher(msg);
			while (match.find()) {
				String colour = msg.substring(match.start(), match.end());
				msg = msg.replace(colour, ChatColor.of(colour) + "");
				match = pattern.matcher(msg);
			}
		}
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	private boolean validVersion() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
        	getLogger().severe("Unknown Server Version");
            return false;
        }

        String pack = "me.scroojalix.npcmanager.nms."+version;
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
		npc.saveNPCs();
		reloadConfig();
		skinFile.reloadConfig();
		skinManager.generateSkins();
		setSaveMethod();
		npc.restoreNPCs();
		showDebugMessages = getConfig().getBoolean("show-debug-messages");
	}
	
	public void setSaveMethod() {
		String save = getConfig().getString("save-method");
		switch(save) {
		case "YAML":
			saveMethod = SaveMethod.YAML;
			log(Level.INFO, "Save method set to YAML");
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
		
		switch(saveMethod) {
		case YAML:
			npcFile = new FileManager(this, "npcs.yml");
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

	public enum SaveMethod {
		YAML, MYSQL;
	}
}
