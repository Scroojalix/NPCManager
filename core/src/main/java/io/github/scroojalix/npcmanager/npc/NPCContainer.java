package io.github.scroojalix.npcmanager.npc;

import com.comphenix.protocol.wrappers.PlayerInfoData;

import io.github.scroojalix.npcmanager.rendering.NPCLoader;

public class NPCContainer {

    //NPC
    private NPCData npcData;
    private PlayerInfoData playerInfo;
    private final int entityId;

    //Holograms
    //TODO make this into an array to allow multiple lines
    private HologramContainer nameHolo;
    private HologramContainer subtitleHolo;
    
    //Loader Task Info
    private int loaderTaskId;
    private NPCLoader loaderTask;

    public NPCContainer(NPCData data, int id) {
        this.npcData = data;
        this.entityId = id;
    }

    // NPC

    public int getNPCEntityID() {
        return this.entityId;
    }

    public NPCData getNPCData() {
        return this.npcData;
    }

    public void setNPCData(NPCData npcData) {
        this.npcData = npcData;
    }

    public PlayerInfoData getPlayerInfo() {
        return this.playerInfo;
    }

    public void setPlayerInfo(PlayerInfoData playerInfo) {
        this.playerInfo = playerInfo;
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
