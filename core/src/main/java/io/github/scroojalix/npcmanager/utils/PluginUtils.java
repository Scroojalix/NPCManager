package io.github.scroojalix.npcmanager.utils;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.commands.CommandUtils;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import net.md_5.bungee.api.ChatColor;

public class PluginUtils {

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
    
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

	public static boolean suitableData(NPCData data) {
		if (data != null) {
			if (!data.isWorldNull()) {
				return true;
			} else {
				NPCMain.instance.log(Level.WARNING, "Could not reload NPC: Unknown World");
			}
		} else {
			NPCMain.instance.log(Level.WARNING, "Could not reload NPC: Invalid JSON");
		}
		return false;
	}

	public static boolean isSuitableItem(ItemStack item, String type, Player p) {
        if (item.getType().name().toLowerCase().contains(type.toLowerCase())) {
            return true;
        } else if (type.equalsIgnoreCase("helmet")) {
            if (item.getType().isBlock()) {
                return true;
            }
            String name = item.getType().name();
            if (name.contains("HEAD") || name.contains("SKULL") && !name.equalsIgnoreCase("SKULL_BANNER_PATTERN")) {
                return true;
            }
        } else if (type.equalsIgnoreCase("item")) {
            try {
                if (item.getType().isItem()) {
                    return true;
                }
            } catch (NoSuchMethodError e) {
                return true;
            }
		}
		if (p != null) {
			p.playSound(p.getLocation(), Sound.valueOf(CommandUtils.getErrorSound()), 5f, 0.5f);
			p.sendMessage(ChatColor.RED+"That item cannot be placed in this slot!");
		}
        return false;
    }
	
	/**
	 * Save method for the plugin. 
	 * @author Scroojalix
	 */
	public enum SaveMethod {
		YAML, JSON, MYSQL;
	}

	/**
	 * Version that the server is running.
	 * @author Scroojalix
	 */
	public enum ServerVersion {
		v1_8_R2(false, true, 0), v1_8_R3(false, true, 0), v1_9_R1(false, true, 1),
		v1_9_R2(false, true, 1), v1_10_R1(false, true, 1), v1_11_R1(false, true, 1),
		v1_12_R1(false, true, 1), v1_13_R1(false, false, 2), v1_13_R2(false, false, 2),
		v1_14_R1(false, false, 2), v1_15_R1(false, false, 2), v1_16_R1(true, false, 2),
		v1_16_R2(true, false, 2), v1_16_R3(true, false, 2);

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
