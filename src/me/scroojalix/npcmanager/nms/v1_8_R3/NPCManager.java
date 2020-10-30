package me.scroojalix.npcmanager.nms.v1_8_R3;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.scroojalix.npcmanager.NPCMain;
import me.scroojalix.npcmanager.nms.interfaces.INPCManager;
import me.scroojalix.npcmanager.nms.interfaces.NMSHologram;
import me.scroojalix.npcmanager.utils.NPCData;
import me.scroojalix.npcmanager.utils.NPCTrait;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumChatFormat;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.ScoreboardTeam;
import net.minecraft.server.v1_8_R3.ScoreboardTeamBase;
import net.minecraft.server.v1_8_R3.WorldServer;

public class NPCManager extends INPCManager {
	
	private ScoreboardTeam npcTeam;
	
	public NPCManager(NPCMain main) {
		this.main = main;
		npcTeam = new ScoreboardTeam(((CraftScoreboard)Bukkit.getScoreboardManager().getNewScoreboard()).getHandle(), "zzzzzzzzzzNMNPCs");
		npcTeam.a(EnumChatFormat.DARK_GRAY);
		npcTeam.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
		NPCs = new HashMap<String, NPCData>();

		restoreNPCs();
	}
	
	public ScoreboardTeam getNPCTeam() {
		return npcTeam;
	}
	
	public void moveNPC(NPCData data, Location loc) {
		data.setLoc(loc);
		((EntityPlayer)data.getNPC()).setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		updateNPC(data);
	}
	
	public void removeNPC(String npc) {
		NPCData data = NPCs.get(npc);
		Bukkit.getScheduler().cancelTask(data.getHeadRotationTask());
		Bukkit.getScheduler().cancelTask(data.getLoaderTask());
		hiddenNPCs.remove(data.getName());
		EntityPlayer npcEntity = (EntityPlayer) data.getNPC();
		for (Player p : Bukkit.getOnlinePlayers()) {
			PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
			connection.sendPacket(new PacketPlayOutEntityDestroy(npcEntity.getId()));
			connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npcEntity));
			if (data.getNameHolo() != null) {
				removeHologramForPlayer(p, data.getNameHolo());
			}
			if (data.getSubtitleHolo() != null) {
				removeHologramForPlayer(p, data.getSubtitleHolo());
			}
		}
	}
	
	public void getNMSEntity(NPCData data) {
		GameProfile profile;
		NPCTrait traits = data.getTraits();
		if (traits.getSkin() != null && main.skinManager.values().contains(traits.getSkin())) {
			String[] profileData = main.skinManager.getSkinData(traits.getSkin());
			profile = new GameProfile(UUID.fromString(profileData[0]), data.getName());
			profile.getProperties().put("textures", new Property("textures", profileData[1], profileData[2]));
		} else if(data.getUUID() != null) {
			profile = new GameProfile(UUID.fromString(data.getUUID()), data.getName());
		}else {
			profile = new GameProfile(UUID.randomUUID(), data.getName());
		}
		MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer world = ((CraftWorld) data.getLoc().getWorld()).getHandle();
		EntityPlayer npc = new EntityPlayer(server,world,profile, new PlayerInteractManager(world));
		npc.setLocation(data.getLoc().getX(), data.getLoc().getY(), data.getLoc().getZ(), data.getLoc().getYaw(), data.getLoc().getPitch());
		npc.getDataWatcher().watch(10, (byte)127);
		data.setNPC(npc, npc.getProfile().getId().toString());
		server.getPlayerList().players.remove(data.getNPC());
		hiddenNPCs.add(((EntityPlayer)data.getNPC()).getName());
	}
	
	public void restoreNPC(NPCData data) {
		getNMSEntity(data);
		String displayName = data.getTraits().getDisplayName();
		String subtitle = data.getTraits().getSubtitle();
		
		//Holograms
		boolean hasDisplayName = displayName != null;
		boolean hasSubtitle = subtitle != null;
		Location loc = data.getLoc();
		Location upperLoc = new Location(loc.getWorld(), loc.getX(), loc.getY()+2.15, loc.getZ());
		Location lowerLoc = new Location(loc.getWorld(), loc.getX(), loc.getY()+1.9, loc.getZ());
		if (hasDisplayName && hasSubtitle) {
			data.setNameHolo(new EntityNMSHologram(upperLoc, main.format(displayName)));
			data.setSubtitleHolo(new EntityNMSHologram(lowerLoc, main.format(subtitle)));
		} else if (hasDisplayName && !hasSubtitle){
			data.setNameHolo(new EntityNMSHologram(lowerLoc, main.format(displayName)));
			data.setSubtitleHolo(null);
		} else if (!hasDisplayName && hasSubtitle) {
			data.setNameHolo(null);
			data.setSubtitleHolo(new EntityNMSHologram(lowerLoc, main.format(subtitle)));
		} else {
			data.setNameHolo(null);
			data.setSubtitleHolo(null);
		}
		
		data.setLoaderTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new NPCLoader(main, data, this), 0l, 1l));
		
		NPCs.put(data.getName(), data);
	}

	public void removeHologramForPlayer(Player player, NMSHologram hologram) {
		PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutEntityDestroy(((EntityNMSHologram) hologram.getEntity()).getId()));
	}
}