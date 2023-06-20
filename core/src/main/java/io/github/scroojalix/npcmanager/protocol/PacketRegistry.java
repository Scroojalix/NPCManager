package io.github.scroojalix.npcmanager.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.npc.HologramContainer;
import io.github.scroojalix.npcmanager.npc.NPCContainer;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public final class PacketRegistry {

    // PACKET CONSTRUCTORS

    public static final Packet<NPCContainer> NPC_ADD_INFO = (NPCContainer container) -> {
        PacketContainer add = createPacket(PacketType.Play.Server.PLAYER_INFO);
        if (PluginUtils.ServerVersion.v1_19_R2.atOrAbove()) {
            add.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
            add.getPlayerInfoDataLists().write(1, Collections.singletonList(container.getPlayerInfo()));
        } else {
            add.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
            add.getPlayerInfoDataLists().write(0, Collections.singletonList(container.getPlayerInfo()));
        }
        return add;
    };

    @SuppressWarnings("deprecation")
    public static final Packet<NPCContainer> NPC_REMOVE_INFO = (NPCContainer container) -> {
		PacketContainer removeInfo;
		if (PluginUtils.ServerVersion.v1_19_R2.atOrAbove()) {
			removeInfo = createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
			removeInfo.getUUIDLists().write(0, Collections.singletonList(container.getNPCData().getUUID()));
		} else {
			removeInfo = createPacket(PacketType.Play.Server.PLAYER_INFO);
			removeInfo.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
			removeInfo.getPlayerInfoDataLists().write(0, Collections.singletonList(container.getPlayerInfo()));
		}
		return removeInfo;
    };

    public static final Packet<NPCContainer> NPC_SPAWN = (NPCContainer container) -> {
        Location npcLoc = container.getNPCData().getLoc();

        PacketContainer spawn = createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        spawn.getIntegers().write(0, container.getNPCEntityID());
        spawn.getUUIDs().write(0, container.getPlayerInfo().getProfileId());
        if (PluginUtils.ServerVersion.v1_9_R1.atOrAbove()) {
            spawn.getDoubles()
                    .write(0, npcLoc.getX())
                    .write(1, npcLoc.getY())
                    .write(2, npcLoc.getZ());
        } else {
            spawn.getIntegers()
                    .write(1, PluginUtils.get1_8LocInt(npcLoc.getX()))
                    .write(2, PluginUtils.get1_8LocInt(npcLoc.getY()))
                    .write(3, PluginUtils.get1_8LocInt(npcLoc.getZ()));
        }
        spawn.getBytes()
                .write(0, PluginUtils.toByteAngle(npcLoc.getYaw()))
                .write(1, PluginUtils.toByteAngle(npcLoc.getPitch()));
        return spawn;
    };

    public static final Packet<NPCContainer> NPC_UPDATE_METADATA = (NPCContainer container) -> {
        PacketContainer meta = createPacket(PacketType.Play.Server.ENTITY_METADATA);
        meta.getIntegers().write(0, container.getNPCEntityID());

        if (PluginUtils.ServerVersion.v1_19_R2.atOrAbove()) {
            final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();
            wrappedDataValueList.add(new WrappedDataValue(
                    NPCMain.serverVersion.getSkinLayersByteIndex(),
                    WrappedDataWatcher.Registry.get(Byte.class),
                    container.getNPCData().getTraits().getSkinLayersByte()));

            meta.getDataValueCollectionModifier().write(0, wrappedDataValueList);
        } else {
            WrappedDataWatcher watcher = new WrappedDataWatcher();
            if (PluginUtils.ServerVersion.v1_9_R1.atOrAbove()) {
                watcher.setObject(
                        NPCMain.serverVersion.getSkinLayersByteIndex(),
                        WrappedDataWatcher.Registry.get(Byte.class),
                        container.getNPCData().getTraits().getSkinLayersByte());
            } else {
                watcher.setObject(
                        NPCMain.serverVersion.getSkinLayersByteIndex(),
                        container.getNPCData().getTraits().getSkinLayersByte());
            }

            meta.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        }
        return meta;
    };

    public static final Packet<NPCContainer> NPC_DESTROY = (NPCContainer container) -> {
        PacketContainer removeEntities = createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(container.getNPCEntityID());
		if (container.isNameHoloEnabled()) {
			ids.add(container.getNameHolo().getID());
		}
		if (container.isSubtitleHoloEnabled()) {
			ids.add(container.getSubtitleHolo().getID());
		}

		if (PluginUtils.ServerVersion.v1_17_R1.atOrAbove()) {
			removeEntities.getIntLists().write(0, ids);
		} else {
			removeEntities.getIntegerArrays().write(0, ids.stream().mapToInt(Integer::intValue).toArray());
		}
        return removeEntities;
    };

    public static final PacketList<NPCContainer> NPC_RESET_HEAD_ROTATION = (NPCContainer container) -> {
        byte yaw = PluginUtils.toByteAngle(container.getNPCData().getLoc().getYaw());
        byte pitch = PluginUtils.toByteAngle(container.getNPCData().getLoc().getPitch());

        return getHeadRotationPackets(container, yaw, pitch);

    };

    public static final Packet<NPCContainer> NPC_PLAY_ANIMATION = (NPCContainer container) -> {
        PacketContainer orient = createPacket(PacketType.Play.Server.ANIMATION);
        orient.getIntegers()
                .write(0, container.getNPCEntityID())
                .write(1, 0);

        return orient;
    };

    @SuppressWarnings("deprecation")
    public static final PacketList<HologramContainer> HOLOGRAM_CREATE = (HologramContainer holo) -> {
        final LinkedHashSet<PacketContainer> packets = new LinkedHashSet<>();

        if (PluginUtils.ServerVersion.v1_14_R1.atOrAbove()) {
            PacketContainer createHologram = createPacket(PacketType.Play.Server.SPAWN_ENTITY);
            createHologram.getIntegers().write(0, holo.getID());
            createHologram.getUUIDs().write(0, holo.getUUID());
            createHologram.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
            createHologram.getDoubles()
                    .write(0, holo.getLocation().getX())
                    .write(1, holo.getLocation().getY())
                    .write(2, holo.getLocation().getZ());
            packets.add(createHologram);

            PacketContainer hologramData = createPacket(PacketType.Play.Server.ENTITY_METADATA);
            hologramData.getIntegers().write(0, holo.getID());
            if (PluginUtils.ServerVersion.v1_19_R2.atOrAbove()) {
                hologramData.getDataValueCollectionModifier().write(0, holo.getDataWatcherAsList());
            } else {
                hologramData.getWatchableCollectionModifier().write(0, holo.getDataWatcher().getWatchableObjects());
            }
            packets.add(hologramData);
        } else {
            PacketContainer createHologram = createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
            createHologram.getIntegers()
                .write(0, holo.getID())
                // Set to armor stand
                .write(1, PluginUtils.ServerVersion.v1_13_R1.atOrAbove() ? 1 : 30);

            if (PluginUtils.ServerVersion.v1_9_R1.atOrAbove()) {
                createHologram.getUUIDs().write(0, holo.getUUID());
                createHologram.getDoubles()
                    .write(0, holo.getLocation().getX())
                    .write(1, holo.getLocation().getY())
                    .write(2, holo.getLocation().getZ());
                createHologram.getDataWatcherModifier().write(0, holo.getDataWatcher());
            } else {
                createHologram.getIntegers()
                    .write(2, PluginUtils.get1_8LocInt(holo.getLocation().getX()))
                    .write(3, PluginUtils.get1_8LocInt(holo.getLocation().getY()))
                    .write(4, PluginUtils.get1_8LocInt(holo.getLocation().getZ()));
                createHologram.getDataWatcherModifier().write(0, holo.getLegacyDataWatcher());
            }

            packets.add(createHologram);
        }

        return packets;
    };

    public static final PacketList<NPCContainer> NPC_SET_EQUIPMENT = (NPCContainer container) -> {
        final List<Pair<ItemSlot, ItemStack>> equipmentList = container.getNPCData()
            .getTraits().getEquipment(false).getSlotStackPairList();
        if (equipmentList.isEmpty()) return new LinkedHashSet<>();

        final LinkedHashSet<PacketContainer> packets = new LinkedHashSet<>();
        if (PluginUtils.ServerVersion.v1_16_R1.atOrAbove()) {
            PacketContainer equipmentPacket = createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
            equipmentPacket.getIntegers().write(0, container.getNPCEntityID());
            equipmentPacket.getSlotStackPairLists().write(0, equipmentList);
            packets.add(equipmentPacket);
        } else {
            for (Pair<ItemSlot, ItemStack> pair : equipmentList) {
                PacketContainer equipmentPacket = createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
                equipmentPacket.getIntegers().write(0, container.getNPCEntityID());
                if (PluginUtils.ServerVersion.v1_9_R1.atOrAbove()) {
                    equipmentPacket.getItemSlots().write(0, pair.getFirst());
                } else {
                    int equipmentSlot;
                    switch (pair.getFirst()) {
                        case MAINHAND: equipmentSlot = 0; break;
                        case FEET: equipmentSlot = 1; break;
                        case LEGS: equipmentSlot = 2; break;
                        case CHEST: equipmentSlot = 3; break;
                        case HEAD: equipmentSlot = 4; break;
                        default: continue;
                    }
                    equipmentPacket.getIntegers().write(1, equipmentSlot);
                }
                equipmentPacket.getItemModifier().write(0, pair.getSecond());
                packets.add(equipmentPacket);
            }
        }
        return packets;
    };

    public static final StaticPacket SCOREBOARD_CREATE = () -> {
        if (PluginUtils.ServerVersion.v1_17_R1.atOrAbove()) {
            PacketContainer createTeam = createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
            createTeam.getStrings().write(0, PluginUtils.NPC_SCOREBOARD_TEAM_NAME);
            InternalStructure struct = createTeam.getOptionalStructures().read(0).get();
            struct.getStrings()
                .write(0, "never") // Visibility
                .write(1, "never"); // Collision
            createTeam.getOptionalStructures().write(0, Optional.of(struct));
            return createTeam;
        } else {
            PacketContainer createTeam = createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
            int teamSettingIndex = PluginUtils.ServerVersion.v1_13_R1.atOrAbove() ? 1 : 4;
            createTeam.getStrings()
                .write(0, PluginUtils.NPC_SCOREBOARD_TEAM_NAME)
                .write(teamSettingIndex, "never");
            if (PluginUtils.ServerVersion.v1_9_R1.atOrAbove()) {
                createTeam.getStrings().write(teamSettingIndex + 1, "never");
            }
            return createTeam;
        }
    };

    public static final Packet<NPCContainer> SCOREBOARD_ADD_NPC = (NPCContainer container) -> {
        if (PluginUtils.ServerVersion.v1_17_R1.atOrAbove()) {
            PacketContainer addNPCToTeam = createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
            addNPCToTeam.getStrings().write(0, PluginUtils.NPC_SCOREBOARD_TEAM_NAME);
            addNPCToTeam.getIntegers().write(0, 3);
            addNPCToTeam.getModifier().write(2, Collections.singletonList(
                container.getPlayerInfo().getProfile().getName()));
            return addNPCToTeam;
        } else {
            PacketContainer addNPCToTeam = createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
            addNPCToTeam.getStrings().write(0, PluginUtils.NPC_SCOREBOARD_TEAM_NAME);
            int teamPacketModeIndex = PluginUtils.ServerVersion.v1_13_R1.atOrAbove() ? 0 : 1;
            addNPCToTeam.getIntegers().write(teamPacketModeIndex, 3);
            addNPCToTeam.getModifier().write(PluginUtils.ServerVersion.v1_9_R1.atOrAbove() ? 7 : 6,
                Collections.singletonList(
                    container.getPlayerInfo().getProfile().getName()));
            return addNPCToTeam;
        }
    };

    // DYNAMIC PACKETS

    public static LinkedHashSet<PacketContainer> getHeadRotationPackets(NPCContainer container, byte yaw, byte pitch) {
        final LinkedHashSet<PacketContainer> packets = new LinkedHashSet<>();
        
        PacketContainer rotate = createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        rotate.getIntegers().write(0, container.getNPCEntityID());
        rotate.getBytes().write(0, yaw);

        PacketContainer look = createPacket(PacketType.Play.Server.ENTITY_LOOK);
		look.getIntegers().write(0, container.getNPCEntityID());
		int yawIndex = PluginUtils.ServerVersion.v1_9_R1.atOrAbove() ? 0 : 3;
		look.getBytes()
				.write(yawIndex, yaw)
				.write(yawIndex + 1, pitch);
		look.getBooleans()
				.write(0, true)
				.write(1, true);

        packets.add(rotate);
        packets.add(look);
        
        return packets;
    }

    // UTILITY FUNCTIONS

    private static PacketContainer createPacket(PacketType type) {
        return ProtocolLibrary.getProtocolManager().createPacket(type);
    }

    // INTERFACES

    public interface StaticPacket {
        public PacketContainer get();
    }

    public interface Packet<T> {
        public PacketContainer get(T container);
    }

    public interface PacketList<T> {
        public LinkedHashSet<PacketContainer> get(T container);
    }
}
