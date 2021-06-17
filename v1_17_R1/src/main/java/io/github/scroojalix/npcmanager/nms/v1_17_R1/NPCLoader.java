package io.github.scroojalix.npcmanager.nms.v1_17_R1;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.mojang.datafixers.util.Pair;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.common.PluginUtils;
import io.github.scroojalix.npcmanager.common.npc.NPCData;
import io.github.scroojalix.npcmanager.common.npc.equipment.NPCEquipment;
import io.github.scroojalix.npcmanager.nms.interfaces.INPCLoader;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;

public class NPCLoader extends INPCLoader implements Runnable {
	
	private NPCManager npcClass;
	private Set<Packet<?>> packets;
	
	public NPCLoader(NPCMain main, NPCData data, NPCManager npcClass) {
		super(main, data);
		packets = new LinkedHashSet<Packet<?>>();
		this.npcClass = npcClass;
		generatePackets();
	}

	protected void generatePackets() {
		EntityNMSPlayer npc = (EntityNMSPlayer) data.getNPC();
		packets.add(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, npc));
		packets.add(new ClientboundAddPlayerPacket(npc));
		packets.add(new ClientboundSetEntityDataPacket(npc.getId(), npc.getEntityData(), true));
		packets.add(new ClientboundRotateHeadPacket(npc, (byte) (npc.getYRot() * 256 / 360)));
		
		packets.add(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(npcClass.getNPCTeam(), true));
		packets.add(ClientboundSetPlayerTeamPacket.createPlayerPacket(npcClass.getNPCTeam(), data.getNPC().getProfile().getName(), Action.ADD));
		
		if (perfectOrientation) {
			packets.add(new ClientboundAnimatePacket(npc, 0));
		}
		
		//Holograms
		ArmorStand holo;
		if (data.getNameHolo() != null) {
			holo = (ArmorStand) data.getNameHolo().getEntity();
			packets.add(new ClientboundAddEntityPacket(holo));
			packets.add(new ClientboundSetEntityDataPacket(holo.getId(), holo.getEntityData(), true));
		}
		
		if (data.getSubtitleHolo() != null) {
			holo = (ArmorStand) data.getSubtitleHolo().getEntity();
			packets.add(new ClientboundAddEntityPacket(holo));
			packets.add(new ClientboundSetEntityDataPacket(holo.getId(), holo.getEntityData(), true));
		}
		
		//Equipment
		if (data.getTraits().getEquipment(false) != null) {
			final List<Pair<EquipmentSlot, ItemStack>> equipmentList = new ArrayList<>();
			NPCEquipment equipment = data.getTraits().getEquipment(false);
			if (equipment.getMainhandItem() != null) {
				equipmentList.add(new Pair<>(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(equipment.getMainhandItem())));
			}
			if (equipment.getOffhandItem() != null) {
				equipmentList.add(new Pair<>(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(equipment.getOffhandItem())));
			}
			if (equipment.getHelmet() != null) {
				equipmentList.add(new Pair<>(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(equipment.getHelmet())));
			}
			if (equipment.getChestplate() != null) {
				equipmentList.add(new Pair<>(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(equipment.getChestplate())));
			}
			if (equipment.getLeggings() != null) {
				equipmentList.add(new Pair<>(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(equipment.getLeggings())));
			}
			if (equipment.getBoots() != null) {
				equipmentList.add(new Pair<>(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(equipment.getBoots())));
			}
			if (!equipmentList.isEmpty()) {
				packets.add(new ClientboundSetEquipmentPacket(npc.getId(), equipmentList));
			}
		}
	}

	protected void lookInDirection(Player player) {
    	EntityNMSPlayer npc = (EntityNMSPlayer)data.getNPC();        
        ServerGamePacketListenerImpl connection = ((CraftPlayer)player).getHandle().connection;
        Vector difference = player.getLocation().subtract(npc.getBukkitEntity().getLocation()).toVector().normalize();
        float degrees = (float) Math.toDegrees(Math.atan2(difference.getZ(), difference.getX()) - Math.PI / 2);
        byte angle = (byte) Mth.floor((degrees * 256.0F) / 360.0F);
        Vector height = npc.getBukkitEntity().getLocation().subtract(player.getLocation()).toVector().normalize();
        byte pitch = (byte) Mth.floor((Math.toDegrees(Math.atan(height.getY())) * 256.0F) / 360.0F);

        connection.send(new ClientboundRotateHeadPacket(npc, angle));
        connection.send(new ClientboundMoveEntityPacket.Rot(npc.getId(), angle, pitch, true));
	}
	
	protected void resetLookDirection(Player player) {
    	EntityNMSPlayer npc = (EntityNMSPlayer)data.getNPC();        
        ServerGamePacketListenerImpl connection = ((CraftPlayer)player).getHandle().connection;
        byte yaw = (byte) (data.getLoc().getYaw() * 255 / 360);
        byte pitch = (byte) (data.getLoc().getPitch() * 255 / 360);
        connection.send(new ClientboundRotateHeadPacket(npc, yaw));
        connection.send(new ClientboundMoveEntityPacket.Rot(npc.getId(), yaw, pitch, true));
	}

	protected void sendLoadPackets(Player player) {
		ServerGamePacketListenerImpl connection = ((CraftPlayer)player).getHandle().connection;
		for (Packet<?> packet : packets) {
			connection.send(packet);
		}
		loadedForPlayers.put(player, Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			@Override
			public void run() {
				connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, (EntityNMSPlayer)data.getNPC()));
			}
		}, PluginUtils.NPC_REMOVE_DELAY));
	}

	protected void sendDeletePackets(Player player) {
		EntityNMSPlayer npc = (EntityNMSPlayer)data.getNPC();
		ServerGamePacketListenerImpl connection = ((CraftPlayer)player).getHandle().connection;
		connection.send(new ClientboundRemoveEntityPacket(npc.getId()));
		connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, npc));
		
		if (data.getNameHolo() != null) {
			connection.send(new ClientboundRemoveEntityPacket(((ArmorStand) data.getNameHolo().getEntity()).getId()));
		}
		if (data.getSubtitleHolo() != null) {
			connection.send(new ClientboundRemoveEntityPacket(((ArmorStand) data.getSubtitleHolo().getEntity()).getId()));
		}
		
		Bukkit.getScheduler().cancelTask(loadedForPlayers.get(player));
		loadedForPlayers.remove(player);
	}
}
