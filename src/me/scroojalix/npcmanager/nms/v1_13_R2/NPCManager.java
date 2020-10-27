package me.scroojalix.npcmanager.nms.v1_13_R2;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.scroojalix.npcmanager.NPCMain;
import me.scroojalix.npcmanager.nms.interfaces.INPCManager;
import me.scroojalix.npcmanager.nms.interfaces.NMSHologram;
import me.scroojalix.npcmanager.utils.NPCData;
import net.minecraft.server.v1_13_R2.DataWatcherRegistry;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.EnumChatFormat;
import net.minecraft.server.v1_13_R2.MinecraftServer;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_13_R2.PlayerConnection;
import net.minecraft.server.v1_13_R2.PlayerInteractManager;
import net.minecraft.server.v1_13_R2.ScoreboardTeam;
import net.minecraft.server.v1_13_R2.ScoreboardTeamBase;
import net.minecraft.server.v1_13_R2.ScoreboardTeamBase.EnumTeamPush;
import net.minecraft.server.v1_13_R2.WorldServer;

public class NPCManager extends INPCManager {
	
	private ScoreboardTeam npcTeam;
	
	public NPCManager(NPCMain main) {
		this.main = main;
		npcTeam = new ScoreboardTeam(((CraftScoreboard)Bukkit.getScoreboardManager().getNewScoreboard()).getHandle(), "zzzzzzzzzzNMNPCs");
		npcTeam.setCollisionRule(EnumTeamPush.NEVER);
		npcTeam.setColor(EnumChatFormat.DARK_GRAY);
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
		if (data.getSkin() != null && main.skinManager.values().contains(data.getSkin())) {
			String[] profileData = main.skinManager.getSkinData(data.getSkin());
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
		npc.getDataWatcher().set(DataWatcherRegistry.a.a(13), (byte)127);
		data.setNPC(npc, npc.getProfile().getId().toString());
		server.getPlayerList().players.remove(data.getNPC());
		hiddenNPCs.add(((EntityPlayer)data.getNPC()).getName());
	}
	
	public void restoreNPC(NPCData data) {
		getNMSEntity(data);
		
		//Holograms
		boolean hasDisplayName = data.getDisplayName() != null;
		boolean hasSubtitle = data.getSubtitle() != null;
		Location loc = data.getLoc();
		Location upperLoc = new Location(loc.getWorld(), loc.getX(), loc.getY()+1.95, loc.getZ());
		Location lowerLoc = new Location(loc.getWorld(), loc.getX(), loc.getY()+1.7, loc.getZ());
		if (hasDisplayName && hasSubtitle) {
			data.setNameHolo(new EntityNMSHologram(upperLoc, main.format(data.getDisplayName())));
			data.setSubtitleHolo(new EntityNMSHologram(lowerLoc, main.format(data.getSubtitle())));
		} else if (hasDisplayName && !hasSubtitle){
			data.setNameHolo(new EntityNMSHologram(lowerLoc, main.format(data.getDisplayName())));
			data.setSubtitleHolo(null);
		} else if (!hasDisplayName && hasSubtitle) {
			data.setNameHolo(null);
			data.setSubtitleHolo(new EntityNMSHologram(lowerLoc, main.format(data.getSubtitle())));
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