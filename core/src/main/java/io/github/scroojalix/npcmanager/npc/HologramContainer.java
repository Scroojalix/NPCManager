package io.github.scroojalix.npcmanager.npc;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Location;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class HologramContainer {

    private final int id;
    private final UUID uuid;
    private final Location loc;
    private final String formattedText;

    public HologramContainer(Location loc, String text) {
        this.id = PluginUtils.nextEntityId();
        this.uuid = UUID.randomUUID();
        this.loc = loc;
        this.formattedText = PluginUtils.format(text);
    }

    public WrappedDataWatcher getDataWatcher() {
        WrappedDataWatcher watcher = new WrappedDataWatcher();

        //Serializers
        WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);
        WrappedDataWatcher.Serializer booleanSerializer = WrappedDataWatcher.Registry.get(Boolean.class);

        //Set invisible
        watcher.setObject(0, byteSerializer, (byte) 0x20);

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
        if (PluginUtils.ServerVersion.v1_10_R1.atOrAbove()) {
            watcher.setObject(new WrappedDataWatcher.
                WrappedDataWatcherObject(5, booleanSerializer),
                    true);
        }

        //Set armor stand metadata
        watcher.setObject(getArmorStandMetaIndex(),
            byteSerializer,
            (byte)(0x01 | 0x08 | 0x10)); //Small | has no base plate | marker

        return watcher;
    }

    /**
     * For use only in 1.8 servers. This is required, as each field in
     * the data watcher on these versions do not include serialisers
     * @return WrappedDataWatcher for 1.8 servers only
     */
    public WrappedDataWatcher getLegacyDataWatcher() {
        WrappedDataWatcher watcher = new WrappedDataWatcher();

        //Set invisible
        watcher.setObject(0, (byte) 0x20);

        //Set custom name
        watcher.setObject(2, formattedText);

        //Set custom name visible
        watcher.setObject(3, (byte) 0x1);

        //Set armor stand metadata
        watcher.setObject(getArmorStandMetaIndex(),
                (byte) (0x01 | 0x08 | 0x10)); //Small | has no base plate | marker

        return watcher;
    }

    public static int getArmorStandMetaIndex() {
        switch(NPCMain.serverVersion) {
            case v1_8_R2:
            case v1_8_R3:
            case v1_9_R1:
            case v1_9_R2:
                return 10;
            case v1_10_R1:
            case v1_11_R1:
            case v1_12_R1:
            case v1_13_R1:
            case v1_13_R2:
                return 11;
            case v1_14_R1:
                return 13;
            case v1_15_R1:
            case v1_16_R1:
            case v1_16_R2:
            case v1_16_R3:
                return 14;
            default:
                return 15;
        }
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
