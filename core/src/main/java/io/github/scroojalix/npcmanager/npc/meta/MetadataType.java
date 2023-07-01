package io.github.scroojalix.npcmanager.npc.meta;

import javax.annotation.Nullable;

import io.github.scroojalix.npcmanager.utils.PluginUtils.ServerVersion;

/**
 * Must only be implemented by enums.
 */
public interface MetadataType {
    
    /**
     * Return the version this metadata flag was added.
     * @return version that this flag was added to Minecraft.
     */
    default ServerVersion getVersionAdded() {
        return ServerVersion.v1_8_R2;
    }

    /**
     * Return the parent metadata node for this metadata flag.
     * Will return null if this metadata flag does not have a parent.
     * @return the parent metadata flag for this metadata flag.
     */
    default @Nullable MetadataType getParent() {
        return null;
    }
}
