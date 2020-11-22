package io.github.scroojalix.npcmanager.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.scroojalix.npcmanager.NPCMain;
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
