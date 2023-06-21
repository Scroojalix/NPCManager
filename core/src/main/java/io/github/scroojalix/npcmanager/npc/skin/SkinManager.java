package io.github.scroojalix.npcmanager.npc.skin;

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

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class SkinManager {

	public static void updateSkin(NPCData data) {
		SkinData skinData = data.getTraits().getSkinData();
		if (skinData != null) {
			if (skinData.needsUpdating())
				setSkinFromUUID(null, data, skinData.getUUID(), true);
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
					data.getTraits().setSkinData(new SkinData(SkinType.PLAYER, player.getName(), player.getUniqueId().toString().replace("-", ""), property.getValue(), property.getSignature(), keepLatest));
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
		setSkinFromUsername(sender, data, username, keepLatest, false);
	}

	public static void setSkinFromUsername(
			CommandSender sender,
			NPCData data,
			String username,
			boolean keepLatest,
			boolean fetchedSkin) {
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
					setSkinFromUUID(sender, data, uuid, keepLatest);
				} catch (Exception e) {
					if (!fetchedSkin) {
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
			}
		});
	}

	public static void setSkinFromUUID(
		CommandSender sender,
		NPCData data,
		String uuid,
		boolean keepLatest) {
		Bukkit.getScheduler().runTaskAsynchronously(NPCMain.instance, new Runnable() {
			@Override
			public void run() {
				try {
					URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
					InputStreamReader reader = new InputStreamReader(url.openStream());
					JsonObject obj = new JsonParser().parse(reader).getAsJsonObject();
					String name = obj.get("name").getAsString();
					JsonObject property = obj.get("properties").getAsJsonArray().get(0).getAsJsonObject();
					String texture = property.get("value").getAsString();
					String signature = property.get("signature").getAsString();
					data.getTraits().setSkinData(new SkinData(SkinType.PLAYER, name, uuid, texture, signature, keepLatest));
					data.getTraits().getSkinData().setHasUpdated(true);
					saveAndUpdateNPCSynchronously(sender, data, "&6Successfully fetched skin data from the username &F"+name);
				} catch (Exception e) {
					e.printStackTrace();
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
					JsonObject output = new JsonParser().parse(reader).getAsJsonObject();
					JsonObject json = output.get("data").getAsJsonObject();
					String uuid = json.get("uuid").getAsString();
					JsonObject texture = json.get("texture").getAsJsonObject();
					String textureEncoded = texture.get("value").getAsString();
					String signature = texture.get("signature").getAsString();
					con.disconnect();
					data.getTraits().setSkinData(new SkinData(SkinType.URL, uuid, uuid.replace("-", ""), textureEncoded, signature, false));
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
				NPCMain.instance.npc.updateNPC(data);
				if (sender != null) {
					sender.sendMessage(PluginUtils.format(message));
				}
			}
		});
	}
}
