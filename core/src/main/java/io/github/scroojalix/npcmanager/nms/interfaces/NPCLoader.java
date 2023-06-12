package io.github.scroojalix.npcmanager.nms.interfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.common.PluginUtils;
import io.github.scroojalix.npcmanager.common.npc.equipment.NPCEquipment;

public class NPCLoader implements Runnable {

	private NPCMain main;
	private NPCContainer npcContainer;
	private ProtocolManager pm;

	private Map<Player, Integer> loadedForPlayers = new HashMap<Player, Integer>();
	private Set<Player> outsideHeadRotationRange = new HashSet<Player>();
	private Set<PacketContainer> loadPackets = new LinkedHashSet<PacketContainer>();

	private double range;
	private boolean hasHeadRotation;
	private double headRotationRange;
	private boolean resetRotation;
	private boolean perfectOrientation;
	
	public NPCLoader(NPCMain main, NPCContainer npcContainer, ProtocolManager protocolManger) {
		this.main = main;
		this.npcContainer = npcContainer;
		this.pm = protocolManger;
		this.range = npcContainer.getNPCData().getTraits().getRange();
		this.hasHeadRotation = npcContainer.getNPCData().getTraits().hasHeadRotation();
		this.headRotationRange = main.getConfig().getDouble("npc-headrotation-range");
		this.resetRotation = main.getConfig().getBoolean("reset-headrotation");
		this.perfectOrientation = main.getConfig().getBoolean("perfect-npc-orientation");

		generatePackets();
	}

	public void clearAllTasks() {
		for (int id : loadedForPlayers.values()) {
			Bukkit.getScheduler().cancelTask(id);
		}
		loadedForPlayers.clear();
		outsideHeadRotationRange.clear();
	}
	
	/**
	 * Generate all packets required to spawn an NPC, and store them in a LinkedHashSet.
	 */
	private void generatePackets() {
		Location loc = npcContainer.getNPCData().getLoc();

		PacketContainer add = pm.createPacket(PacketType.Play.Server.PLAYER_INFO);
		// TODO For older servers
		// packet1.getPlayerInfoAction().write(0, PlayerInfoAction.ADD_PLAYER);
		// For servers that use EnumSet implementation
		add.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
		add.getPlayerInfoDataLists().write(1, Collections.singletonList(npcContainer.getPlayerInfo()));
		loadPackets.add(add);
		
		PacketContainer spawn = pm.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
		spawn.getIntegers().write(0, npcContainer.getNPCEntityID());
		spawn.getUUIDs().write(0, npcContainer.getPlayerInfo().getProfileId());
		spawn.getDoubles()
			.write(0, loc.getX())
			.write(1, loc.getY())
			.write(2, loc.getZ());
		spawn.getBytes()
			.write(0, (byte)(loc.getYaw() * 256.0F / 360.0F))
			.write(1, (byte)(loc.getPitch() * 256.0F / 360.0F));
		loadPackets.add(spawn);

		PacketContainer meta = pm.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		meta.getIntegers().write(0, npcContainer.getNPCEntityID());
		// TODO add skin layers byte to data watcher
		meta.getDataValueCollectionModifier().write(0, new ArrayList<WrappedDataValue>());
		loadPackets.add(meta);

		PacketContainer rotate = pm.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
		rotate.getIntegers().write(0, npcContainer.getNPCEntityID());
		rotate.getBytes().write(0, (byte)(loc.getYaw() * 256.0F / 360.0F));
		loadPackets.add(rotate);


		//TODO Scoreboard team packets

		if (perfectOrientation) {

			PacketContainer orient = pm.createPacket(PacketType.Play.Server.ANIMATION);
			orient.getIntegers()
				.write(0, npcContainer.getNPCEntityID())
				.write(1, 0);			
			
			loadPackets.add(orient);	
		}

		//Holograms
		if (npcContainer.isNameHoloEnabled()) {
			addHologramPackets(
				npcContainer.getNameHoloID(),
				npcContainer.getNameHoloLocation(),
				WrappedChatComponent.fromText(PluginUtils.format(npcContainer.getNPCData().getTraits().getDisplayName()))
			);
		}
		if (npcContainer.isSubtitleHoloEnabled()) {
			addHologramPackets(
				npcContainer.getNameHoloID(),
				npcContainer.getNameHoloLocation(),
				WrappedChatComponent.fromText(PluginUtils.format(npcContainer.getNPCData().getTraits().getDisplayName()))
			);
		}

		//Equipment
		if (npcContainer.getNPCData().getTraits().getEquipment(false) != null) {
			final List<Pair<ItemSlot, ItemStack>> equipmentList = new ArrayList<>();
			NPCEquipment equipment = npcContainer.getNPCData().getTraits().getEquipment(false);
			if (equipment.getMainhandItem() != null) {
				equipmentList.add(new Pair<>(ItemSlot.MAINHAND, equipment.getMainhandItem()));
			}
			if (equipment.getOffhandItem() != null) {
				equipmentList.add(new Pair<>(ItemSlot.OFFHAND, equipment.getOffhandItem()));
			}
			if (equipment.getHelmet() != null) {
				equipmentList.add(new Pair<>(ItemSlot.HEAD, equipment.getHelmet()));
			}
			if (equipment.getChestplate() != null) {
				equipmentList.add(new Pair<>(ItemSlot.CHEST, equipment.getChestplate()));
			}
			if (equipment.getLeggings() != null) {
				equipmentList.add(new Pair<>(ItemSlot.LEGS, equipment.getLeggings()));
			}
			if (equipment.getBoots() != null) {
				equipmentList.add(new Pair<>(ItemSlot.FEET, equipment.getBoots()));
			}
			if (!equipmentList.isEmpty()) {

				PacketContainer equipmentPacket = pm.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
				equipmentPacket.getIntegers().write(0, npcContainer.getNPCEntityID());
				equipmentPacket.getSlotStackPairLists().write(0, equipmentList);

				loadPackets.add(equipmentPacket);
			}
		}

	}

