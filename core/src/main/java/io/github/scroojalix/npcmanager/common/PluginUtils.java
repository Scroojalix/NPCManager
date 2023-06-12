package io.github.scroojalix.npcmanager.common;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonParser;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.common.npc.NPCData;
import io.github.scroojalix.npcmanager.nms.interfaces.NPCManager;
import net.md_5.bungee.api.ChatColor;

public class PluginUtils {

	public static long NPC_REMOVE_DELAY = 60l;

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

	private static final NPCManager manager = NPCMain.instance.npc;
    
    /**
	 * Translate colour codes and hex codes into a coloured string.
	 * @param msg The message to translate.
	 * @return The translated string.
	 */
	public static String format(String msg) {
		if (NPCMain.serverVersion.hasHexSupport) {
			Matcher match = pattern.matcher(msg);
			while (match.find()) {
				String colour = msg.substring(match.start(), match.end());
				msg = msg.replace(colour, ChatColor.of(colour) + "");
				match = pattern.matcher(msg);
			}
		}
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	/**
	 * Check if an update is available on the github repository.
	 * 
	 * @return <code>true</code> if an update is available on the github repo.
	 *         <code>false</code> otherwise.
	 */
	public static void checkForUpdate() {
		Bukkit.getScheduler().runTaskLaterAsynchronously(NPCMain.instance, new Runnable() {
			@Override
			public void run() {
				NPCMain.instance.log(Level.INFO, "Checking if you have the latest version of the plugin...");
				String current = NPCMain.instance.getDescription().getVersion();
				try {
					URL url = new URL("https://api.github.com/repos/Scroojalix/NPCManager/releases/latest");
					InputStreamReader reader = new InputStreamReader(url.openStream());
					String latest = new JsonParser().parse(reader).getAsJsonObject().get("tag_name").getAsString();
					if (!current.equalsIgnoreCase(latest.replace("v",""))) {
						Logger logger = NPCMain.instance.getLogger();
						logger.info("--------------------------------------------------------");
						logger.info("A new version of the plugin is available!");
						logger.info("It can be downloaded at:");
						logger.info("https://github.com/Scroojalix/NPCManager/releases/latest");
						logger.info("--------------------------------------------------------");
					} else {
						NPCMain.instance.log(Level.INFO, "You have the latest version of the plugin.");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 5l);
	}

	public static boolean npcExists(String name) {
		return manager.getNPCHashMap().containsKey(name);
	}

	/**
	 * Gets NPC entity ID by name
	 * Returns null if NPC doesn't exist
	 * @param name name of npc
	 * @return Entity ID of NPC with name, or null.
	 */
	public static int getNPCIDByName(String name) {
		return npcExists(name)?manager.getNPCHashMap().get(name).getNPCEntityID():null;
	}

	/**
	 * Gets NPCData object by name
	 * Returns null if NPC doesn't exist
	 * @param name name of npc
	 * @return NPCData object defined by name, or null.
	 */
	public static NPCData getNPCDataByName(String name) {
		return npcExists(name)?manager.getNPCHashMap().get(name).getNPCData():null;
	}

	/**
	 * Returns true if the NPC hashmap is empty
	 * @return the value of NPCs#isEmpty()
	 */
	public static boolean noNPCs() {
		return manager.getNPCHashMap().isEmpty();
	}

	/**
	 * Returns number of NPCs
	 * @return the value of NPCs#size()
	 */
	public static int getNumberOfNPCs() {
		return manager.getNPCHashMap().size();
	}

	/**
	 * Returns set of all NPC names
	 * @return value of NPCs#keySet()
	 */
	public static Set<String> getAllNPCNames() {
		return manager.getNPCHashMap().keySet();
	}

	public static boolean isAlphanumeric(String s) {
        return s != null && s.matches("^[a-zA-Z0-9_]+$");
	}

	@SuppressWarnings("deprecation")
	public static boolean isSuitableItem(ItemStack item, String type, Player p) {
		if (item.getType() == Material.AIR)
			return false;
		if (item.getAmount() != 1) {
			if (p != null) {
				p.playSound(p.getLocation(), Sound.valueOf(CommandUtils.getErrorSound()), 5f, 0.5f);
				p.sendMessage(ChatColor.RED+"Please do not use a stack of items.");
			}
			return false;
		}
		if (item.getType().name().toLowerCase().contains(type.toLowerCase()))
			return true;
		switch(type) {
		case "helmet":
		case "mainhand":
		case "offhand":
			try {
				return item.getType().isItem();
			} catch (NoSuchMethodError e) {
				try {
					Class<?> c = Class.forName("net.minecraft.server."+NPCMain.serverVersion.toString()+".Item");
					Method m = c.getDeclaredMethod("getById", int.class);
					return m.invoke(null, item.getType().getId()) != null;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return false;
			}
		}
		if (p != null) {
			p.playSound(p.getLocation(), Sound.valueOf(CommandUtils.getErrorSound()), 5f, 0.5f);
			p.sendMessage(ChatColor.RED+"That item cannot be placed in this slot!");
		}
        return false;
	}
	
	/**
	 * Version that the server is running.
	 * @author Scroojalix
	 */
	public enum ServerVersion {
		//TODO simplify this using a version id
		v1_8_R2(false, true, 0), v1_8_R3(false, true, 0), v1_9_R1(false, true, 1),
		v1_9_R2(false, true, 1), v1_10_R1(false, true, 1), v1_11_R1(false, true, 1),
		v1_12_R1(false, true, 1), v1_13_R1(false, false, 2), v1_13_R2(false, false, 2),
		v1_14_R1(false, false, 2), v1_15_R1(false, false, 2), v1_16_R1(true, false, 2),
		v1_16_R2(true, false, 2), v1_16_R3(true, false, 2), v1_17_R1(true, false, 2),
		v1_18_R1(true, false, 2), v1_18_R2(true, false, 2), v1_19_R1(true, false, 2),
		v1_19_R2(true, false, 2), v1_19_R3(true, false, 2);

		public final boolean hasHexSupport;
		public final boolean usesDamageForColouredMaterials;
		public final boolean hasOffHand;
		public final int errorSoundId;

		private ServerVersion(boolean hexSupport, boolean usesDamageForColouredMaterials, int errorSoundId) {
			this.hasHexSupport = hexSupport;
			this.usesDamageForColouredMaterials = usesDamageForColouredMaterials;
			this.hasOffHand = errorSoundId != 0;
			this.errorSoundId = errorSoundId;
		}
	}
}
