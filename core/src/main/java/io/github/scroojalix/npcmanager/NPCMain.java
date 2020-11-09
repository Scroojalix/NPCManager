package io.github.scroojalix.npcmanager;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.scroojalix.npcmanager.commands.NPCCommands;
import io.github.scroojalix.npcmanager.events.NPCEvents;
import io.github.scroojalix.npcmanager.nms.interfaces.INPCManager;
import io.github.scroojalix.npcmanager.nms.interfaces.IPacketReader;
import io.github.scroojalix.npcmanager.utils.FileManager;
import io.github.scroojalix.npcmanager.utils.InteractionsManager;
import io.github.scroojalix.npcmanager.utils.SkinManager;
import io.github.scroojalix.npcmanager.utils.sql.MySQL;
import net.md_5.bungee.api.ChatColor;

/**
 * Main class for the NPCManager plugin.
 * @author Scroojalix.
 * @see <a href="https://github.com/Scroojalix/NPCManager">Github Repository</a>
 */
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
	public ServerVersion serverVersion;
	
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
		this.initSaveMethod();
		this.skinFile = new FileManager(this, "skins.yml");
		this.skinManager = new SkinManager(this);
		this.getCommand("npc").setExecutor(new NPCCommands(this));
		this.getServer().getPluginManager().registerEvents(new NPCEvents(this), this);
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
			InteractionsManager.getInteractEvents().clear();
			if (saveMethod == SaveMethod.MYSQL) {
				sql.disconnect();
			}
		}
		instance = null;
	}
	
	/**
	 * Translate colour codes and hex codes into a coloured string.
	 * @param msg The message to translate.
	 * @return The translated string.
	 */
	public String format(String msg) {
		if (serverVersion.hasHexSupport) {
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

	/**
	 * Save method for the plugin. 
	 * @author Scroojalix
	 */
	public enum SaveMethod {
		YAML, MYSQL;
	}

	/**
	 * Version that the server is running.
	 * @author Scroojalix
	 */
	public enum ServerVersion {
		v1_8_R2(false), v1_8_R3(false), v1_9_R1(false), v1_9_R2(false), v1_10_R1(false),
		v1_11_R1(false), v1_12_R1(false), v1_13_R1(false), v1_13_R2(false), v1_14_R1(false),
		v1_15_R1(false), v1_16_R1(true), v1_16_R2(true), v1_16_R3(true);

		public final boolean hasHexSupport; 

		private ServerVersion(boolean hexSupport) {
			this.hasHexSupport = hexSupport;
		}
	}
}
