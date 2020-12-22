package io.github.scroojalix.npcmanager.nms.v1_13_R2;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftChatMessage;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.nms.interfaces.INPCManager;
import io.github.scroojalix.npcmanager.nms.interfaces.NMSHologram;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.npc.NPCTrait;
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
		super(main);
		npcTeam = new ScoreboardTeam(((CraftScoreboard)Bukkit.getScoreboardManager().getNewScoreboard()).getHandle(), "zzzzzzzzzzNMNPCs");
		npcTeam.setCollisionRule(EnumTeamPush.NEVER);
		npcTeam.setColor(EnumChatFormat.DARK_GRAY);
		npcTeam.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
		npcTeam.setPrefix(CraftChatMessage.fromStringOrNull(PluginUtils.format("&8[NPC] ")));

		restoreNPCs();
	}
	
	public ScoreboardTeam getNPCTeam() {
		return npcTeam;
	}
		
	public void sendRemoveNPCPackets(Player p, NPCData data) {
		EntityPlayer npcEntity = (EntityPlayer) data.getNPC();
		PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutEntityDestroy(npcEntity.getId()));
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npcEntity));
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
		npc.getDataWatcher().set(DataWatcherRegistry.a.a(13), (byte)126);
		data.setNPC(npc, npc.getProfile().getId().toString());
	}
	
	public void restoreNPC(NPCData data) {
		getNMSEntity(data);
		String displayName = data.getTraits().getDisplayName();
		String subtitle = data.getTraits().getSubtitle();
		
		//Holograms
		boolean hasDisplayName = displayName != null;
		boolean hasSubtitle = subtitle != null;
		Location loc = data.getLoc();
		Location upperLoc = new Location(loc.getWorld(), loc.getX(), loc.getY()+1.95, loc.getZ());
		Location lowerLoc = new Location(loc.getWorld(), loc.getX(), loc.getY()+1.7, loc.getZ());
		if (hasDisplayName && hasSubtitle) {
			data.setNameHolo(new EntityNMSHologram(upperLoc, PluginUtils.format(displayName)));
			data.setSubtitleHolo(new EntityNMSHologram(lowerLoc, PluginUtils.format(subtitle)));
		} else if (hasDisplayName && !hasSubtitle){
			data.setNameHolo(new EntityNMSHologram(lowerLoc, PluginUtils.format(displayName)));
			data.setSubtitleHolo(null);
		} else if (!hasDisplayName && hasSubtitle) {
			data.setNameHolo(null);
			data.setSubtitleHolo(new EntityNMSHologram(lowerLoc, PluginUtils.format(subtitle)));
		} else {
			data.setNameHolo(null);
			data.setSubtitleHolo(null);
		}
		
		NPCs.put(data.getName(), data);
		data.setLoaderTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new NPCLoader(main, data, this), 0l, 1l));
	}

	public void removeHologramForPlayer(Player player, NMSHologram hologram) {
		PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutEntityDestroy(((EntityNMSHologram) hologram.getEntity()).getId()));
	}
}