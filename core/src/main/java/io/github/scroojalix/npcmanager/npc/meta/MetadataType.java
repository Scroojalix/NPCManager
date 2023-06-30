package io.github.scroojalix.npcmanager.npc.meta;

import javax.annotation.Nullable;

import io.github.scroojalix.npcmanager.utils.PluginUtils.ServerVersion;

public interface MetadataType {
    
    /**
     * Return the version this metadata flag was added.
     * @return version that this flag was added to Minecraft.
     */
    ServerVersion getVersionAdded();

    /**
     * Return the parent metadata node for this metadata flag.
     * Will return null if this metadata flag does not have a parent.
     * @return the parent metadata flag for this metadata flag.
     */
    @Nullable MetadataType getParent();

    /**
     * The children of this metadata sub group.
     * @return
     */
    MetadataType[] getChildren();
}
