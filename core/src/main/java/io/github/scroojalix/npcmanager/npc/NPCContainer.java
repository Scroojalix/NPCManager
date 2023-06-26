package io.github.scroojalix.npcmanager.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import io.github.scroojalix.npcmanager.npc.interactions.InteractEvent;
import io.github.scroojalix.npcmanager.npc.meta.HandState;
import io.github.scroojalix.npcmanager.npc.meta.NPCMetaInfo;
import io.github.scroojalix.npcmanager.npc.skin.SkinLayer;
import io.github.scroojalix.npcmanager.protocol.NPCLoader;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class NPCContainer {

    //NPC
    private final NPCData npcData;
    private final PlayerInfoData playerInfo;
    private final WrappedDataWatcher dataWatcher;
    private final int entityId;

    //Interact Event
    private InteractEvent interactEvent;

    //Holograms
    //TODO make this into an array to allow multiple lines
    private HologramContainer nameHolo;
    private HologramContainer subtitleHolo;
    
    //Loader Task Info
    private int loaderTaskId;
    private NPCLoader loaderTask;

    public NPCContainer(NPCData data, PlayerInfoData playerInfo) {
        this.npcData = data;
        this.playerInfo = playerInfo;
        this.entityId = PluginUtils.nextEntityId();
        if (PluginUtils.ServerVersion.v1_9_R1.atOrAbove()) {
            this.dataWatcher = generateDataWatcher();
        } else {
            this.dataWatcher = generateLegacyDataWatcher();
        }
    }

    /**
     * Generate Data Watcher for 1.9+ servers
     * @return {@code WrappedDataWatcher} for 1.9+ servers.
     */
    private WrappedDataWatcher generateDataWatcher() {
		final WrappedDataWatcher watcher = new WrappedDataWatcher();
        final WrappedDataWatcher.Serializer byteSerialiser = WrappedDataWatcher.Registry.get(Byte.class);
        final NPCMetaInfo metaInfo = npcData.getTraits().getMetaInfo();

        // Entity Base Metadata
        watcher.setObject(0, byteSerialiser,
            metaInfo.getEntityMetaByte());

        // Pose
        if (PluginUtils.ServerVersion.v1_14_R1.atOrAbove()) {
            watcher.setObject(6,
                WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass()),
                metaInfo.getPose().getNMSValue());
        }

        // Shivering
        if (metaInfo.isShivering() && PluginUtils.ServerVersion.v1_17_R1.atOrAbove()) {
            watcher.setObject(7,
                WrappedDataWatcher.Registry.get(Integer.class),
                140);
        }

        // FIXME this doesn't set the hand state correctly
        // May need to send a packet along with this
        // Hand State
        watcher.setObject(
            HandState.getIndex(), byteSerialiser,
            metaInfo.getHandState().getByteFlag());

        // Active Skin Layers
        watcher.setObject(
            SkinLayer.getSkinLayersByteIndex(),
            byteSerialiser,
            npcData.getTraits().getSkinLayersByte());
        
        return watcher;
    }

    /**
     * Generate data watcher used in 1.8 servers.
     * @return {@code WrappedDataWatcher} for 1.8 servers
     */
    private WrappedDataWatcher generateLegacyDataWatcher() {
        final WrappedDataWatcher watcher = new WrappedDataWatcher();

        // Metadata Settings
        watcher.setObject(0,
            npcData.getTraits().getMetaInfo()
            .getEntityMetaByte());
        
        // Set Active Skin Layers
        watcher.setObject(
            SkinLayer.getSkinLayersByteIndex(),
            npcData.getTraits().getSkinLayersByte());
            
        return watcher;
    }

    // NPC

    public int getNPCEntityID() {
        return this.entityId;
    }

    public NPCData getNPCData() {
        return this.npcData;
    }

    public PlayerInfoData getPlayerInfo() {
        return this.playerInfo;
    }

    public WrappedDataWatcher getDataWatcher() {
        return dataWatcher;
    }

    /**
	 * Sets the Interact Event of this NPC.
	 * @param interactEvent New InteractEvent
	 */
	public void setInteractEvent(InteractEvent interactEvent) {
		this.interactEvent = interactEvent;
	}
	
	/**
	 * @return The Interact Event of this NPC.
	 */
	public InteractEvent getInteractEvent() {
		return interactEvent;
	}

    // Holograms
    public void setNameHolo(HologramContainer hologramContainer) {
        this.nameHolo = hologramContainer;
    }
    
    public void setSubtitleHolo(HologramContainer hologramContainer) {
        this.subtitleHolo = hologramContainer;
    }

    public HologramContainer getNameHolo() {
        return nameHolo;
    }

    public HologramContainer getSubtitleHolo() {
        return subtitleHolo;
    }

    public boolean isNameHoloEnabled() {
        return this.nameHolo != null;
    }

    public boolean isSubtitleHoloEnabled() {
        return this.subtitleHolo != null;
    }

    // Loader Task

    public void setLoaderTask(NPCLoader loaderTask, int loaderTaskId) {
        this.loaderTask = loaderTask;
        this.loaderTaskId = loaderTaskId;
    }

    public int getLoaderTaskID() {
        return this.loaderTaskId;
    }

    public NPCLoader getLoaderTask() {
        return this.loaderTask;
    }
}
