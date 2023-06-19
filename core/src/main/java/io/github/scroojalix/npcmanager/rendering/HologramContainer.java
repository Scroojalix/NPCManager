package io.github.scroojalix.npcmanager.rendering;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class HologramContainer {

    private final int id;
    private final UUID uuid;
    private final Location loc;
    private final String formattedText;
    
    public HologramContainer(int id, Location loc, String text) {
        this.id = id;
        this.uuid = UUID.randomUUID();
        this.loc = loc;
        this.formattedText = PluginUtils.format(text);
    }

    // TODO move this, and all other packets to a PacketRegistry class
    @SuppressWarnings("deprecation")
    public LinkedHashSet<PacketContainer> getHologramPackets() {
        final LinkedHashSet<PacketContainer> packets = new LinkedHashSet<>();
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();

        if (PluginUtils.ServerVersion.v1_14_R1.atOrAbove()) {
            PacketContainer createHologram = pm.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
			createHologram.getIntegers().write(0, id);
			createHologram.getUUIDs().write(0, uuid);
			createHologram.getDoubles()
				.write(0, loc.getX())
				.write(1, loc.getY())
				.write(2, loc.getZ());
			createHologram.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
            packets.add(createHologram);
			
            PacketContainer hologramData = pm.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            hologramData.getIntegers().write(0, id);
            if (PluginUtils.ServerVersion.v1_19_R2.atOrAbove()) {
                hologramData.getDataValueCollectionModifier().write(0, getDataWatcherAsList());
            } else {
                hologramData.getWatchableCollectionModifier().write(0, getDataWatcher().getWatchableObjects());
            }
            packets.add(hologramData);
        } else {
            PacketContainer createHologram = pm.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
            createHologram.getIntegers()
            .write(0, id)
            // Set to armor stand
            .write(1, PluginUtils.ServerVersion.v1_13_R1.atOrAbove()? 1 : 30); 
            createHologram.getUUIDs().write(0, uuid);
            createHologram.getDoubles()
                .write(0, loc.getX())
                .write(1, loc.getY())
                .write(2, loc.getZ());

            createHologram.getDataWatcherModifier().write(0, getDataWatcher());
            packets.add(createHologram);
        }

        return packets;
    }

    public WrappedDataWatcher getDataWatcher() {
        WrappedDataWatcher watcher = new WrappedDataWatcher();

		//Serializers
		WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
		WrappedDataWatcher.Serializer booleanSerializer = WrappedDataWatcher.Registry.get(Boolean.class);
        
        //Set invisible
		watcher.setObject(0, byteSerializer, (byte)0x20);
        
        //Set custom name
        if (PluginUtils.ServerVersion.v1_13_R1.atOrAbove()) {
            WrappedDataWatcher.Serializer chatCompSerializer = WrappedDataWatcher.Registry.getChatComponentSerializer(true);
            watcher.setObject(2, chatCompSerializer, Optional.of(
                WrappedChatComponent.fromChatMessage(formattedText)[0]
            ));
        } else {
            WrappedDataWatcher.Serializer stringSerializer = WrappedDataWatcher.Registry.get(String.class);
            watcher.setObject(2, stringSerializer, formattedText);
        }
        
        //Set custom name visible
		watcher.setObject(new WrappedDataWatcher.
			WrappedDataWatcherObject(3, booleanSerializer),
			formattedText != null && !formattedText.isEmpty());
        
        //Set no gravity
		watcher.setObject(new WrappedDataWatcher.
			WrappedDataWatcherObject(5, booleanSerializer),
			true);
        
        //Set armor stand metadata
		watcher.setObject(NPCMain.serverVersion.getArmorStandMetaIndex(),
            byteSerializer,
            (byte)(0x01 | 0x08 | 0x10)); //Small | has no base plate | marker

        return watcher;
    }

    public List<WrappedDataValue> getDataWatcherAsList() {
        final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();
		for(final WrappedWatchableObject entry : getDataWatcher().getWatchableObjects()) {
			if(entry == null) continue;

			final WrappedDataWatcherObject watcherObject = entry.getWatcherObject();
			wrappedDataValueList.add(
				new WrappedDataValue(
					watcherObject.getIndex(),
					watcherObject.getSerializer(),
					entry.getRawValue()
				)
			);
		}
        return wrappedDataValueList;
    }

    public int getID() {
        return this.id;
    }


    public UUID getUUID() {
        return this.uuid;
    }


    public Location getLocation() {
        return this.loc;
    }


    public String getFormattedText() {
        return this.formattedText;
    }
}