	private void addHologramPackets(int id, Location loc, WrappedChatComponent text) {
		PacketContainer addHologram = pm.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
		addHologram.getIntegers().write(0, id);
		addHologram.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
		addHologram.getDoubles()
			.write(0, loc.getX())
			.write(1, loc.getY())
			.write(2, loc.getZ());
		// FIXME do i need to set object data here?

		PacketContainer hologramData = pm.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		hologramData.getIntegers().write(0, id);

		// Watcher Needs:
		// Index	|	value
		//	0		|   0x20
		//	2		|	Custom Name
		//  3 		| 	name not null AND name not empty
		//  5		|	1
		//	15		|	0x01 | 0x08 | 0x10

		WrappedDataWatcher watcher = new WrappedDataWatcher();
		watcher.setObject(0, 0x20);
		watcher.setObject(2, text);
		watcher.setObject(3, text != null && !text.toString().isEmpty());
		watcher.setObject(5, true);
		watcher.setObject(15, 0x01 | 0x08 | 0x10);

		hologramData.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

		loadPackets.add(addHologram);
		loadPackets.add(hologramData);
	}
	
	/**
	 * Method that loops to update NPC's.
	 */
	public void run() {
		Location loc = npcContainer.getNPCData().getLoc();

		if (loc != null) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getWorld().getName().equalsIgnoreCase(loc.getWorld().getName())) {
					double distance = calculateDistance(loc, player.getLocation());
					if (distance <= range) {
						if (!loadedForPlayers.containsKey(player)) {
							sendLoadPackets(player);
						}
						if (hasHeadRotation) {
							if (distance <= headRotationRange && distance > 0) {
								lookInDirection(player);
								if (outsideHeadRotationRange.contains(player)) {
									outsideHeadRotationRange.remove(player);
								}
							} else if (resetRotation) {
								if (!outsideHeadRotationRange.contains(player)) {
									resetLookDirection(player);
									outsideHeadRotationRange.add(player);
								}
							}
						}
					} else {
						if (loadedForPlayers.containsKey(player)) {
							sendDeletePackets(player);
						}
					}
				} else if (loadedForPlayers.containsKey(player)) loadedForPlayers.remove(player);
			}
		} else main.npc.removeNPC(npcContainer.getNPCData().getName(), true);
	}
	
	/**
	 * Makes an NPC look towards a player.
	 * @param player The player to look at.
	 */
	private void lookInDirection(Player player) {
		Vector difference = player.getLocation().subtract(npcContainer.getNPCData().getLoc()).toVector().normalize();
        float degrees = (float) Math.toDegrees(Math.atan2(difference.getZ(), difference.getX()) - Math.PI / 2);
        byte angle = (byte) Math.floor((degrees * 256.0F) / 360.0F);
        Vector height = npcContainer.getNPCData().getLoc().subtract(player.getLocation()).toVector().normalize();
        byte pitch = (byte) Math.floor((Math.toDegrees(Math.atan(height.getY())) * 256.0F) / 360.0F);

		setLookDirection(player, angle, pitch);
	}
	
	/**
	 * Reset an NPC's head rotation for a player.
	 * @param player The player to reset head rotation for.
	 */
	private void resetLookDirection(Player player) {
		byte yaw = (byte) (npcContainer.getNPCData().getLoc().getYaw() * 255 / 360);
        byte pitch = (byte) (npcContainer.getNPCData().getLoc().getPitch() * 255 / 360);

		setLookDirection(player, yaw, pitch);		
	}

	private void setLookDirection(Player player, byte yaw, byte pitch) {
		PacketContainer rotate = pm.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
		rotate.getIntegers().write(0, npcContainer.getNPCEntityID());
		rotate.getBytes().write(0, yaw);

		PacketContainer move = pm.createPacket(PacketType.Play.Server.ENTITY_LOOK);
		move.getIntegers().write(0, npcContainer.getNPCEntityID());
		// TODO may not need this
		move.getShorts()
			.write(0, (short)0)
			.write(1, (short)0)
			.write(2, (short)0);
		move.getBytes()
			.write(0, yaw)
			.write(1, pitch);
		move.getBooleans()
			.write(0, true)
			.write(1, true)
			.write(2, false);
		
		pm.sendServerPacket(player, rotate);
		pm.sendServerPacket(player, move);
	}
	
	/**
	 * Send packets that spawn the NPC to a player.
	 * @param player The player to send packets to.
	 */
	private void sendLoadPackets(Player player) {
		for (PacketContainer container : loadPackets) {
			pm.sendServerPacket(player, container);
		}
		loadedForPlayers.put(player, Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			@Override
			public void run() {
				PacketContainer remove = pm.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
				remove.getUUIDLists().write(0, Collections.singletonList(npcContainer.getNPCData().getUUID()));
				pm.sendServerPacket(player, remove);
			}
		}, PluginUtils.NPC_REMOVE_DELAY));
	}

	/**
	 * Send packets to a player to delete/hide an NPC.
	 * @param player
	 */
	private void sendDeletePackets(Player player) {
		PacketContainer removeEntities = pm.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(npcContainer.getNPCEntityID());

		if (npcContainer.isNameHoloEnabled()) {
			ids.add(npcContainer.getNameHoloID());
		}
		if (npcContainer.isSubtitleHoloEnabled()) {
			ids.add(npcContainer.getSubtitleHoloID());
		}

		removeEntities.getIntLists().write(0, ids);

		PacketContainer removeInfo = pm.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
		removeInfo.getUUIDLists().write(0, Collections.singletonList(npcContainer.getNPCData().getUUID()));
		
		
		pm.sendServerPacket(player, removeEntities);
		pm.sendServerPacket(player, removeInfo);

		Bukkit.getScheduler().cancelTask(loadedForPlayers.get(player));
		loadedForPlayers.remove(player);
	}
	
	/**
	 * Calculate the distance between two locations.
	 * @param loc1 Location of NPC.
	 * @param loc2 Location of Player.
	 * @return The distance between an NPC and a Player.
	 */
	private double calculateDistance(Location loc1, Location loc2) {
        return Math.sqrt(Math.pow(loc1.getX() - loc2.getX(), 2) + Math.pow(loc1.getY() - loc2.getY(), 2) + Math.pow(loc1.getZ() - loc2.getZ(), 2));
    }
}
