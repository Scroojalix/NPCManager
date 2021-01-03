package io.github.scroojalix.npcmanager.utils.npc.skin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;

public class SkinManager {

	public static void updateSkin(NPCData data) {
		SkinData skinData = data.getTraits().getSkinData();
		if (skinData != null) {
			if (skinData.needsUpdating())
				setSkinFromUsername(null, data, skinData.getSkinName(), true);
		}
	}

	public static void setSkinFromOnlinePlayer(CommandSender sender, NPCData data, Player player, boolean keepLatest) {
		Bukkit.getScheduler().runTaskAsynchronously(NPCMain.instance, new Runnable() {
			@Override
			public void run() {
				try {
					Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + NPCMain.serverVersion.toString() + ".entity.CraftPlayer");
					GameProfile profile = (GameProfile) craftPlayerClass.getMethod("getProfile").invoke(craftPlayerClass.cast(player));
					Property property = profile.getProperties().get("textures").iterator().next();
					data.getTraits().setSkinData(new SkinData(player.getName(), property.getValue(), property.getSignature(), keepLatest));
					data.getTraits().getSkinData().setHasUpdated(true);
					saveAndUpdateNPCSynchronously(sender, data, "&6Successfully fetched skin data from the username &F"+player.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void setSkinFromUsername(
			CommandSender sender,
			NPCData data,
			String username,
			boolean keepLatest) {
		if (Bukkit.getServer().getPlayerExact(username) != null) {
			if (sender != null) {
				sender.sendMessage(PluginUtils.format("&6Hey! That player is online. Getting skin data from them."));
			}
			setSkinFromOnlinePlayer(sender, data, Bukkit.getServer().getPlayerExact(username), keepLatest);
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(NPCMain.instance, new Runnable() {
			@Override
			public void run() {
				try {
					URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
					InputStreamReader reader = new InputStreamReader(url.openStream());
					String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();
		
					URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
					InputStreamReader reader2 = new InputStreamReader(url2.openStream());
					JsonObject obj = new JsonParser().parse(reader2).getAsJsonObject();
					String name = obj.get("name").getAsString();
					JsonObject property = obj.get("properties").getAsJsonArray().get(0).getAsJsonObject();
					String texture = property.get("value").getAsString();
					String signature = property.get("signature").getAsString();
					//TODO access NPC game profile and set texture and signature, without updating NPC
					//May have to add another nms interface NMSPlayer and invoke each NMS version manually
					data.getTraits().setSkinData(new SkinData(name, texture, signature, keepLatest));
					data.getTraits().getSkinData().setHasUpdated(true);
					saveAndUpdateNPCSynchronously(sender, data, "&6Successfully fetched skin data from the username &F"+username);
				} catch (Exception e) {
					if (sender != null) {
						Bukkit.getScheduler().runTask(NPCMain.instance, new Runnable() {
							@Override
							public void run() {
								sender.sendMessage(ChatColor.RED+"Something happened when attempting to fetch skin data from the username '"+username+"'. That player may not exist.");
							}
						});
					} else {
						throw new IllegalArgumentException("Something happened when attempting to fetch skin data from the username '"+username+"'. That player may not exist.");
					}
				}
			}
		});
	}
	
	public static void setSkinFromURL(
			CommandSender sender,
			NPCData data,
			String url,
			boolean useSlimModel) {
		Bukkit.getScheduler().runTaskAsynchronously(NPCMain.instance, new Runnable() {
			@Override
			public void run() {
				try {
					URL target = new URL(getFormattedURL(url, useSlimModel));
					HttpURLConnection con = (HttpURLConnection) target.openConnection();
					con.setRequestMethod("POST");
					con.setDoOutput(true);
					con.setConnectTimeout(5000);
					con.setReadTimeout(60000);
					BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
					JSONObject output = (JSONObject) new JSONParser().parse(reader);
					JSONObject json = (JSONObject) output.get("data");
					String uuid = (String) json.get("uuid");
					JSONObject texture = (JSONObject) json.get("texture");
					String textureEncoded = (String) texture.get("value");
					String signature = (String) texture.get("signature");
					con.disconnect();
					data.getTraits().setSkinData(new SkinData(uuid, textureEncoded, signature, false));
					saveAndUpdateNPCSynchronously(sender, data, "&6Successfully fetched skin data from the URL.");
				} catch (Exception e) {
					if (sender != null) {
						Bukkit.getScheduler().runTask(NPCMain.instance, new Runnable() {
							@Override
							public void run() {
								sender.sendMessage(ChatColor.RED+"Could not fetch skin data from the URL. Ensure that it is a valid URL and it links to a valid .png file.");
							}
						});
					} else {
						throw new IllegalArgumentException("Could not fetch skin data from the URL. Ensure that it is a valid URL and it links to a valid .png file.");
					}
				}
			}
		});
	}

	private static String getFormattedURL(String url, boolean useSlimModel) throws UnsupportedEncodingException {
		StringBuilder result = new StringBuilder("https://api.mineskin.org/generate/url");
		result.append("?url="+URLEncoder.encode(url, "UTF-8"));
		result.append("&visibility=1");
		if (useSlimModel) {
			result.append("&model=slim");
		}
		return result.toString();
	}

	private static void saveAndUpdateNPCSynchronously(CommandSender sender, NPCData data, String message) {
		Bukkit.getScheduler().runTask(NPCMain.instance, new Runnable() {
			@Override
			public void run() {
				NPCMain.instance.npc.saveNPC(data);
				NPCMain.instance.npc.updateNPC(data);
				if (sender != null) {
					sender.sendMessage(PluginUtils.format(message));
				}
			}
		});
	}
}
