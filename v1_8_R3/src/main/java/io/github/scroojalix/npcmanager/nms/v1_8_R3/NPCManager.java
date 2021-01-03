package io.github.scroojalix.npcmanager.nms.v1_8_R3;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.nms.interfaces.INPCManager;
import io.github.scroojalix.npcmanager.nms.interfaces.NMSHologram;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.npc.NPCTrait;
import io.github.scroojalix.npcmanager.utils.npc.skin.SkinData;

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
		super(main);
		npcTeam = new ScoreboardTeam(((CraftScoreboard)Bukkit.getScoreboardManager().getNewScoreboard()).getHandle(), "zzzzzzzzzzNMNPCs");
		npcTeam.setPrefix(PluginUtils.format("&8[NPC] "));
		npcTeam.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
	}
	
	public ScoreboardTeam getNPCTeam() {
		return npcTeam;
	}

	public void sendRemoveNPCPackets(Player p, NPCData data) {
		EntityNMSPlayer npcEntity = (EntityNMSPlayer) data.getNPC();
		PlayerConnection connection = ((CraftPlayer)p).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutEntityDestroy(npcEntity.getId()));
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npcEntity));
	}

	public void createNPCData(NPCData data) {
		//NPC
        NPCTrait traits = data.getTraits();
		GameProfile profile = new GameProfile(data.getUUID(), data.getName());
		SkinData skin = traits.getSkinData();
		if (skin != null && skin.getTexture() != null && skin.getSignature() != null) {
			profile.getProperties().put("textures", new Property("textures", skin.getTexture(), skin.getSignature()));
		}
		MinecraftServer server = ((CraftServer)Bukkit.getServer()).getServer();
		WorldServer world = ((CraftWorld)data.getLoc().getWorld()).getHandle();
        EntityNMSPlayer npc = new EntityNMSPlayer(server, world, profile, new PlayerInteractManager(world), data);
        data.setNPC(npc);

        //Holograms
        String displayName = data.getTraits().getDisplayName();
		String subtitle = data.getTraits().getSubtitle();
		
		boolean hasDisplayName = displayName != null;
		boolean hasSubtitle = subtitle != null;
		Location loc = data.getLoc();
		Location upperLoc = new Location(loc.getWorld(), loc.getX(), loc.getY()+2.15, loc.getZ());
		Location lowerLoc = new Location(loc.getWorld(), loc.getX(), loc.getY()+1.9, loc.getZ());
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
    }

	public void spawnNPC(NPCData data) {
		data.setLoaderTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new NPCLoader(main, data, this), 0l, 1l));
	}

	public void removeHologramForPlayer(Player player, NMSHologram hologram) {
		PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutEntityDestroy(((EntityNMSHologram) hologram.getEntity()).getId()));
	}
}