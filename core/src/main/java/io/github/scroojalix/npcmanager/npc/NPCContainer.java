package io.github.scroojalix.npcmanager.npc;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.npc.interactions.InteractEvent;
import io.github.scroojalix.npcmanager.npc.meta.NPCMetaInfo;
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
        this.dataWatcher = generateDataWatcher();
    }

    private WrappedDataWatcher generateDataWatcher() {
        //Skin layers and pose
		WrappedDataWatcher watcher = new WrappedDataWatcher();
		if (PluginUtils.ServerVersion.v1_9_R1.atOrAbove()) {
			// Data values need serialisers
            WrappedDataWatcher.Serializer byteSerialiser = WrappedDataWatcher.Registry.get(Byte.class);

            if (npcData.getTraits().getMetaInfo() != null) {
                NPCMetaInfo poseInfo = npcData.getTraits().getMetaInfo();
                // Pose settings
                watcher.setObject(
                    0,
                    byteSerialiser,
                    poseInfo.getEntityMetaByte());

                watcher.setObject(
                    6,
                    WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass()),
                    poseInfo.getPose().getNMSValue());

                if (poseInfo.isShivering()) {
                    watcher.setObject(
                        7,
                        WrappedDataWatcher.Registry.get(Integer.class),
                        140);
                }

                // FIXME this doesn't set the hand state correctly
                watcher.setObject(
                    8, byteSerialiser,
                    poseInfo.getHandState().getByteFlag());
            }

			// Active Skin Layers
			watcher.setObject(
				NPCMain.serverVersion.getSkinLayersByteIndex(),
                byteSerialiser,
				npcData.getTraits().getSkinLayersByte());
		} else {
			// Serialisers not needed
            // TODO indexes may be incorrect
            if (npcData.getTraits().getMetaInfo() != null) {
                NPCMetaInfo poseInfo = npcData.getTraits().getMetaInfo();
                // Pose settings
                watcher.setObject(
                    0,
                    poseInfo.getEntityMetaByte());

                watcher.setObject(
                    6,
                    poseInfo.getPose().getNMSValue());

                if (poseInfo.isShivering()) {
                    watcher.setObject(
                        7,
                        140);
                }

                watcher.setObject(
                    8,
                    poseInfo.getHandState().getByteFlag());
            }

			// Active Skin Layers
			watcher.setObject(
				NPCMain.serverVersion.getSkinLayersByteIndex(),
				npcData.getTraits().getSkinLayersByte());
		}
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
