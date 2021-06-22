package io.github.scroojalix.npcmanager.nms.v1_17_R1;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.common.PluginUtils;
import io.github.scroojalix.npcmanager.common.npc.NPCData;
import io.github.scroojalix.npcmanager.common.npc.NPCTrait;
import io.github.scroojalix.npcmanager.common.npc.skin.SkinData;
import io.github.scroojalix.npcmanager.nms.interfaces.INPCManager;
import io.github.scroojalix.npcmanager.nms.interfaces.NMSHologram;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntityPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team.CollisionRule;
import net.minecraft.world.scores.Team.Visibility;

public class NPCManager extends INPCManager {
	
	private PlayerTeam npcTeam;
	
	public NPCManager(NPCMain main) {
		super(main);
		npcTeam = new PlayerTeam(((CraftScoreboard)Bukkit.getScoreboardManager().getNewScoreboard()).getHandle(), "zzzzzzzzzzNMNPCs");
		npcTeam.setCollisionRule(CollisionRule.NEVER);
		npcTeam.setColor(ChatFormatting.DARK_GRAY);
		npcTeam.setNameTagVisibility(Visibility.NEVER);
		npcTeam.setPlayerPrefix(CraftChatMessage.fromStringOrNull(PluginUtils.format("&8[NPC] ")));
	}
	
	public PlayerTeam getNPCTeam() {
		return npcTeam;
	}
		
	public void sendRemoveNPCPackets(Player p, NPCData data) {
		EntityNMSPlayer npcEntity = (EntityNMSPlayer) data.getNPC();
		ServerGamePacketListenerImpl connection = ((CraftPlayer)p).getHandle().connection;
		connection.send(new ClientboundRemoveEntityPacket(npcEntity.getId()));
		connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npcEntity));
	}

	public void createNPCData(NPCData data) {
		//NPC
        NPCTrait traits = data.getTraits();
		GameProfile profile = new GameProfile(data.getUUID(), getRandomNPCName());
		SkinData skin = traits.getSkinData();
		if (skin != null && skin.getTexture() != null && skin.getSignature() != null) {
			profile.getProperties().put("textures", new Property("textures", skin.getTexture(), skin.getSignature()));
		}
		MinecraftServer server = ((CraftServer)Bukkit.getServer()).getServer();
		ServerLevel world = ((CraftWorld)data.getLoc().getWorld()).getHandle();
        EntityNMSPlayer npc = new EntityNMSPlayer(server, world, profile, data);
        data.setNPC(npc);

        //Holograms
        String displayName = data.getTraits().getDisplayName();
		String subtitle = data.getTraits().getSubtitle();
		
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
	}

	public void startLoaderTask(NPCData data) {
		NPCLoader loader = new NPCLoader(main, data, this);
		data.setLoaderTask(Bukkit.getScheduler().scheduleSyncRepeatingTask(main, loader, 0l, 1l), loader);
	}

	public void removeHologramForPlayer(Player player, NMSHologram hologram) {
		ServerGamePacketListenerImpl connection = ((CraftPlayer)player).getHandle().connection;
		connection.send(new ClientboundRemoveEntityPacket(((EntityNMSHologram) hologram.getEntity()).getId()));
	}
}