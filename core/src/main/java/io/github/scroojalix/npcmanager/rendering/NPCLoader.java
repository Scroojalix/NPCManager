package io.github.scroojalix.npcmanager.rendering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.npc.equipment.NPCEquipment;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

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

	private Location npcLoc;
	
	public NPCLoader(NPCMain main, NPCContainer npcContainer, ProtocolManager protocolManger) {
		this.main = main;
		this.npcContainer = npcContainer;
		this.pm = protocolManger;
		this.range = npcContainer.getNPCData().getTraits().getRange();
		this.hasHeadRotation = npcContainer.getNPCData().getTraits().hasHeadRotation();
		this.headRotationRange = main.getConfig().getDouble("npc-headrotation-range");
		this.resetRotation = main.getConfig().getBoolean("reset-headrotation");
		this.perfectOrientation = main.getConfig().getBoolean("perfect-npc-orientation");

		npcLoc = npcContainer.getNPCData().getLoc();

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
		// FIXME may not need to send all of these packets every time.

		PacketContainer add = pm.createPacket(PacketType.Play.Server.PLAYER_INFO);
		if (PluginUtils.ServerVersion.v1_19_R2.atOrAbove()) {
			add.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
			add.getPlayerInfoDataLists().write(1, Collections.singletonList(npcContainer.getPlayerInfo()));
		} else {
			add.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
			add.getPlayerInfoDataLists().write(0, Collections.singletonList(npcContainer.getPlayerInfo()));
		}
		loadPackets.add(add);
		
		PacketContainer spawn = pm.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
		spawn.getIntegers().write(0, npcContainer.getNPCEntityID());
		spawn.getUUIDs().write(0, npcContainer.getPlayerInfo().getProfileId());
		spawn.getDoubles()
			.write(0, npcLoc.getX())
			.write(1, npcLoc.getY())
			.write(2, npcLoc.getZ());
		spawn.getBytes()
			.write(0, toByteAngle(npcLoc.getYaw()))
			.write(1, toByteAngle(npcLoc.getPitch()));
		loadPackets.add(spawn);

		PacketContainer meta = pm.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		meta.getIntegers().write(0, npcContainer.getNPCEntityID());

		if (PluginUtils.ServerVersion.v1_19_R2.atOrAbove()) {
			final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();
			wrappedDataValueList.add(new WrappedDataValue(
				NPCMain.serverVersion.getSkinLayersByteIndex(),
				WrappedDataWatcher.Registry.get(Byte.class), 
				npcContainer.getNPCData().getTraits().getSkinLayersByte()));
		
			meta.getDataValueCollectionModifier().write(0, wrappedDataValueList);
		} else {
			WrappedDataWatcher watcher = new WrappedDataWatcher();
			watcher.setObject(
				NPCMain.serverVersion.getSkinLayersByteIndex(),
				WrappedDataWatcher.Registry.get(Byte.class), 
				npcContainer.getNPCData().getTraits().getSkinLayersByte());
			
			meta.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
		}
		loadPackets.add(meta);

		PacketContainer rotate = pm.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
		rotate.getIntegers().write(0, npcContainer.getNPCEntityID());
		rotate.getBytes().write(0, toByteAngle(npcLoc.getYaw()));
		loadPackets.add(rotate);

		//Scoreboards
		// TODO these packets don't need to be sent every time an NPC is loaded

		if (PluginUtils.ServerVersion.v1_17_R1.atOrAbove()) {
			PacketContainer createTeam = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
			createTeam.getStrings().write(0, PluginUtils.NPC_SCOREBOARD_TEAM_NAME);
			InternalStructure struct = createTeam.getOptionalStructures().read(0).get();
			struct.getStrings()
				.write(0, "never")  // Visibility
				.write(1, "never"); // Collision
			createTeam.getOptionalStructures().write(0, Optional.of(struct));
			loadPackets.add(createTeam);

			PacketContainer addNPCToTeam = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
			addNPCToTeam.getStrings().write(0, PluginUtils.NPC_SCOREBOARD_TEAM_NAME);
			addNPCToTeam.getIntegers().write(0, 3);
			addNPCToTeam.getModifier().write(2, Collections.singletonList(
				npcContainer.getPlayerInfo().getProfile().getName()
			));
			loadPackets.add(addNPCToTeam);
		} else {
			PacketContainer createTeam = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
			createTeam.getStrings()
				.write(0, PluginUtils.NPC_SCOREBOARD_TEAM_NAME)
				.write(1, "never")
				.write(2, "never");
			loadPackets.add(createTeam);

			PacketContainer addNPCToTeam = pm.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
			addNPCToTeam.getStrings().write(0, PluginUtils.NPC_SCOREBOARD_TEAM_NAME);
			addNPCToTeam.getIntegers().write(0, 3);
			addNPCToTeam.getModifier().write(7, Collections.singletonList(
				npcContainer.getPlayerInfo().getProfile().getName()
			));
			loadPackets.add(addNPCToTeam);
		}

		if (perfectOrientation) {

			PacketContainer orient = pm.createPacket(PacketType.Play.Server.ANIMATION);
			orient.getIntegers()
				.write(0, npcContainer.getNPCEntityID())
				.write(1, 0);			
			
			loadPackets.add(orient);
		}

		//Holograms
		if (npcContainer.isNameHoloEnabled()) {
			addHologramPackets(npcContainer.getNameHolo());
		}
		if (npcContainer.isSubtitleHoloEnabled()) {
			addHologramPackets(npcContainer.getSubtitleHolo());
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

	private void addHologramPackets(HologramContainer holo) {
		PacketContainer addHologram = pm.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
		addHologram.getIntegers()
			.write(0, holo.getID())
			.write(1, 1);
		addHologram.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
		addHologram.getUUIDs().write(0, holo.getUUID());
		addHologram.getDoubles()
			.write(0, holo.getLocation().getX())
			.write(1, holo.getLocation().getY())
			.write(2, holo.getLocation().getZ());

		PacketContainer hologramData = pm.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		hologramData.getIntegers().write(0, holo.getID());

		if (PluginUtils.ServerVersion.v1_19_R2.atOrAbove()) {
			hologramData.getDataValueCollectionModifier().write(0, holo.getDataWatcherAsList());
		} else {
			hologramData.getWatchableCollectionModifier().write(0, holo.getDataWatcher().getWatchableObjects());
		}

		loadPackets.add(addHologram);
		loadPackets.add(hologramData);
	}
	
	/**
	 * Method that loops to update NPC's.
	 */
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getWorld().getName().equalsIgnoreCase(npcLoc.getWorld().getName())) {
				double distance = calculateDistance(npcLoc, player.getLocation());
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
				} else if (loadedForPlayers.containsKey(player)) {
					sendDeletePackets(player);
					Bukkit.getScheduler().cancelTask(loadedForPlayers.get(player));
					loadedForPlayers.remove(player);
				}
			} else if (loadedForPlayers.containsKey(player)) loadedForPlayers.remove(player);
		}
	}
	
	/**
	 * Makes an NPC look towards a player.
	 * @param player The player to look at.
	 */
	private void lookInDirection(Player player) {
		Vector difference = player.getLocation().clone().subtract(npcLoc).toVector().normalize();
        float degrees = (float) Math.toDegrees(Math.atan2(difference.getZ(), difference.getX()) - Math.PI / 2);
        byte angle = toByteAngle(degrees);
        Vector height = npcLoc.clone().subtract(player.getLocation()).toVector().normalize();
		byte pitch = toByteAngle((float)Math.toDegrees(Math.atan(height.getY())));

		setLookDirection(player, angle, pitch);
	}
	
	/**
	 * Reset an NPC's head rotation for a player.
	 * @param player The player to reset head rotation for.
	 */
	private void resetLookDirection(Player player) {
		byte yaw = toByteAngle(npcLoc.getYaw());
        byte pitch = toByteAngle(npcLoc.getPitch());
		setLookDirection(player, yaw, pitch);
	}

	private void setLookDirection(Player player, byte yaw, byte pitch) {
		PacketContainer rotate = pm.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
		rotate.getIntegers().write(0, npcContainer.getNPCEntityID());
		rotate.getBytes().write(0, yaw);

		PacketContainer move = pm.createPacket(PacketType.Play.Server.ENTITY_LOOK);
		move.getIntegers().write(0, npcContainer.getNPCEntityID());
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
				PacketContainer remove = getPlayerInfoRemovePacket();
				pm.sendServerPacket(player, remove);
			}
		}, PluginUtils.NPC_REMOVE_DELAY));
	}

	/**
	 * Send packets to a player to delete/hide an NPC.
	 * @param player
	 */
	public void sendDeletePackets(Player player) {
		PacketContainer removeEntities = pm.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(npcContainer.getNPCEntityID());
		if (npcContainer.isNameHoloEnabled()) {
			ids.add(npcContainer.getNameHolo().getID());
		}
		if (npcContainer.isSubtitleHoloEnabled()) {
			ids.add(npcContainer.getSubtitleHolo().getID());
		}

		if (PluginUtils.ServerVersion.v1_17_R1.atOrAbove()) {
			removeEntities.getIntLists().write(0, ids);
		} else {
			removeEntities.getIntegerArrays().write(0, ids.stream().mapToInt(Integer::intValue).toArray());
		}

		PacketContainer removeInfo = getPlayerInfoRemovePacket();

		pm.sendServerPacket(player, removeEntities);
		pm.sendServerPacket(player, removeInfo);
	}

	@SuppressWarnings("deprecation")
	private PacketContainer getPlayerInfoRemovePacket() {
		PacketContainer removeInfo;
		if (PluginUtils.ServerVersion.v1_19_R2.atOrAbove()) {
			removeInfo = pm.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
			removeInfo.getUUIDLists().write(0, Collections.singletonList(npcContainer.getNPCData().getUUID()));
		} else {
			removeInfo = pm.createPacket(PacketType.Play.Server.PLAYER_INFO);
			removeInfo.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
			removeInfo.getPlayerInfoDataLists().write(0, Collections.singletonList(npcContainer.getPlayerInfo()));
		}
		return removeInfo;
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

	private byte toByteAngle(float angle) {
		return (byte) (angle * 256.0F / 360.0F);
	}
}
