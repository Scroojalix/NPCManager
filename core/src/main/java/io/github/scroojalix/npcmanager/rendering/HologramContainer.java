package io.github.scroojalix.npcmanager.rendering;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Location;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class HologramContainer {

    private final int id;
    private final UUID uuid;
    private final Location loc;
    private final WrappedChatComponent text;
    
    public HologramContainer(int id, Location loc, String text) {
        this.id = id;
        this.uuid = UUID.randomUUID();
        this.loc = loc;
        this.text = WrappedChatComponent.fromText(PluginUtils.format(text));
    }

    public WrappedDataWatcher getDataWatcher() {
        WrappedDataWatcher watcher = new WrappedDataWatcher();

		//Serializers
		WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
		WrappedDataWatcher.Serializer chatCompSerializer = WrappedDataWatcher.Registry.getChatComponentSerializer(true);
		WrappedDataWatcher.Serializer booleanSerializer = WrappedDataWatcher.Registry.get(Boolean.class);

		watcher.setObject(0, byteSerializer, (byte)0x20);
		watcher.setObject(2, chatCompSerializer, Optional.of(text.getHandle()));
		watcher.setObject(new WrappedDataWatcher.
			WrappedDataWatcherObject(3, booleanSerializer),
			text != null && !text.toString().isEmpty());
		watcher.setObject(new WrappedDataWatcher.
			WrappedDataWatcherObject(5, booleanSerializer),
			true);
		watcher.setObject(15, byteSerializer, (byte)(0x01 | 0x08 | 0x10));

        return watcher;
    }

    public List<WrappedDataValue> getDataWatcherAsList() {
        WrappedDataWatcher watcher = getDataWatcher();
        final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();
		for(final WrappedWatchableObject entry : watcher.getWatchableObjects()) {
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


    public WrappedChatComponent getFormattedText() {
        return this.text;
    }
}
