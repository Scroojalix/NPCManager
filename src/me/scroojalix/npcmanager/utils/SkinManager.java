package me.scroojalix.npcmanager.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.WordUtils;
import org.bukkit.configuration.file.FileConfiguration;

import me.scroojalix.npcmanager.NPCMain;

public class SkinManager {
	
	private NPCMain main;
	private Map<String, String[]> skins = new HashMap<String, String[]>();
	
	public SkinManager(NPCMain main) {
		this.main = main;
		generateSkins();
	}
	
	/**
	 * Generate skin data from skins.yml
	 */
	public void generateSkins() {
		skins.clear();
		FileConfiguration data = main.skinFile.getConfig();
		data.getConfigurationSection("skins").getKeys(false).forEach(name -> {
			if (data.isSet("skins."+name+".UUID") && data.isSet("skins."+name+".Texture") && data.isSet("skins."+name+".Signature")) {
				String id = data.getString("skins."+name+".UUID");
				try {
					UUID.fromString(id);
				} catch(IllegalArgumentException e) {
					main.getLogger().severe("'"+id+"' in skins.yml is not a valid UUID!");
					return;
				}
				String Texture = data.getString("skins."+name+".Texture");
				String Signature = data.getString("skins."+name+".Signature");
				skins.put(WordUtils.capitalize(name.toLowerCase()), new String[] {id, Texture, Signature});
			}
		});
	}
	
	public String[] getSkinData(String name) {
		return skins.get(WordUtils.capitalize(name.toLowerCase()));
	}
	
	public Set<String> values() {
		return skins.keySet();
	}

}
