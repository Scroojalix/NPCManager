package io.github.scroojalix.npcmanager.nms.v1_15_R1;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.nms.interfaces.INPCLoader;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.npc.equipment.NPCEquipment;
import net.minecraft.server.v1_15_R1.EntityArmorStand;
import net.minecraft.server.v1_15_R1.EnumItemSlot;
import net.minecraft.server.v1_15_R1.MathHelper;
import net.minecraft.server.v1_15_R1.Packet;
import net.minecraft.server.v1_15_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntity;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_15_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_15_R1.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_15_R1.PlayerConnection;

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
		EntityNMSPlayer npc = (EntityNMSPlayer)data.getNPC();		
		packets.add(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
		packets.add(new PacketPlayOutNamedEntitySpawn(npc));
		packets.add(new PacketPlayOutEntityMetadata(npc.getId(), npc.getDataWatcher(), true));
		packets.add(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));
		
		packets.add(new PacketPlayOutScoreboardTeam(npcClass.getNPCTeam(), 0));
		packets.add(new PacketPlayOutScoreboardTeam(npcClass.getNPCTeam(), npcClass.getNPCs().keySet(),3));
		
		packets.add(new PacketPlayOutAnimation(npc, 0));
		
		//Holograms
		EntityArmorStand holo;
		if (data.getNameHolo() != null) {
			holo = (EntityArmorStand) data.getNameHolo().getEntity();
			packets.add(new PacketPlayOutSpawnEntityLiving(holo));
			packets.add(new PacketPlayOutEntityMetadata(holo.getId(), holo.getDataWatcher(), true));
		}
		
		if (data.getSubtitleHolo() != null) {
			holo = (EntityArmorStand) data.getSubtitleHolo().getEntity();
			packets.add(new PacketPlayOutSpawnEntityLiving(holo));
			packets.add(new PacketPlayOutEntityMetadata(holo.getId(), holo.getDataWatcher(), true));
		}
		
		//Equipment
		if (data.getTraits().getEquipment(false) != null) {
			NPCEquipment equipment = data.getTraits().getEquipment(false);
			if (equipment.getMainhandItem() != null) {
				packets.add(new PacketPlayOutEntityEquipment(npc.getId(), EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(equipment.getMainhandItem())));
			}
			if (equipment.getOffhandItem() != null) {
				packets.add(new PacketPlayOutEntityEquipment(npc.getId(), EnumItemSlot.OFFHAND, CraftItemStack.asNMSCopy(equipment.getOffhandItem())));
			}
			if (equipment.getHelmet() != null) {
				packets.add(new PacketPlayOutEntityEquipment(npc.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(equipment.getHelmet())));
			}
			if (equipment.getChestplate() != null) {
				packets.add(new PacketPlayOutEntityEquipment(npc.getId(), EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(equipment.getChestplate())));
			}
			if (equipment.getLeggings() != null) {
				packets.add(new PacketPlayOutEntityEquipment(npc.getId(), EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(equipment.getLeggings())));
			}
			if (equipment.getBoots() != null) {
				packets.add(new PacketPlayOutEntityEquipment(npc.getId(), EnumItemSlot.FEET, CraftItemStack.asNMSCopy(equipment.getBoots())));
			}
		}
	}

	protected void lookInDirection(Player player) {
    	EntityNMSPlayer npc = (EntityNMSPlayer)data.getNPC();        
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        Vector difference = player.getLocation().subtract(npc.getBukkitEntity().getLocation()).toVector().normalize();
        float degrees = (float) Math.toDegrees(Math.atan2(difference.getZ(), difference.getX()) - Math.PI / 2);
        byte angle = (byte) MathHelper.d((degrees * 256.0F) / 360.0F);
        Vector height = npc.getBukkitEntity().getLocation().subtract(player.getLocation()).toVector().normalize();
        byte pitch = (byte) MathHelper.d((Math.toDegrees(Math.atan(height.getY())) * 256.0F) / 360.0F);

        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, angle));
        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(npc.getId(), angle, pitch, true));
	}
	
	protected void resetLookDirection(Player player) {
    	EntityNMSPlayer npc = (EntityNMSPlayer)data.getNPC();        
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        byte yaw = (byte) (data.getLoc().getYaw() * 255 / 360);
        byte pitch = (byte) (data.getLoc().getPitch() * 255 / 360);
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, yaw));
        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(npc.getId(), yaw, pitch, true));
	}

	protected void sendLoadPackets(Player player) {
		PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
		for (Packet<?> packet : packets) {
			connection.sendPacket(packet);
		}
		loadedForPlayers.put(player, Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			@Override
			public void run() {
				connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, (EntityNMSPlayer)data.getNPC()));
			}
		}, PluginUtils.NPC_REMOVE_DELAY));
	}

	protected void sendDeletePackets(Player player) {
		EntityNMSPlayer npc = (EntityNMSPlayer)data.getNPC();
		PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
		
		if (data.getNameHolo() != null) {
			connection.sendPacket(new PacketPlayOutEntityDestroy(((EntityArmorStand) data.getNameHolo().getEntity()).getId()));
		}
		if (data.getSubtitleHolo() != null) {
			connection.sendPacket(new PacketPlayOutEntityDestroy(((EntityArmorStand) data.getSubtitleHolo().getEntity()).getId()));
		}
		
		Bukkit.getScheduler().cancelTask(loadedForPlayers.get(player));
		loadedForPlayers.remove(player);
	}
}
